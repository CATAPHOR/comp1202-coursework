
public class BatteryMeter extends Meter
{
	/*
	 * PROPERTIES
	 */
	
	Battery battery;
	
	/*
	 * METHODS
	 */
	
	public BatteryMeter(String name, double unitCost, float capacityLimit)
	{
		super(name, unitCost);
		
		this.battery = new Battery(capacityLimit);
	}
	
	public double report()
	{
		double cost, x;
		
		System.out.println("Battery storage: " + this.battery.unitsStored)
		
		if (this.meterReading < 0)
		{
			this.battery.storeUnits(-1 * this.meterReading);
			cost = 0;
		}
		else if (this.meterReading > 0)
		{
			x = this.meterReading - this.battery.releaseUnits(this.meterReading);
			cost = this.unitCost * x;
		}
		
		System.out.println("Battery storage: " + this.battery.unitsStored);
		
		//print report of utility name, reading, and cost
		System.out.println("\n====METER REPORT====");
		System.out.println("Utility: " + this.utilityName);
		System.out.println("Meter Reading (units): " + x);
		System.out.println("Cost (£): " + cost);
		
		//reset meterReading to 0
		this.meterReading = 0f;
		
		//return calculated cost
		return cost;
	}
	
	/*
	 * MAIN
	 */
}
