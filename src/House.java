//necessary for ArrayList of Appliances
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
	private Meter waterMeter;
	private Meter electroMeter;
	
	//ArrayList of all appliances within the House
	private ArrayList<Appliance> applianceList;
	
	/*
	 * METHODS
	 */
	
	//constructor, assigns new objects to instance var references
	public House()
	{
		//create Meter objects and assign them to this House
		this.electroMeter = new Meter("electricity", 0.013);
		this.waterMeter = new Meter("water", 0.002);
		
		//create the Appliance ArrayList
		applianceList = new ArrayList<>();
	}
	
	//adds Appliance that runs on electricity to applianceList and sets it to electroMeter
	public void addElectricAppliance(Appliance appliance)
	{
		applianceList.add(appliance);
		appliance.setMeter(electroMeter);
	}
	
	//adds Appliance that runs on water to applianceList and sets it to waterMeter
	public void addWaterAppliance(Appliance appliance)
	{
		applianceList.add(appliance);
		appliance.setMeter(waterMeter);
	}
	
	//removes Appliance object from the applianceList
	public void removeAppliance(Appliance appliance)
	{
		applianceList.remove(appliance);
	}
	
	//returns the number of Appliances within the House (applianceList)
	public int numAppliances()
	{
		return applianceList.size();
	}
	
	//makes all Appliance run one time increment (1 hour), then return total Meter cost afterward
	public double activate()
	{
		for (Appliance appliance : applianceList)
		{
			appliance.timePasses();
		}
		
		return waterMeter.report() + electroMeter.report();
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test House functionality
	public static void main(String[] args)
	{
		//try-catch to catch any exceptions thrown by invalid input
		try
		{
			//create house object
			House house = new House();
			//print number of appliances in house, should be 0
			System.out.println(house.numAppliances());
			
			//assign CyclicFixed Appliances as in specified table
			house.addElectricAppliance(new CyclicFixed("Lights", 6, 5));
			house.addElectricAppliance(new CyclicFixed("Fridge", 2, 24));
			
			//print number of appliances in house, should be 2
			System.out.println(house.numAppliances());
			
			//run one activate(), should print two reports then total cost of 8 * 0.013 (0.104)
			System.out.println("\nTotal cost (£): " + house.activate());
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
