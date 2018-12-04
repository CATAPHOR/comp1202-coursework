//necessary to generate random numbers
import java.util.Random;

//inherits Appliance, an Appliance with random usage and unit consumption per each hour of the day 
public class RandomVaries extends Appliance
{
	/*
	 * PROPERTIES
	 */
	
	//min and max number of units that can be consumed in an hour by Appliance
	private float minUnits, maxUnits;
	//defines probability (1 in switchOn) that Appliance will be turned on
	private int switchOnP;
	//Random object to be used to generate random numbers
	private Random random;
	
	/*
	 * METHODS
	 */
	
	//constructor, calls Appliance constructor and initialises RandomVaries instance vars
	public RandomVaries(String name, float minUnits, float maxUnits, int switchOnP)
	{
		super(name);
		
		/*
		 * config file has negative "max" values that are less than the "min" values; swapping them in such cases
		 * is necessary in order for the subsequently defined Random functions to work if given negative values
		 */
		if (minUnits < maxUnits)
		{
			this.minUnits = minUnits;
			this.maxUnits = maxUnits;
		}
		else
		{
			this.minUnits = maxUnits;
			this.maxUnits = minUnits;
		}
		
		this.switchOnP = switchOnP;
		
		//initialise Random object for random number generation
		this.random = new Random();
	}
	
	//defines behaviour of RandomVaries Appliance within an hour time increment
	public void timePasses()
	{
		/*
		 * generate random number (0 and switchOnP - 1 inclusive); for 1 / switchOnP chance, if the number
		 * chosen is 0 then the Appliance will turn on in this execution of timePasses()
		 */
		if (this.random.nextInt(this.switchOnP) == 0)
		{
			/*
			 * nextFloat() returns float between 0.0 and 1.0, (max - min) * float + min gives a random float
			 * between min and max, tells Meter to consume this quantity of units
			 */
			this.tellMeterToConsumeUnits((this.maxUnits - this.minUnits) * this.random.nextFloat() + this.minUnits);
		}
	}
	
	/*
	 * EXTENSION METHODS
	 */
	
	/*
	 * getter methods for Appliance properties
	 */
	//return maximum number of units consumed
	public float getMaxUnits()
	{
		return this.maxUnits;
	}
	
	//return minimum number of units consumed
	public float getMinUnits()
	{
		return this.minUnits;
	}
	
	//return probability of appliance turning on
	public int getProbability()
	{
		return this.switchOnP;
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test RandomVaries functionality
	public static void main(String[] args)
	{
		//create RandomVaries Appliance and Meter objects, assign Meter to Appliance
		RandomVaries testApp = new RandomVaries("test", 2, 12, 6);
		Meter meter = new Meter("electricity", 0.013);
		testApp.setMeter(meter);
		
		//run 1 timePasses() and give Meter's report
		testApp.timePasses();
		meter.report();
		
		//run 20 timePasses() then give Meter's report
		for (int i = 0; i < 20; i++)
		{
			testApp.timePasses();
		}
		meter.report();
		
		//testing for negative min and max values given
		RandomVaries testApp2 = new RandomVaries("test2", -2, -12, 1);
		testApp2.setMeter(meter);
		
		testApp2.timePasses();
		meter.report();
	}
}
