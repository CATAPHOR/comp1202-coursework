//necessary to output text to save file
import java.io.FileNotFoundException;
import java.io.PrintStream;

//necessary to access Appliances to write to file
import java.util.ArrayList;

//class defined for EXTENSION task -- helper class intended to save current state of House to config file
public class ConfigSaver 
{
	//unfinished; no time to implement this extension feature
	
	/*
	 * PROPERTIES
	 */
	
	//PrintStream object used to print to file
	private PrintStream printer;
	
	/*
	 * METHODS
	 */
	
	//constructor, initialises PrintStream object to print to "save.txt"
	public ConfigSaver()
	{
		try
		{
			this.printer = new PrintStream("save.txt");
		}
		catch (FileNotFoundException ex)
		{
			System.err.println(ex);
		}
	}
	
	//save details of House state after running simulation
	public void saveSaveState(int hour, double cost, double batteryStore)
	{
		//header line
		this.printer.println("SAVESTATE");
		this.printer.println("");
		
		//add hour and cost parameter lines
		this.printer.println("hour:" + hour);
		this.printer.println("total cost:" + cost);
		//negative battery store number taken to mean naive electricMeter (i.e. not BatteryMeter instance)
		if (batteryStore < 0)
		{
			this.printer.println("battery store:");
		}
		//if BatteryMeter instance, write current battery units stored
		else
		{
			this.printer.println("battery store:" + batteryStore);
		}
		
		this.printer.println("");
	}
	
	//save details defining the House's Meters
	public void saveMeters(Meter electricMeter, Meter waterMeter)
	{
		//print header line
		this.printer.println("METERS");
		this.printer.println("");
		
		//save electricMeter, put name and cost
		this.printer.println("name:electricity");
		this.printer.println("unit cost:" + electricMeter.unitCost);
		//print battery capacity if electricMeter instance of BatteryMeter
		if (electricMeter instanceof BatteryMeter)
		{
			this.printer.println("battery cap:" + ((BatteryMeter) electricMeter).getBatteryCap());
		}
		//if naive electricMeter, print line with no characters after prefix
		else
		{
			this.printer.println("battery cap:");
		}
		this.printer.println("");
		
		//save waterMeter, put name and cost
		this.printer.println("name:water");
		this.printer.println("unit cost:" + waterMeter.unitCost);
		this.printer.println("");
	}
	
	//save all Appliances in House's Appliance ArrayList
	public void saveAppliances(ArrayList<Appliance> applianceArrayList)
	{
		//print header line
		this.printer.println("APPLIANCES");
		this.printer.println("");
		
		//declare Strings to write for each property
		String subclass, minu, maxu, fixu, prob, cycle;
		
		//iterate through every Appliance and write to file
		for (Appliance appliance : applianceArrayList)
		{
			//initialise property Strings to empty string
			subclass = minu = maxu = fixu = prob = cycle = "";
			//initialise subclass to the appliance's Class name
			subclass = appliance.getClass().getSimpleName();
			
			//write properties common to all Appliances
			this.printer.println("name: " + appliance.getName());
			this.printer.println("subclass: " + subclass);
			this.printer.println("meter: " + appliance.getMeter().utilityName);
			
			//if Appliance is a CyclicVaries, define minimum and maximum unit consumption; cycle length Strings
			if (appliance instanceof CyclicVaries)
			{
				minu = " " + Float.toString(((CyclicVaries) appliance).getMinUnits());
				maxu = " " + Float.toString(((CyclicVaries) appliance).getMaxUnits());
				cycle = " " + Integer.toString(((CyclicVaries) appliance).getCycleLength()) + "/24";
			}
			//if Appliance is a CyclicFixed, define fixed unit consumption; cycle length Strings
			else if (appliance instanceof CyclicFixed)
			{
				fixu = " " + Float.toString(((CyclicFixed) appliance).getFixedUnits());
				cycle = " " + Integer.toString(((CyclicFixed) appliance).getCycleLength()) + "/24";
			}
			//if Appliance is a RandomVaries, define minimum and maximum unit consumption; probability Strings
			else if (appliance instanceof RandomVaries)
			{
				minu = " " + Float.toString(((RandomVaries) appliance).getMinUnits());
				maxu = " " + Float.toString(((RandomVaries) appliance).getMaxUnits());
				prob = " 1 in " + Integer.toString(((RandomVaries) appliance).getProbability());
			}
			//if Appliance is a RandomFixed, define fixed unit consumption; probability Strings
			else if (appliance instanceof RandomFixed)
			{
				fixu = " " + Float.toString(((RandomFixed) appliance).getFixedUnits());
				prob = " 1 in " + Integer.toString(((RandomFixed) appliance).getProbability());
			}
			
			//print all property Strings, with either defined or empty string after prefixes depending on Appliance
			this.printer.println("Min units consumed:" + minu);
			this.printer.println("Max units consumed:" + maxu);
			this.printer.println("Fixed units consumed:" + fixu);
			this.printer.println("Probability switched on:" + prob);
			this.printer.println("Cycle length:" + cycle);
			this.printer.println("");
		}
	}
	
	//composite save function intended to write all 3 sections to config file
	public void save(int hour, double cost, Meter electricMeter, Meter waterMeter,
			ArrayList<Appliance> applianceArrayList)
	{
		//create batteryStore to get the current units stored in the BatteryMeter
		double batteryStore;
		if (electricMeter instanceof BatteryMeter)
		{
			batteryStore = ((BatteryMeter)electricMeter).getBatteryStore();
		}
		//if electricMeter not a BatteryMeter, set this value to -1 (signals other methods)
		else
		{
			batteryStore = -1;
		}
		
		//call all 3 save functions to config file
		this.saveSaveState(hour, cost, batteryStore);
		this.saveMeters(electricMeter, waterMeter);
		this.saveAppliances(applianceArrayList);
	}
}
