//inherits Appliance, an Appliance with fixed use cycle and unit consumption during each day
public class CyclicFixed extends Appliance
{
	/*
	 * PROPERTIES
	 */
	
	//fixed number of units consumed by Appliance per hour
	private int unitsConsumed;
	//number of hours per day (1 - 24) Appliance is active
	private int cycleLength;
	//number of timePasses() called within a day (i.e. hours Appliance on since day start)
	private int timeOn;
	
	/*
	 * METHODS
	 */
	
	//constructor, calls Appliance constructor and initialises CyclicFixed instance vars
	public CyclicFixed(String name, int unitsConsumed, int cycleLength) throws Exception
	{
		super(name);
		this.unitsConsumed = unitsConsumed;
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
	}
	
	//defines CyclicFixed behaviour within an hour time increment
	public void timePasses()
	{
		//only run Appliance for specified period within a day
		if (this.timeOn < this.cycleLength)
		{
			this.tellMeterToConsumeUnits(this.unitsConsumed);
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
	
	//main method to test CyclicFixed functionality
	public static void main(String[] args)
	{
		//create meter for appliance
		Meter meter = new Meter("Electricity", 0.013);
		//try-catch required to catch Exception thrown by CyclicFixed constructor
		try
		{
			//create testApp (unit consumption 6 units/hr and 3/24 hr ontime)
			CyclicFixed testApp = new CyclicFixed("test", 6, 3);
			testApp.setMeter(meter);
			//should show all readings at 0
			meter.report();
			
			//run timePasses for 1/24
			testApp.timePasses();
			//should show 6 units consumed
			meter.report();
			
			//run timePasses for 1/24 - 4/24 (3 hrs)
			for (int i = 0; i < 3; i++)
			{
				testApp.timePasses();
			}
			//should show 12 (6 * 2) units consumed
			meter.report();
			
			//run timePasses for 4/24 - 1/24 (21 hours, rolls over to new day)
			for(int i = 0; i < 21; i++)
			{
				testApp.timePasses();
			}
			//should show 6 units consumed
			meter.report();
			
			//invalid constructor, should throw exception
			CyclicFixed testApp2 = new CyclicFixed("test2", 6, 25);
			testApp2.timePasses();
		}
		//catch any thrown Exception
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
