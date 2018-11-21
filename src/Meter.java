//class defining a Meter -- an object to monitor utility consumption in a smart home
public class Meter 
{
	/*
	 * PROPERTIES
	 */
	
	//describes type of utility monitored
	private String utilityName;
	//cost of one unit of this type of utility in £
	private double unitCost;
	//holds balance of units of utility used by appliance since last meter reading
	private float meterReading;
	
	/*
	 * CONSTRUCTORS
	 */
	
	//constructor, initialises meter properties
	public Meter(String utilityName, double unitCost)
	{
		this.utilityName = utilityName;
		this.unitCost = unitCost;
		this.meterReading = 0f;
	}
	
	/*
	 * METHODS
	 */
	
	//adjust meterReading as per how many units of a utility are consumed
	public void consumeUnits(int i)
	{
		this.meterReading += i;
	}
	
	//generate report from current state of Meter properties, return calculated cost in £
	public double report()
	{
		//calculate cost of total metered utility usage
		double cost = this.meterReading * this.unitCost;
		
		//print report of utility name, reading, and cost
		System.out.println("\n====METER REPORT====");
		System.out.println("Utility: " + this.utilityName);
		System.out.println("Meter Reading (units): " + this.meterReading);
		System.out.println("Cost: " + cost);
		
		//reset meterReading to 0
		this.meterReading = 0f;
		
		//return calculated cost
		return cost;
	}
	
	/*
	 * MAIN
	 */
	
	//testing Meter functionality
	public static void main(String[] args)
	{
		Meter elecMeter = new Meter("Electricity", 0.013);
		
		elecMeter.consumeUnits(10);
		elecMeter.consumeUnits(-5);
		elecMeter.report();
		elecMeter.report();
	}
}
