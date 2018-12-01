//class to define a Battery - a device capable of storing and releasing units of utility
public class Battery 
{
	/*
	 * PROPERTIES
	 */
	
	//maximum number of units that can be stored in the Battery
	protected float capacityLimit;
	//current number of units stored in the Battery
	protected float unitsStored;
	
	/*
	 * METHODS
	 */
	
	//constructor, initialises Battery instance vars
	public Battery(float capacityLimit)
	{
		this.capacityLimit = capacityLimit;
		this.unitsStored = 0f;
	}
	
	//Battery attempts to store units passed to it
	public void storeUnits(float f)
	{
		//safeguard to ensure positive magnitude of f (negative shouldn't be passed to function in any case)
		f = Math.abs(f);
		
		//can only take in more units if the currently stored units are below capacity
		if (this.unitsStored < this.capacityLimit)
		{
			//ensure that adding the set units would not exceed the Battery's capacity
			if (this.unitsStored + f < this.capacityLimit)
			{
				this.unitsStored += f;
			}
			//if adding the set units would exceed capacity, the Battery's stored units are set to the capacity
			else
			{
				this.unitsStored = this.capacityLimit;
			}
		}
	}
	
	//Battery attempts to release stored units as per a set request (f)
	public float releaseUnits(float f)
	{
		//ensure request is for positive magnitude of f (negative shouldn't be passed to function in any case)
		f = Math.abs(f);
		
		//if more units stored than to be released, accept request and remove f from unitsStored
		if (f < this.unitsStored)
		{
			this.unitsStored -= f;
		}
		//if more units requested than currently stored, release all units stored instead
		else
		{
			f = this.unitsStored;
			this.unitsStored = 0f;
		}
		
		return f;
	}
	
	/*
	 * MAIN
	 */
	
	//main method to test Battery function
	public static void main(String[] args)
	{
		Battery battery = new Battery(5);
		
		//store units over capacity
		battery.storeUnits(12);
		System.out.println(battery.unitsStored);
		
		//release units over capacity
		System.out.println(battery.releaseUnits(6));
		System.out.println(battery.unitsStored);
		
		//test negative input
		System.out.println(battery.releaseUnits(-1));
		System.out.println(battery.unitsStored);
	}
}
