//necessary to create ArrayList of Appliances
import java.util.ArrayList;
//necessary for user input (EXTENSION)
import java.util.Scanner;

/*
 * class that defines a House, the environment in which all Appliances operate and
 * the state of which Meters measure
 */
public class House 
{
	/*
	 * PROPERTIES
	 */
	
	//water and electricity Meters for the House
	private Meter waterMeter, electricMeter;
	//ArrayList of all appliances within the House
	private ArrayList<Appliance> applianceList;
	//ConfigReader to read from config txt file and add Appliances accordingly
	private ConfigReader configReader;
	
	/*
	 * METHODS
	 */
	
	//constructor with no parameters, calls this(Meter, Meter) with default Meter objects as arguments
	public House()
	{
		this(new Meter("electricity", 0.013), new Meter("water", 0.002));
	}
	
	//constructor where Meter object references are taken as arguments
	public House(Meter electricMeter, Meter waterMeter)
	{
		//assign Meter arguments to the House's Meters
		this.electricMeter = electricMeter;
		this.waterMeter = waterMeter;
		
		//create the Appliance ArrayList
		this.applianceList = new ArrayList<>();
	}
	
	//adds Appliance that runs on electricity to applianceList and sets it to electroMeter
	public void addElectricAppliance(Appliance appliance)
	{
		this.applianceList.add(appliance);
		appliance.setMeter(this.electricMeter);
	}
	
	//adds Appliance that runs on water to applianceList and sets it to waterMeter
	public void addWaterAppliance(Appliance appliance)
	{
		this.applianceList.add(appliance);
		appliance.setMeter(this.waterMeter);
	}
	
	//removes Appliance object from the applianceList
	public void removeAppliance(Appliance appliance)
	{
		this.applianceList.remove(appliance);
	}
	
	//retrieves Appliance ArrayList from config file using ConfigReader methods
	public void addConfigAppliances(String filename) throws Exception
	{
		//initialise ConfigReader instance with filename of config file
		this.configReader = new ConfigReader(filename);
		//return ArrayList from getAppliances (passing House's Meters as arguments), set to House's applianceList
		this.applianceList = configReader.getAppliances(this.electricMeter, this.waterMeter);
	}
	
	//returns the number of Appliances within the House (applianceList)
	public int numAppliances()
	{
		return this.applianceList.size();
	}
	
	//makes all Appliances run one time increment (1 hour), then return total Meter cost afterward
	public double activate() throws Exception
	{
		for (Appliance appliance : this.applianceList)
		{
			//throw Exception if Appliance has not had one of the House's Meters set to it
			if (appliance.getMeter() != this.electricMeter && appliance.getMeter() != this.waterMeter)
			{
				throw new Exception("Appliance \"" + appliance.getName() + "\" is not set to a Meter from this House.");
			}
			
			appliance.timePasses();
		}
		
		return this.electricMeter.report() + this.waterMeter.report();
	}
	
	//overloaded activate(), loops over number of hours (timePasses() increments) given for parameter
	public double activate(int n) throws Exception
	{
		//totalCost will be returned as sum of total costs calculated over n hours
		double totalCost = 0;
		
		//run loop for n -- number of hours
		for (int i = 0; i < n; i++)
		{
			//print day number for each multiple of 24 hours
			if (i % 24 == 0)
			{
				System.out.println("\n\n+++++++++++++++++DAY  " + (i / 24 + 1) + "+++++++++++++++++");
			}
			
			//hour number; modulo 24 ensure hours are counted to 24 then reset to 1
			System.out.println("\n\n*******HOUR " + (i % 24 + 1) + "*******");
			
			//add total cost of one activate() in this loop to totalCost
			totalCost += this.activate();
		}
		
		return totalCost;
	}
	
