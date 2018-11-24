//necessary to generate random numbers
import java.util.Random;

//inherits Appliance, an Appliance with fixed use cycle but random unit consumption within each hour
public class CyclicVaries extends Appliance
{
	/*
	 * PROPERTIES
	 */
	
	//min and max number of units that can be consumed in an hour by Appliance
	private int minUnits, maxUnits;
	//number of hours per day (1 - 24) Appliance is active
	private int cycleLength;
	//number of timePasses() called within a day (i.e. hours Appliance on since day start)
	private int timeOn;
	//Random object to be used to generate random numbers
	private Random random;
	
	/*
	 * METHODS
	 */
	
	//constructor, calls Appliance constructor and initialises CyclicVaries instance vars
	public CyclicVaries(String name, int minUnits, int maxUnits, int cycleLength) throws Exception
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
		
		//presumes initialised at start of 24hr day; will not have run for any amount of time yet
		this.timeOn = 0;
		
		//check 1 hour <= cycleLength <= 24 hours, throw Exception if not
		if (cycleLength > 0 && cycleLength < 25)
		{
			this.cycleLength = cycleLength;
		}
		else
		{
			throw new Exception("Cycle length must be 1 - 24 hours.");
		}
		
		//initialise Random object for random number generation
		this.random = new Random();
	}
	
	//defines behaviour of CyclicVaries Appliance within an hour time increment
	public void timePasses()
	{
		//only run Appliance for specified period within a day
		if (this.timeOn < this.cycleLength)
		{
			//nextInt() returns int between minUnits and maxUnits, tells Meter to consume this number of units
			this.tellMeterToConsumeUnits(this.random.nextInt(this.maxUnits - this.minUnits + 1) + this.minUnits);
		}
				
		//increment timeOn; if day concludes (timeOn = 24), then reset to 0
		this.timeOn++;
		if (this.timeOn == 24)
		{
			this.timeOn = 0;
		}
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test CyclicVaries functionality
	public static void main(String[] args)
	{
		//try-catch block to catch any exceptions generated
		try
		{
			//create CyclicVaries and Meter objects, assign Meter to Appliance
			CyclicVaries testApp = new CyclicVaries("test", 4, 18, 2);
			Meter meter = new Meter("electricity", 0.013);
			testApp.setMeter(meter);
			
			//run timePasses() 3 times and report each, should show 2 random values then 0 for the last
			for (int i = 0; i < 3; i++)
			{
				testApp.timePasses();
				meter.report();
			}
			
			//run timePasses() 22 more times then report, should show 1 random value (begins new 24hr day)
			for (int i = 0; i < 22; i++)
			{
				testApp.timePasses();
			}
			meter.report();
			
			//test invalid constructor, should throw exception
			CyclicVaries testApp2 = new CyclicVaries("test2", 1, 10, 25);
			testApp2.timePasses();
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
