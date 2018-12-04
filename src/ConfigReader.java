//necessary for reading of the text file
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

//necessary to create ArrayList of Appliances
import java.util.ArrayList;

//helper class to help extract information from the config txt file for use in a House simulation
public class ConfigReader
{
	/*
	 * PROPERTIES
	 */
	
	//create BufferedReader object to read text from input stream
	private BufferedReader reader;
	//array of Appliances to be passed to House
	private ArrayList<Appliance> applianceArray;
	//meters (passed to ConfigReader by House) to be used when registering Appliance instances
	private Meter electricMeter, waterMeter;
	//filename for EXTENSION, checking whether the file is of custom Extension format
	private String filename;
	
	/*
	 * METHODS
	 */
	
	//constructor, takes filename parameter as String to initialise reader
	public ConfigReader(String filename)
	{
		this.filename = filename;
		
		try
		{
			//set input stream as a FileReader of the text file specified in filename
			this.reader = new BufferedReader(new FileReader(this.filename));
		}
		catch (FileNotFoundException ex)
		{
			//catch the exception if the file is not found
			System.err.println(ex);
		}
	}
	
	//returns contents of next line in reader's file
	public String getLine()
	{
		try
		{
			//BufferedReader's readLine() returns text content of next line in file
			return this.reader.readLine();
		}
		//catch IOException (e.g. line not found)
		catch (IOException ex)
		{
			return null;
		}
	}

	//method to determine whether file is ready to be read
	public boolean fileIsReady()
	{
		//return false if reader has not been initialised
		if (this.reader == null)
		{
			return false;
		}
		
		//if reader exists:
		try
		{
			/*
			 * BufferedReader's ready() returns boolean: whether stream is ready to be read (not empty, 
			 * character stream ready, etc.)
			 */
			return this.reader.ready();
		}
		//presumably, if there's an IO exception, the file is not ready to be read
		catch (IOException ex)
		{
			return false;
		}
	}
	
	//return ArrayList of appliances gleaned from config txt file, take Meters as input to set for each Appliance
	public ArrayList<Appliance> getAppliances(Meter electricMeter, Meter waterMeter) throws Exception
	{
		//initialise local Meters to those supplied by House's call
		this.electricMeter = electricMeter;
		this.waterMeter = waterMeter;
		
		//initialise new applianceArray
		this.applianceArray = new ArrayList<>();
		//create temporary strings to store getLine() contents
		String applianceProperties = "";
		String line = "";
		
		//run this block if the first line is SAVESTATE, i.e. if it is of custom EXTENSION format
		if (checkExtension())
		{
			//skip through lines until the line is "APPLIANCES", the header for the Appliance section of the file
			do
			{
				line = this.getLine();
			} while (!line.equals("APPLIANCES"));
		}
		
		//runs while fileIsReady() is true (ready to read valid textual input)
		while (fileIsReady())
		{
			
			//if file ready, sets line to getLine(), ignores the empty lines between each appliance in the config file
			while (fileIsReady() && !(line = this.getLine()).equals(""))
			{
				//add the lines for each of the Appliance properties, delimited by ";", to the temporary String
				applianceProperties += line + ";";
				
				//breaks on last line to ensure Appliances read if no empty line between them in config file
				if (line.contains("Cycle length:"))
				{
					break;
				}
			}
			
			//prevent passing of invalid applianceProperties Strings to getAppliance() method
			if (!applianceProperties.equals(""))
			{
				//add use getAppliance() to generate Appliance from non-empty String, add this to the applianceArray
				this.applianceArray.add(this.getAppliance(applianceProperties.split(";")));
			}
			
			//reset temporary string
			applianceProperties = "";
		}
		
		//throw Exception for invalid config file with no defined Appliances
		if (this.applianceArray.isEmpty())
		{
			throw new Exception("No Appliances defined in config file.");
		}
		
		//return final applianceArray of all Appliances generated from config file
		return this.applianceArray;
	}
	
