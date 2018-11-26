/*
 * Abstract class defining an Appliance -- devices within the smart home used at different times 
 * throughout the day. Can consume or generate units of utility.
 */
public abstract class Appliance 
{
	/*
	 * PROPERTIES
	 */
	
	//name of Appliance
	private String name;
	//Meter instance used by Appliance to monitor its utility consumption
	private Meter meter;
	
	/*
	 * METHODS
	 */
	
	//constructor, initialises Appliance's name
	public Appliance(String name)
	{
		this.name = name;
	}
	
	//public getter method for the Appliance's name
	public String getName()
	{
		return this.name;
	}
	
	//public setter method for the Appliance's meter
	public void setMeter(Meter meter)
	{
		this.meter = meter;
	}
	
	//protected method for meter to register consumed units from Appliance
	protected void tellMeterToConsumeUnits(float f)
	{
		this.meter.consumeUnits(f);
	}
	
	//abstract method for what an Appliance does within a time increment (1 hour)
	public abstract void timePasses();
}