	//method to run entire simulation given a config file and number of hours to run, defined in main() method
	public void runSimulation(String filename, int hours) throws Exception
	{
		//header declaring the config file used and the number of hours the simulation will run
		System.out.println("\n> Running simulation for " + hours + " hours, using config file: " + filename + ":");
		
		//addConfigAppliance() called to initialise Appliance ArrayList from config file
		this.addConfigAppliances(filename);
		//calculate total cost of running simulation through computing activate(hours)
		System.out.println("\nTotal cost (\u00A3): " + String.format("%.2f", this.activate(hours)));
	}
	
	/*
	 * MAIN
	 */
	
	//MAIN method -- program RUNS HERE i.e. this method will RUN SIMULATION
	public static void main(String[] args)
	{
		//try-catch to catch any Exceptions thrown
		try
		{
			//initialise default values for filename and hours to pass to runSimulation() if no args given
			String filename = "myHouse.txt";
			int hours = 7 * 24;
			//set default value for the capacity of the House's BatteryMeter's Battery
			float batteryCap = 2;
			//House object from which the simulation will be run
			House house;
			
			
			//more than three command line args invalid -- throws Exception
			if (args.length > 3)
			{
				throw new Exception("No more than 3 arguments can be passed to command line.");
			}
			//battery capacity set to integer defined by 3rd arg (if it exists)
			if (args.length > 2)
			{
				batteryCap = Float.parseFloat(args[2]);
				
				//do not accept negative batteryCap input
				if (batteryCap < 0)
				{
					throw new Exception("Battery capacity must not be a negative value.");
				}
			}
			//hours set to integer defined by 2nd arg (if it exists)
			if (args.length > 1)
			{
				hours = Integer.parseInt(args[1]);
				
				//do not accept negative hours input
				if (hours < 0)
				{
					throw new Exception("Hours must not be a negative value.");
				}
				//limit input to less than or equal to one year's worth of hours
				if (hours > 8760)
				{
					throw new Exception("Hours exceed maximum value of 8760 (365 days).");
				}
			}
			//filename set to filename defined by 1st arg (if it exists)
			if (args.length > 0)
			{
				filename = args[0];
				
				//throw Exception if filename is not of .txt filetype
				if (!filename.substring(filename.lastIndexOf(".")).equals(".txt"))
				{
					throw new Exception(".txt filetype required; " 
										+ filename.substring(filename.lastIndexOf(".")) + " given.");
				}
			}
			
			
			/*
			 * user determines whether they want to enable EXTENSION functionality
			 */
			//create choice string, then Scanner object to take user input
			String choice = "";
			Scanner scanner = new Scanner(System.in);
			//repeat query to user while choice String is not input as valid answer ("Y" or "N")
			while (!choice.equals("Y") && !choice.equals("N"))
			{
				System.out.println("Run Extension Mode? Y/N");
				choice = scanner.nextLine();
			}
			//close Scanner object
			scanner.close();
			
			//if user chooses to enable EXTENSION, enable its functionality
			if (choice.equals("Y"))
			{
				//myHouse.txt is to remain for running program in non-extension mode; throw Exception if passed
				if (filename.equals("myHouse.txt"))
				{
					throw new Exception("Cannot pass myHouse.txt as filename in Extension Mode");
				}
				//for any filename that isn't myHouse.txt, proceed with Extension Mode
				else
				{
					//ConfigReader object created to read Meters from config file referenced in filename
					ConfigReader extensionCfr = new ConfigReader(filename);
					
					//use ConfigReader methods to supply Meters for House() constructor
					house = new House(extensionCfr.getElectricMeter(), extensionCfr.getWaterMeter());
				}
			}
			//if non-extension mode, take the following default Meter arguments:
			else if (choice.equals("N"))
			{
				//create House object using batteryCap value to define BatteryMeter's Battery's unit capacity
				house = new House(new BatteryMeter("electricity", 0.013, batteryCap), new Meter("water", 0.002));
			}
			//if user (somehow) bypassed Scanner while-loop without giving valid input, throw Exception
			else
			{
				throw new Exception("Invalid input for toggling Extension Mode. Enter Y or N.");
			}
			
			
			//current state of filename and hours vars used to call runSimulation()
			house.runSimulation(filename, hours);
		}
		//print any caught Exception from try block
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
