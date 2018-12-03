//necessary to create ArrayList of Appliances
import java.util.ArrayList;

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
	
	//constructor with no parameters, calls this() with default Meter objects as references
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
	
	//makes all Appliance run one time increment (1 hour), then return total Meter cost afterward
	public double activate()
	{
		for (Appliance appliance : this.applianceList)
		{
			appliance.timePasses();
		}
		
		return this.electricMeter.report() + this.waterMeter.report();
	}
	
	//overloaded activate(), loops over number of hours (timePasses() increments) given for parameter
	public double activate(int n)
	{
		//totalCost will be returned as sum of total costs calculated over n hours
		double totalCost = 0;
		
		//run loop for n -- number of hours
		for (int i = 0; i < n; i++)
		{
			try
			{
				//pause for 0.5 seconds at start of iteration to slow down execution for user convenience
				Thread.sleep(500);
				
				//hour number
				System.out.println("\n=====HOUR " + (i + 1) + "=====");
				
				//add total cost of one activate() in this loop to totalCost
				totalCost += this.activate();
			}
			//catch thrown Exception from Thread.sleep()
			catch (InterruptedException e)
			{
			}
		}
		
		return totalCost;
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test House functionality
	public static void main(String[] args)
	{
		//try-catch to catch any Exceptions thrown
		try
		{
			//create House object
			House house = new House(new BatteryMeter("electricity", 0.013, 5), new Meter("water", 0.002));
			
			/*
			 * RUN SIMULATION
			 */
			
			//more than one command line argument invalid -- throws Exception
			if (args.length > 1)
			{
				throw new Exception("No more than 1 argument can be passed to command line.");
			}
			//if one argument given on command line, it is passed as the filename to addConfigAppliances()
			else if (args.length == 1)
			{
				house.addConfigAppliances(args[0]);
				for (int i = 0; i < 7; i++)
				{
					System.out.println("\n\n======DAY " + (i + 1) + "======\n");
					System.out.println("\nDay cost (£): " + house.activate(24));
				}
			}
			//if no argument given, defaults to attempting to read a "myHouse.txt" file
			else
			{
				house.addConfigAppliances("myHouse.txt");
				System.out.println("\nTotal cost (£): " + house.activate(24));
			}
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
