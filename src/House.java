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
	
	/*
	 * METHODS
	 */
	
	//constructor, assigns new objects to instance var references
	public House()
	{
		//create Meter objects and assign them to this House
		this.electricMeter = new Meter("electricity", 0.013);
		this.waterMeter = new Meter("water", 0.002);
		
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
	
	/*
	 * MAIN
	 */
	
	//main method to test House functionality
	public static void main(String[] args)
	{
		//try-catch to catch any exceptions thrown by invalid input
		try
		{
			//create House object
			House house = new House();
			//print number of Appliances in House, should be 0
			System.out.println(house.numAppliances());
			
			//assign Appliances
			house.addElectricAppliance(new CyclicFixed("Lights", 6, 5));
			house.addElectricAppliance(new CyclicFixed("Fridge", 2, 24));
			house.addWaterAppliance(new RandomVaries("Shower", 1, 2, 1));
			
			//print number of Appliances in House, should be 3
			System.out.println(house.numAppliances());
			
			//run one activate(), should print three reports then total cost
			System.out.println("\nTotal cost (£): " + house.activate());
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
