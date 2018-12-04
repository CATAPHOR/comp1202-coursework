//class to define a Meter which has a related Battery instance to which it stores and from which it draws power
public class BatteryMeter extends Meter
{
	/*
	 * PROPERTIES
	 */
	
	//battery object declared to which the Meter shall be linked
	private Battery battery;
	
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
		System.out.println("Battery Power Drawn (units): " + String.format("%.2f", batteryPower));
		System.out.println("Mains Meter Reading (units): " + String.format("%.2f", this.meterReading));
		System.out.println("Current Battery Store (units): " + String.format("%.2f", this.battery.unitsStored) + "/" 
				+ String.format("%.2f", this.battery.capacityLimit));
		System.out.println("Cost (\u00A3): " + String.format("%.2f", cost));
		
		//reset meterReading to 0
		this.meterReading = 0f;
		
		//return calculated cost
		return cost;
	}
	
	/*
	 * EXTENSION METHODS
	 */
	
	//method to store units in Battery on saved simulation (in config file) to Battery
	public void loadBatteryStore(float f) throws Exception
	{
		//throw Exception if attempt is made to load more units than the Battery's capacity; else run method
		if (f <= this.battery.capacityLimit)
		{
			this.battery.storeUnits(Math.abs(f));
		}
		else
		{
			throw new Exception("Stored units value, " + f + ", exceeds maximum Battery capacity, " +
		+ this.battery.capacityLimit + ".");
		}
	}
	
	//getter method to battery capacity (units)
	public float getBatteryCap()
	{
		return this.battery.capacityLimit;
	}
	
	//getter method for currently stored units in battery
	public float getBatteryStore()
	{
		return this.battery.capacityLimit;
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