	//method to return an Appliance instance generated from the properties described in a String parameter
	public Appliance getAppliance(String[] input) throws Exception
	{
		//initialise Appliance object to return, and declare vars to store each of input's property substrings
		Appliance appliance = null;
		String name, subclass, meter, minu, maxu, fixu, prob, cycle;
		
		//name set to UNDETERMINED by default, if input[0] formatted correctly it is set to Appliance's name
		name = "UNDETERMINED";
		if (input[0].contains("name: "))
		{
			name = input[0].replace("name: ", "");
		}
		
		//define Exception for invalid input format (using Appliance name if input[0] was correctly formatted)
		Exception invalidInput = new Exception("Invalid data in config file, entry: " + name);
		
		//input String[] must be of length 8 -- defining the state of the 8 possible Appliance properties
		if (input.length != 8)
		{
			throw invalidInput;
		}
		
		//check all 8 required properties are present in the input
		boolean checkFormat = input[0].contains("name: ") && input[1].contains("subclass: ") && 
				input[2].contains("meter: ") && input[3].contains("Min units consumed:") && 
				input[4].contains("Max units consumed:") && input[5].contains("Fixed units consumed:") &&
				input[6].contains("Probability switched on:") && input[7].contains("Cycle length:");
		
		//if checkFormat is not true, input is invalid -- throw Exception
		if (!checkFormat)
		{
			throw invalidInput;
		}
		
		//all Appliance varieties have subclasses and meters -- initialise and strip config prefixes
		subclass = input[1].replace("subclass: ", "");
		meter = input[2].replace("meter: ", "");
		
		//check that property strings common to all Appliances aren't empty
		if (name.equals("") || subclass.equals("") || meter.equals(""))
		{
			throw invalidInput;
		}
		
		//set unique property Strings to be initially empty
		minu = maxu = fixu = prob = cycle = "";
		
		//Cyclic-type Appliances have "X/24" cycle lengths property; extract "X" from input
		if (subclass.contains("Cyclic"))
		{
			cycle = input[7].replace("Cycle length: ", "").replace("/24", "");
		}
		//Random-type Appliances have "1 in X" probability property; extract "X" from input
		if (subclass.contains("Random"))
		{
			prob = input[6].replace("Probability switched on: ", "").replace("1 in ", "");
		}
		//Varies-type Appliances have min and max unit properties; extract these from input
		if (subclass.contains("Varies"))
		{
			minu = input[3].replace("Min units consumed: ", "");
			maxu = input[4].replace("Max units consumed: ", "");
		}
		//Fixed-type Appliances have fixed unit consumption property; extract this from input
		if (subclass.contains("Fixed"))
		{
			fixu = input[5].replace("Fixed units consumed: ", "");
		}
		
		//use checkFormat to check if subclass String is one of the four defined Appliance variants
		checkFormat = subclass.equals("CyclicFixed") || subclass.equals("CyclicVaries") || 
				subclass.equals("RandomFixed") || subclass.equals("RandomVaries");
		
		//if subclass does not contain a String corresponding to a valid class, throw Exception
		if (!checkFormat)
		{
			throw invalidInput;
		}
		
		/*
		 * initialise appliance based on constructor of its subclass (defined by the subclass substring) using the 
		 * required properties initialised in the above if-statements
		 */
		if (subclass.equals("CyclicFixed"))
		{
			appliance = new CyclicFixed(name, Float.valueOf(fixu), Integer.valueOf(cycle));
		}
		if (subclass.equals("CyclicVaries"))
		{
			appliance = new CyclicVaries(name, Float.valueOf(minu), Float.valueOf(maxu), Integer.valueOf(cycle));
		}
		if (subclass.equals("RandomFixed"))
		{
			appliance = new RandomFixed(name, Float.valueOf(fixu), Integer.valueOf(prob));
		}
		if (subclass.equals("RandomVaries"))
		{
			appliance = new RandomVaries(name, Float.valueOf(minu), Float.valueOf(maxu), Integer.valueOf(prob));
		}
		
		//set Appliance's Meter based on the contents of its meter substring
		if (meter.equals("electricity") || meter.equals("electric"))
		{
			appliance.setMeter(this.electricMeter);
		}
		if (meter.equals("water"))
		{
			appliance.setMeter(this.waterMeter);
		}
		
		//return completed Appliance
		return appliance;
	}
	
	/*
	 * EXTENSION METHODS
	 */

	//method to load electric Meter from saved custom config file format
	public Meter getElectricMeter() throws Exception
	{
		//define Strings for Meter properties, set each to empty string ""
		String line, unitCost, batteryCap;
		line = unitCost = batteryCap = "";
		
		//runs while fileIsReady() is true (ready to read valid textual input)
		while (fileIsReady())
		{
			//read line from file
			line = this.getLine();
			//begin block if line read is the "name: electricity" header
			if (line.equals("name:electricity"))
			{
				//set the next two getLine contents, sans prefix, to the property Strings
				unitCost = this.getLine().replace("unit cost:", "");
				batteryCap = this.getLine().replaceAll("battery cap:", "");
				
				try
				{
					//naive electricity Meters have an empty string after the "battery cap:" prefix; call Meter()
					if (batteryCap.equals(""))
					{
						return new Meter("electricity", Double.parseDouble(unitCost));
					}
					//otherwise a value is given for the batteryCap and is passed to BatteryMeter() constructor
					else
					{
						return new BatteryMeter("electricity", Double.parseDouble(unitCost), Float.parseFloat(batteryCap));
					}
				}
				//if constructor Meter() failed, arguments must have been invalid; throw Exception
				catch (Exception ex)
				{
					throw new Exception("Electricity Meter data invalid in config file.");
				}
			}
		}
		
		//if method reaches this point it has not returned a Meter; no valid electric Meter defined in file
		throw new Exception("No valid Electricity Meter defined in config file.");
	}
	
