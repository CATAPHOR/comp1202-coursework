//necessary to generate random numbers
import java.util.Random;

//inherits Appliance, an Appliance with fixed unit consumption but random usage per each hour in the day
public class RandomFixed extends Appliance
{
	/*
	 * PROPERTIES
	 */
	
	//fixed number of units consumed by Appliance per hour
	private float unitsConsumed;
	//defines probability (1 in switchOn) that Appliance will be turned on
	private int switchOnP;
	//Random object to be used to generate random numbers
	private Random random;
	
	/*
	 * METHODS
	 */
	
	//constructor, calls Appliance constructor and initialises RandomFixed instance vars
	public RandomFixed(String name, float unitsConsumed, int switchOnP)
	{
		super(name);
		this.unitsConsumed = unitsConsumed;
		this.switchOnP = switchOnP;
		
		//initialise Random object for random number generation
		this.random = new Random();	
	}
	
	//defines behaviour of RandomFixed Appliance within an hour time increment
	public void timePasses()
	{
		/*
		 * generate random number (0 and switchOnP - 1 inclusive); for 1 / switchOnP chance, if the number
		 * chosen is 0 then the Appliance will turn on in this execution of timePasses()
		 */
		if (this.random.nextInt(this.switchOnP) == 0)
		{
			this.tellMeterToConsumeUnits(this.unitsConsumed);
		}
	}
	
	/*
	 * EXTENSION METHODS
	 */
	
	/*
	 * getter methods for Appliance properties
	 */
	//return fixed number of units consumed
	public float getFixedUnits()
	{
		return this.unitsConsumed;
	}
	
	//return probability of appliance turning on
	public int getProbability()
	{
		return this.switchOnP;
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test RandomFixed functionality
	public static void main(String[] args)
	{
		//create RandomFixed Appliance and Meter objects, assign Meter to Appliance
		RandomFixed testApp = new RandomFixed("test", 3, 6);
		Meter meter = new Meter("electricity", 0.013);
		testApp.setMeter(meter);
		
		//run 20 timePasses() then execute Meter's report
		for (int i = 0; i < 20; i++)
		{
			testApp.timePasses();
		}
		meter.report();
	}
}
