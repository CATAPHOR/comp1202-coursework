/*
 * ACADEMIC INTEGRITY DISCLAIMER:
 * This class reuses/recycles some of the functions I defined in my COMP1202 Lab 9 FlashCardReader class.
 */

//necessary reading of the text file
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
	
	/*
	 * METHODS
	 */
	
	//constructor, takes filename parameter as String to initialise reader
	public ConfigReader(String filename)
	{
		try
		{
			//set input stream as a FileReader of the text file specified in filename
			this.reader = new BufferedReader(new FileReader(filename));
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
		
		//runs while fileIsReady() is true (ready to read valid textual input)
		while (fileIsReady())
		{
			//sets line to getLine(), ignores the empty lines between each appliance in the config file
			while (!(line = this.getLine()).equals(""))
			{
				//add the lines for each of the Appliance properties, delimited by ";", to the temporary String
				applianceProperties += line + ";";
			}
			
			//prevent passing of invalid applianceProperties Strings to getAppliance() method
			if (!applianceProperties.equals("") && applianceProperties.split(";").length == 8)
			{
				//add use getAppliance() to generate Appliance from non-empty String, add this to the applianceArray
				this.applianceArray.add(this.getAppliance(applianceProperties));
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
	public Appliance getAppliance(String input) throws Exception
	{
		//initialise Appliance object to return, and declare vars to store each of input's property substrings
		Appliance appliance = null;
		String name, subclass, meter, minu, maxu, fixu, prob, cycle;
		
		//all Appliance varieties have names, subclasses, and meters -- initialise and strip config prefixes
		name = input.split(";")[0].replace("name: ", "");
		subclass = input.split(";")[1].replace("subclass: ", "");
		meter = input.split(";")[2].replace("meter: ", "");
		//set unique property Strings to be initially empty
		minu = maxu = fixu = prob = cycle = "";
		
		//Cyclic-type Appliances have "X/24" cycle lengths property; extract "X" from input
		if (subclass.contains("Cyclic"))
		{
			cycle = input.split(";")[7].replace("Cycle length: ", "").replace("/24", "");
		}
		//Random-type Appliances have "1 in X" probability property; extract "X" from input
		if (subclass.contains("Random"))
		{
			prob = input.split(";")[6].replace("Probability switched on: ", "").replace("1 in ", "");
		}
		//Varies-type Appliances have min and max unit properties; extract these from input
		if (subclass.contains("Varies"))
		{
			minu = input.split(";")[3].replace("Min units consumed: ", "");
			maxu = input.split(";")[4].replace("Max units consumed: ", "");
		}
		//Fixed-type Appliances have fixed unit consumption property; extract this from input
		if (subclass.contains("Fixed"))
		{
			fixu = input.split(";")[5].replace("Fixed units consumed: ", "");
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
	 * MAIN
	 */
	
	//main method used for testing of ConfigReader functionality
	public static void main(String[] args)
	{
		//try-catch necessary as some constructors throw Exceptions
		try
		{
			//create new ConfigReader for the config file
			ConfigReader cfr = new ConfigReader("Config file.txt");
			
			//testing single input for getAppliance()
			cfr.getAppliance("name: Lights;subclass: CyclicFixed;meter: electric;Min units consumed:;Max units consumed:;Fixed units consumed: 6;Probability switched on:;Cycle length: 5/24;");
			
			//attempt to getAppliances(), with 2 new Meter objects, from config file
			cfr.getAppliances(new Meter("electric", 0.013), new Meter("water", 0.002));
			//ArrayList size should be 13 according to the number of Appliances in config file
			System.out.println(cfr.applianceArray.size());
			
			//output class names and name Strings for each Appliance in ArrayList
			for (Appliance appliance : cfr.applianceArray)
			{
				System.out.println(appliance.getClass() + ": " + appliance.getName());
			}
			
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
