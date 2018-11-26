//class defining a Meter -- an object to monitor utility consumption in a smart home
public class Meter 
{
	/*
	 * PROPERTIES
	 */
	
	//describes type of utility monitored
	protected String utilityName;
	//cost of one unit of this type of utility in £
	protected double unitCost;
	//holds balance of units of utility used by appliance since last meter reading
	protected float meterReading;
	
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
	public void consumeUnits(float f)
	{
		this.meterReading += f;
	}
	
	//generate report from current state of Meter properties, return calculated cost in £
	public double report()
	{	
		//calculate cost of total metered utility usage, if meterReading negative then cost = 0
		double cost;
		
		if (this.meterReading > 0)
		{
			cost = this.meterReading * this.unitCost;
		}
		else
		{
			cost = 0;
		}
		
		//print report of utility name, reading, and cost
		System.out.println("\n====METER REPORT====");
		System.out.println("Utility: " + this.utilityName);
		System.out.println("Meter Reading (units): " + this.meterReading);
		System.out.println("Cost (£): " + cost);
		
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
		//create Meter object
		Meter elecMeter = new Meter("Electricity", 0.013);
		
		//should report 5 units, then 0
		elecMeter.consumeUnits(10);
		elecMeter.consumeUnits(-5);
		elecMeter.report();
		elecMeter.report();
	}
}