	//method to load electric Meter from saved custom config file format
	public Meter getWaterMeter() throws Exception
	{
		//define Strings for Meter properties, set each to empty string ""
		String line, unitCost;
		line = unitCost = "";
		
		//runs while fileIsReady() is true (ready to read valid textual input)
		while (fileIsReady())
		{
			//read line from file
			line = this.getLine();
			//begin block if line read is the "name: water" header
			if (line.equals("name:water"))
			{
				//set the next two getLine contents, sans prefix, to the property Strings
				unitCost = this.getLine().replace("unit cost:", "");
				
				try
				{
					return new Meter("water", Double.parseDouble(unitCost));
				}
				//if constructor Meter() failed, arguments must have been invalid; throw Exception
				catch (Exception ex)
				{
					throw new Exception("Water Meter data invalid in config file.");
				}
			}
		}
		
		//if method reaches this point it has not returned a Meter; no valid water Meter defined in file
		throw new Exception("No valid Water Meter defined in config file.");
	}
	
	//checks whether file is of custom Extension format
	public boolean checkExtension() throws Exception
	{
		//check whether first line is "SAVESTATE", as would be with the custom Extension format
		boolean isExtensionFormat = this.reader.readLine().equals("SAVESTATE");
		//reset this.reader to new BufferedReader to return to beginning of file
		this.reader = new BufferedReader(new FileReader(filename));
		
		return isExtensionFormat;
	}
	
	//method to load SAVESTATE contents in Extension file format
	public String[] loadSave() throws Exception
	{
		//create return String[] to contain Strings for hour, total cost, and battery store
		String[] saveState = new String[3];
		String line, hour, totalCost, batteryStore;
		line = hour = totalCost = batteryStore = "";
		
		//runs while fileIsReady() is true (ready to read valid textual input)
		while (fileIsReady())
		{
			//read line from file
			line = this.getLine();
			//begin block if line read is the "SAVESTATE" header
			if (line.equals("SAVESTATE"))
			{
				//skip empty lines
				do
				{
					line = this.getLine();
				} while(line.equals(""));
				
				//set the next three getLine contents, with prefix, to the property Strings
				hour = line;
				totalCost = this.getLine();
				batteryStore = this.getLine();
				
				//verify the input from file has been correctly formatted
				if (hour.contains("hour:") && totalCost.contains("total cost:") && 
						batteryStore.contains("battery store:"))
				{
					//attempt to place strings holding only the values in saveState then return
					try
					{
						saveState[0] = hour.replace("hour:", "");
						saveState[1] = totalCost.replace("total cost:", "");
						saveState[2] = batteryStore.replace("battery store:", "");
						
						return saveState;
					}
					//if try block fails, arguments must have been invalid; throw Exception
					catch (Exception ex)
					{
						throw new Exception("SAVESTATE data invalid in config file.");
					}
				}
			}
		}
		
		//if method reaches this point it has not returned a Meter; no valid water Meter defined in file
		throw new Exception("No valid SAVESTATE contents found in config file.");
	}
	
	/*
	 * MAIN
	 */
	
	//main method used for testing of ConfigReader functionality
	public static void main(String[] args)
	{
		//try-catch necessary as some constructors throw Exceptions
		try
		{
			//create new ConfigReader for the config file
			ConfigReader cfr = new ConfigReader("myHouse.txt");
			
			//testing single input for getAppliance()
			cfr.getAppliance("name: Lights;subclass: CyclicFixed;meter: electric;Min units consumed:;Max units consumed:;Fixed units consumed: 6;Probability switched on:;Cycle length: 5/24;".split(";"));
			
			//attempt to getAppliances(), with 2 new Meter objects, from config file
			cfr.getAppliances(new Meter("electric", 0.013), new Meter("water", 0.002));
			//ArrayList size should be 13 according to the number of Appliances in config file
			System.out.println(cfr.applianceArray.size());
			
			//output class names and name Strings for each Appliance in ArrayList
			for (Appliance appliance : cfr.applianceArray)
			{
				System.out.println(appliance.getClass() + ": " + appliance.getName());
			}
			
			//EXTENSION
			ConfigReader cfrEx = new ConfigReader("extension.txt");
			cfrEx.loadSave();
			cfrEx.getElectricMeter();
			
			//create ConfigReader from invalid file
			ConfigReader cfr2 = new ConfigReader("nothere.txt");
			cfr2.getAppliances(new Meter("electric", 0.013), new Meter("water", 0.002));
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}
}
