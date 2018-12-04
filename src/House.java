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
	//ConfigSaver to write to config txt file during save process (EXTENSION)
	private ConfigSaver configSaver;
	//hour to save to config txt file during save process (EXTENSION)
	private int hourToSave;
	//static scanner to read user input from command line (independent of House instance)
	private static Scanner scanner = new Scanner(System.in);
	
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
	public double runSimulation(String filename, int hours) throws Exception
	{
		//header declaring the config file used and the number of hours the simulation will run
		System.out.println("\n> Running simulation for " + hours + " hours, using config file: " + filename + ":");
		
		//addConfigAppliances() called to initialise Appliance ArrayList from config file
		this.addConfigAppliances(filename);
		//calculate total cost of running simulation through computing activate(hours)
		double totalCost = this.activate(hours);
		System.out.println("\nTotal cost (\u00A3): " + String.format("%.2f", totalCost));
		
		return totalCost;
	}
	
	/*
	 * EXTENSION METHODS
	 */
	
	//overloaded activate will load saved values for previous hours run and total cost of a simulation and run:
	public double activate(int n, int loadedHours, double loadedCost) throws Exception
	{
		//run loop for n -- number of hours; set i to loadedHours to increment from loaded value
		for (int i = loadedHours; i < (n + loadedHours); i++)
		{
			//print day number for each multiple of 24 hours
			if (i % 24 == 0)
			{
				System.out.println("\n\n+++++++++++++++++DAY  " + (i / 24 + 1) + "+++++++++++++++++");
			}
			
			//hour number; modulo 24 ensure hours are counted to 24 then reset to 1
			System.out.println("\n\n*******HOUR " + (i % 24 + 1) + "*******");
			
			//add total cost of one activate() in this loop to previous totalCost (loadedCost)
			loadedCost += this.activate();
		}
		
		//set House's hour to save to config file (if necessary)
		this.hourToSave = n + loadedHours;
		
		return loadedCost;
	}
	
	//overloaded runSimulation takes saveState[] argument to load saved House state
	public double runSimulation(String filename, int hours, String[] saveState) throws Exception
	{
		//declare vars to load values defined in saveState[] to
		int loadedHours;
		double loadedCost;
		
		//parse values from strings; if invalid Exception thrown
		try
		{
			loadedHours = Integer.parseInt(saveState[0]);
			loadedCost = Double.parseDouble(saveState[1]);
		}
		catch(Exception ex)
		{
			throw new Exception("Invalid SAVESTATE values in config file.");
		}
		
		//if saveState[2] is an empty string, there was no Battery; if not, then attempt to load to House's Battery
		if (!saveState[2].equals(""))
		{
			//if float can't be parsed, throw invalid input Exception
			try
			{
				//if House's electricMeter is not a BatteryMeter object, throw Exception (cannot load units)
				if (this.electricMeter instanceof BatteryMeter)
				{
					//pass float to Battery through loadBatteryStore()
					((BatteryMeter) this.electricMeter).loadBatteryStore(Float.parseFloat(saveState[2]));
				}
				else
				{
					throw new Exception("House's Electric Meter has no Battery to restore saved units to.");
				}
			}
			catch (Exception ex)
			{
				throw new Exception("Invalid SAVESTATE Battery Store value: " + saveState[2]);
			}
		}
		
		//header declaring the config file used and the number of hours the simulation will run
		System.out.println("\n> Continuing simulation for " + hours + " hours, using config file: " + filename + ":");
		
		//header showing the hours run and total cost of the loaded House SAVESTATE
		System.out.print("\n> Previous simulation: Hours run: " + loadedHours + "; Total cost (\u00A3): " +
		String.format("%.2f", loadedCost));
		//print battery's units stored too, if it exists
		if (saveState[2].equals(""))
		{
			System.out.print("\n");
		}
		else
		{
			System.out.print("; Battery store (units): " + saveState[2] + "\n");
		}
		
		//addConfigAppliances() called to initialise Appliance ArrayList from config file
		this.addConfigAppliances(filename);
		
		//set Cyclic Appliances of House to be consistent with time in loadedHours so that they can "resume"
		for (Appliance appliance : this.applianceList)
		{
			if (appliance instanceof CyclicVaries)
			{
				((CyclicVaries) appliance).setTime(loadedHours);
			}
			else if (appliance instanceof CyclicFixed)
			{
				((CyclicFixed) appliance).setTime(loadedHours);
			}
		}
		
		//calculate total cost of running simulation through computing activate(hours)
		double totalCost = this.activate(hours, loadedHours, loadedCost);
		System.out.println("\nTotal cost (\u00A3): " + String.format("%.2f", totalCost));
		
		return totalCost;
	}
	
	//method to save House state to config file, using ConfigSaver instance
	public void saveToConfig(double cost)
	{
		//initialies configSaver
		configSaver = new ConfigSaver();
		//save() method saves all required fields to config file
		configSaver.save(this.hourToSave, cost, this.electricMeter, this.waterMeter, this.applianceList);
	}
	
	//static method to retrieve Y/N String input from user
	public static String userInput(String question)
	{
		//initialise empty choice string and new Scanner object
		String choice = "";
		
		//repeat query to user while choice String is not input as valid answer ("Y" or "N")
		while (!choice.equals("Y") && !choice.equals("N"))
		{
			System.out.println(question);
			choice = scanner.nextLine();
		}
		
		//return choice String
		return choice;
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
			/*
			 * VARIABLES
			 */
			
			//initialise default values for filename and hours to pass to runSimulation() if no args given
			String filename = "myHouse.txt";
			int hours = 7 * 24;
			//set default value for the capacity of the House's BatteryMeter's Battery
			float batteryCap = 2;
			//House object from which the simulation will be run
			House house;
			
			/*
			 * ARGUMENT HANDLING
			 */
			
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
			 * RUN SIMULATION (MODES: EXTENSION, NON-EXTENSION)
			 */
			
			/*
			 * user determines whether they want to enable EXTENSION functionality
			 */
			//create choice string, then Scanner object to take user input
			String choice = userInput("Run Extension Mode? Y/N");
			
			//if user chooses to enable EXTENSION, enable its functionality
			if (choice.equals("Y"))
			{
				//myHouse.txt is to remain for running program in non-extension mode; throw Exception if passed
				if (filename.equals("myHouse.txt"))
				{
					throw new Exception("Cannot pass myHouse.txt as filename in Extension Mode -- use another file.");
				}
				//for any filename that isn't myHouse.txt, proceed with Extension Mode
				else
				{
					//ConfigReader object created to read Meters from config file referenced in filename
					ConfigReader extensionCfr = new ConfigReader(filename);
					
					//load SAVESTATE contents to String[] for use in overloaded runSimulation()
					String[] saveState = extensionCfr.loadSave();
					
					//use ConfigReader methods to supply Meters for House() constructor
					house = new House(extensionCfr.getElectricMeter(), extensionCfr.getWaterMeter());
					
					//current state of filename and hours vars used to call runSimulation()
					double totalCost = house.runSimulation(filename, hours, saveState);
					
					/*
					 * SAVING (EXTENSION)
					 */
					
					//after execution of simulation, user has option to save to config file and exit, or just exit
					choice = userInput("\nSave state of House (Y) or end program (N)?");
					
					//if choice is "Y", attempt to save state of House to config file
					if (choice.equals("Y"))
					{
						house.saveToConfig(totalCost);
						System.out.println("File successfully saved to save.txt");
					}
				}
			}
			//if non-extension mode, take the following default Meter arguments:
			else if (choice.equals("N"))
			{
				//create House object using batteryCap value to define BatteryMeter's Battery's unit capacity
				house = new House(new BatteryMeter("electricity", 0.013, batteryCap), new Meter("water", 0.002));
				
				//current state of filename and hours vars used to call runSimulation()
				house.runSimulation(filename, hours);
			}
			//if user (somehow) bypassed Scanner while-loop without giving valid input, throw Exception
			else
			{
				throw new Exception("Invalid input for toggling Extension Mode. Only \"Y\" or \"N\" valid.");
			}
			
			//close scanner to prevent resource leak
			scanner.close();
		}
		//print any caught Exception from try block
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
