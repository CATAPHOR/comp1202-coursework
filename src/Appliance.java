/*
 * Abstract class defining an Appliance -- devices within the smart home used at different times 
 * throughout the day. Can consume or generate units of utility.
 */
public abstract class Appliance 
{
	/*
	 * PROPERTIES
	 */
	
	//name of appliance
	private String name;
	//Meter instance used by appliance to monitor its utility consumption
	private Meter meter;
	
	/*
	 * METHODS
	 */
	
	//constructor, initialises Appliance's name
	public Appliance(String name)
	{
		this.name = name;
	}
	
	//public setter method for the appliance's meter
	public void setMeter(Meter meter)
	{
		this.meter = meter;
	}
	
	//protected method for meter to register consumed units from appliance
	protected void tellMeterToConsumeUnits(int i)
	{
		this.meter.consumeUnits(i);
	}
	
	//abstract method for what an appliance does within a time increment (1 hour)
	public abstract void timePasses();
}
