/*
 * File: PetConstants.java
 * ---------------------------
 * This file declares several constants that are shared by the
 * different modules in the PetForStanford.
 */

public interface PetConstants {

/** The width of the application window */
	public static final int APPLICATION_WIDTH = 1200;

/** The height of the application window */
	public static final int APPLICATION_HEIGHT = 700;
	
/** The display of the instruction */
	public static final String INSTRUCTION_FILE = "PetInstruction.txt";
	public static final double INSTRUCTION_PADDING = 15;
	public static final double INSTRUCTION_PAUSE = 10;
	public static final double INSTRUCTION_END = -5;
	
/** The number of dogs to be set initially */
	public static final int INITIAL_N_DOGS = 10;

/** Maximum number of dogs that could be displayed on canvas */
	public static final int MAX_N_DOGS = 14;
	
/** The size of dogs */	 
	public static final int DOG_MIN_SIZE = 30;
	public static final int DOG_MAX_SIZE = 50;
	
/** The size of the arc used for creating dog */	 	
	public static final int ARC_WIDTH = 20; 
	public static final int ARC_HEIGHT = 400; 

/** The max number of time that dog looks around */		
	public static final int MAX_LOOK = 5;

/** The time slots */	 
	public static final int MILLISECOND = 1000;
	public static final int HEARTBEAT = 20; 

/** Textfield size*/
	public static final int TXT_LENGTH = 10;
	
/** Dog going to user's home */	
	public static final int DOGMOVE_AMT = 7;	
	
}