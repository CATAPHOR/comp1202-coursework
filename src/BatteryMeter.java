//class to define a Meter which has a related Battery instance to which it stores and from which it draws power
public class BatteryMeter extends Meter
{
	/*
	 * PROPERTIES
	 */
	
	//battery object declared to which the Meter shall be linked
	Battery battery;
	
	/*
	 * METHODS
	 */
	
	//constructor, takes properties to call superclass Meter(), also initialises Battery instance
	public BatteryMeter(String name, double unitCost, float capacityLimit)
	{
		super(name, unitCost);
		//capacityLimit used to generate Battery instance with set capacity (units)
		this.battery = new Battery(capacityLimit);
	}
	
	//overridden report() from Meter, draws and stores power in Battery and alters cost as necessary
	public double report()
	{
		//print report header and utility type (name)
		System.out.println("\n====METER REPORT====");
		System.out.println("Utility: " + this.utilityName);
		
		//cost used to calculate and return cost of utility usage (as in Meter.report())
		double cost = 0;
		//float used to store the number of units released by the Battery
		float batteryPower = 0f;
		
		//if meterReading negative (i.e. power generated), attempt to store magnitude of negative value in Battery
		if (this.meterReading <= 0)
		{
			this.battery.storeUnits(Math.abs(this.meterReading));
		}
		/*
		 * if meterReading positive, attempt to release as much power as needed from the Battery storage, 
		 * subtract the released units from meterReading, then calculate the cost given the new meterReading value
		 */
		else
		{
			batteryPower = this.battery.releaseUnits(this.meterReading);
			this.meterReading -= batteryPower;
			cost = this.unitCost * this.meterReading;
		}
		
		//print report of current units in Battery and power drawn from it, the meterReading (mains reading), and cost
		System.out.println("Current Battery Store (units): " + this.battery.unitsStored + "/" + this.battery.capacityLimit);
		System.out.println("Battery Power Drawn (units): " + batteryPower);
		System.out.println("Mains Meter Reading (units): " + this.meterReading);
		System.out.println("Cost (£): " + cost);
		
		//reset meterReading to 0
		this.meterReading = 0f;
		
		//return calculated cost
		return cost;
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test BatteryMeter functionality
	public static void main(String[] args)
	{
		BatteryMeter meter = new BatteryMeter("electricity", 0.013, 5);
		//consume set number of units
		meter.consumeUnits(5);
		meter.report();
		
		//generate units, over Battery capacity
		meter.consumeUnits(-6);
		meter.report();
		
		//consume units, under Battery-stored
		meter.consumeUnits(3);
		meter.report();
		
		//consume units, over Battery-stored
		meter.consumeUnits(3);
		meter.report();
	}
}
