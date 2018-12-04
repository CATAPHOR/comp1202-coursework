-	PARTS ATTEMPTED
	Parts: 1, 2, 3, 4, 5, 6, 7 [i.e. ALL]
	
	NB:
	Part 4:	Though listed as private in the specification, Meter's properties were changed to 
		protected variables to facilitate the overriding of report() in BatteryMeter --
		thus avoiding unnecessarily convoluted getter/setter method usage.

	Part 7:	The method by which config-defined Appliances are added to the House's ArrayList will
		never assign an Appliance incorrectly (given the config entry has its Meter listed).
		As such, there is no need to handle Exceptions, as suggested in the specification, for 
		incorrect Meter-type assignment.

-	RUNNING THE MAIN METHOD:

	Main method is in the House class; main methods in other classes are for testing purposes and
	were purposely left in to demonstrate evaluation of each class' function.

	Config file has been renamed to "myHouse.txt" as shown in the specification.

	Run Main method from command line like so:
	
	java House "[config filename]" [hours to run simulation] [capacity of BatteryMeter's Battery]
	
	Each argument succeeds the previous; this means you can run the program with 0 - 3 arguments
	(i.e. giving 2 arguments will be registed as having given only the config filename and the 
	hours to run the simulation; 1 will presume only config filename given; and 0 will run with
	no arguments). Any of the 3 arguments not given in command line will take the default value 
	defined in the House main method.
	
	As arg[0] must be a string, it is better to enclose in "", as if the filename has a space
	it will attempt to pass the filename as multiple arguments.

	arg[1] must be an integer (hour number), and arg[2] a float (battery capacity). Invalid input
	will throw an Exception.

	If no arguments are given, the program will read from "myHouse.txt", run for 168 hours 
	(7 * 24), and set the Battery's capacity to 2.0.
	
	Upon running the program, it will prompt the user as to whether they wish to enable Extension
	Mode; giving "Y" will throw an exception if a config file isn't specified (i.e. extension.txt);
	myHouse.txt is the default and is RESERVED for non-Extension Mode.

-	EXTENSIONS:
	
	- 	Third argument taken in running Main (arg[2] = capacity of BatteryMeter's Battery)
	-	Extensive error-handling, especially in ConfigReader:
		Can read config files with 0 - any number of spaces between the property blocks for
		each appliance. Property blocks for each Appliance MUST have 8 lines or Exception
		thrown for invalid data. If one Appliance is incorrectly defined the Exception will
		contain its name (if it is salvageable). Numerous other cases accounted for.
	
	-	MAIN EXTENSION ATTEMPT:
		Using "extension.txt" as separate config file. Running the m
		Config File extended:
		 
		SAVESTATE contains last hour run in activate(), the Battery's
		stored units, and the total cost accumulated over the hours simulated. This allows
		for the state of a House after running a simulation to be saved and resumed to and
		from the extended config file. (NOT YET IMPLEMENTED)
		
		METERS contains the properties for the Meter objects, meaning that in Extension Mode
		the House's Electricity and Water Meters are initialised based on the values given
		in the config file.

		ConfigReader and House's main() had to undergo significant changes to accommodate
		the extended features as well as the default, non-extended feature set.