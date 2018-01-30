/*
 * File: GraphicsContest.java
 * --------------------------
 * Student: Sunwoo Kang 
 * Instructor: Maria 
 * __________________________
 * Title: Pet Dog in Stanford 
 */

import acm.util.*;
import acm.io.*;
import acm.program.*;
import acm.graphics.*;
import acm.util.RandomGenerator.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.swing.*;


public class GraphicsContest extends GraphicsProgram implements PetConstants {

	/** the way to store all dogs*/
	private ArrayList<Dog> dogs = new ArrayList<Dog>(); 
	private ArrayList<GObject> caughtDog = new ArrayList<GObject>();

	/**generate random things*/
	private RandomGenerator rg = new RandomGenerator();
	private GLabel maxDog;

	/**custom new dog*/
	private JTextField sizeField = null; 
	private JTextField colorField = null;

	/**user information*/
	private String userName = ""; 
	private String userAddress = "";
	private boolean preCatch = true;
	private boolean haveDog = false;
	private ArrayList<GLabel> instruct;

	/**variables needed for dog talk*/
	private double speechX, speechY, dogDsz;

	public void init() {
		sizeField = new JTextField(TXT_LENGTH);
		colorField = new JTextField(TXT_LENGTH);
		JButton custom = new JButton("Designed Dog");
		JButton adDog = new JButton("New Dog");
		JButton clearDog = new JButton("All Away");
		add(new JLabel("Small or Big: "), NORTH);
		add(sizeField, NORTH);
		add(new JLabel("Color: "), NORTH);
		add(colorField, NORTH);
		add(custom, NORTH);	
		add(adDog, NORTH);
		add(clearDog, NORTH);
		addActionListeners();
		addMouseListeners();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("New Dog")){
			if(dogs.size()<=MAX_N_DOGS) {
				hideMaxDogMessage();
				addDog(dogs.size()); 
			}
		} else if (e.getActionCommand().equals("All Away")) {
			hideMaxDogMessage();
			removeAllDogs();			
		} else if (e.getActionCommand().equals("Designed Dog")) {
			if(dogs.size()<MAX_N_DOGS) {
				hideMaxDogMessage();
				String sizeInput = sizeField.getText().toLowerCase();
				String colorInput = colorField.getText().toUpperCase();
				Color cdog = findColor(colorInput);
				if(cdog != null) {
					addCDog(dogs.size(),sizeInput, cdog);
				}
			}
		}
	}
	private void hideMaxDogMessage() {
		if(maxDog!=null) {
			GRect hide = new GRect (maxDog.getWidth()*1.1, maxDog.getHeight()*1.1);
			hide.setFilled(true);
			hide.setFillColor(Color.WHITE);
			hide.setColor(Color.WHITE);
			add(hide,(APPLICATION_WIDTH-maxDog.getWidth())/2.0, 0);	
		}
	}
	private Color findColor (String colorname) {
		Color c=null;
		if(colorname.contentEquals("RED")) c = Color.RED;
		if(colorname.contentEquals("BLUE")) c = Color.BLUE;
		if(colorname.contentEquals("GREEN")) c = Color.GREEN;
		if(colorname.contentEquals("YELLOW")) c = Color.YELLOW;
		if(colorname.contentEquals("CYAN")) c = Color.CYAN;
		if(colorname.contentEquals("ORANGE")) c = Color.ORANGE;
		if(colorname.contentEquals("PINK")) c = Color.PINK;
		if(colorname.contentEquals("MAGENTA")) c = Color.MAGENTA;
		if(colorname.contentEquals("GRAY")) c = Color.GRAY;
		if(colorname.contentEquals("DARK GRAY")) c = Color.DARK_GRAY;
		if(colorname.contentEquals("LIGHT GRAY")) c = Color.LIGHT_GRAY;
		return c;
	}
	//code adapted from Nick's code "ArrayLists_OpeningCrawlSoln"
	private ArrayList<GLabel> readInstructionFile(String fileName) {
		try {
			BufferedReader rd = new BufferedReader(new FileReader(fileName));		
			ArrayList<GLabel> instruction = new ArrayList<GLabel>();
			String line = rd.readLine();
			while (line != null) {
				// Make a GLabel out of this line and put it in our arraylist
				GLabel label = new GLabel(line);
				label.setFont("Courier New-bold-32");
				instruction.add(label);
				line = rd.readLine();
			}
			rd.close();
			return instruction;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public void run() {	
		//0 phase - instruction & user input 
		instruct = readInstructionFile(INSTRUCTION_FILE);

		double y = getHeight();
		for (int i = 0; i < instruct.size(); i++) {
			GLabel currentLabel = instruct.get(i);			
			double x = getWidth() / 2.0 - currentLabel.getWidth() / 2.0;
			currentLabel.sendToBack();
			add(instruct.get(i), x, y);
			y += currentLabel.getAscent() + INSTRUCTION_PADDING;
		}
		while(preCatch) {
			for (int i = 0; i < instruct.size(); i++) {
				instruct.get(i).move(0, -1);
				if(instruct.get(instruct.size()-1).getY()<INSTRUCTION_END) {
					preCatch = false;
				}
			}
			pause(INSTRUCTION_PAUSE);
		}
		for(int i=0; i<INITIAL_N_DOGS; i++) {
			addDog(i);
		}
		enterPlayerDetail();

		//1st phase - catching dog 
		while(!haveDog) {
			if(dogs.size()==MAX_N_DOGS ) {
				maxDog = new GLabel ("Too Many Dogs! Wait or Send All Away");
				maxDog.setColor(Color.RED);
				maxDog.setFont("Courier-12");
				add(maxDog, (APPLICATION_WIDTH-maxDog.getWidth())/2.0, maxDog.getAscent());
				maxDog.sendToFront();				
			}
			for(int i=0; i<dogs.size();i++) {
				if(dogs.contains(dogs.get(i))) {
					dogs.get(i).heartbeat();
				}
			}
			clashDog();
			for(int i=0; i<dogs.size();i++) {
				if(dogs.contains(dogs.get(i))) {
					dogs.get(i).reflectOffWalls();
				}
			}
			pause(HEARTBEAT);
		}
		//2nd phase - talking to dog 
		double talktime = MILLISECOND*1.5;
		pause(talktime);
		addCircleForSpeech(dogDsz*8, dogDsz*2.5);
		addSecondDogMessage(speechX+dogDsz*4, speechY+dogDsz*2.5/2); 
		pause(talktime);
		addCircleForSpeech(dogDsz*8, dogDsz*2.5);
		addThirdDogMessage(speechX+dogDsz*4, speechY+dogDsz*2.5/2); 
		pause(talktime);
		stopDogTalking();

		//3rd phase - going home with the dog 
		boolean atHome=false;
		while(!atHome) {		
			for(GObject obj: caughtDog) {				
				obj.move(0, DOGMOVE_AMT);
				obj.sendToFront();
			}
			if(caughtDog.get(0).getY()>APPLICATION_HEIGHT) atHome = true;
			pause(HEARTBEAT);
		}
		GLabel end = new GLabel("THE END");
		end.setFont("Courier-100");
		end.setLocation((APPLICATION_WIDTH-end.getWidth())/2.0, 
				(APPLICATION_HEIGHT-end.getAscent())/2.0);
		add(end);
	}
	private void enterPlayerDetail() {
		IODialog dialog = getDialog();
		userName = dialog.readLine("Enter your name: ");
		userAddress = dialog.readLine("Enter your dorm: ");
	}
	public void mouseClicked(MouseEvent e) {
		preCatch = false;
		for (int i = 0; i < instruct.size(); i++) {
			instruct.get(i).move(0, -10000);
		}	
	}
	public void mousePressed(MouseEvent e) {
		for (int i=0; i<dogs.size(); i++) {
			caughtDog = dogs.get(i).catchDog(e.getX(), e.getY());
			if(caughtDog!=null) {
				removeAllDogs();
				hideMaxDogMessage();
				for (GObject obj: caughtDog) {
					add(obj);
				}	
				IODialog dialog = getDialog();
				String name = dialog.readLine("Enter dog's name");
				haveDog = true;
				talkingDog(name);	
			}
		}
	}
	private void talkingDog (String dogName) {
		//location & boolean setup 
		double caughtPosX = caughtDog.get(1).getX();
		double caughtPosY = caughtDog.get(1).getY();
		//find the right size of speech 
		dogDsz = caughtDog.get(0).getWidth();
		if(dogDsz>DOG_MAX_SIZE) {
			dogDsz = DOG_MAX_SIZE;
		} else if(dogDsz<DOG_MIN_SIZE) {
			dogDsz = DOG_MIN_SIZE;
		} 
		double speechW, speechH;
		boolean speechAtRight = true;
		boolean speechAtUp = true;

		//find speech location relative to dog 
		if(caughtPosX < APPLICATION_WIDTH/2.0) {
			speechX = caughtPosX + dogDsz*3;
		} else {
			speechX = caughtPosX - dogDsz*3;
			speechAtRight = false;
		}
		if (caughtPosY>APPLICATION_HEIGHT/2.0) {
			speechY = caughtPosY - 4*dogDsz;
			if(speechY>APPLICATION_HEIGHT-dogDsz*2.6) {
				speechY = APPLICATION_HEIGHT-dogDsz*4;
			}
		} else {
			speechY = caughtPosY + dogDsz*3;
			speechAtUp = false;
			if(speechY<0) {
				speechY = caughtPosY - 3*dogDsz;
				//if(speechY<0) speechY = 10;
			}
		}

		// add circle for speech
		addSpeechTriangle(speechAtRight,speechAtUp);
		addCircleForSpeech(dogDsz*8, dogDsz*2.5);

		//actual dog talking
		addFirstDogMessage(dogName, speechX+dogDsz*4, speechY+dogDsz*2.5/2);
	}
	private void stopDogTalking() {
		GRect stop = new GRect(dogDsz*9, dogDsz*3.5);
		stop.setFilled(true);
		stop.setFillColor(Color.WHITE);
		stop.setColor(Color.WHITE);
		add(stop,speechX-dogDsz*0.3,speechY-dogDsz*0.3);
	}
	private void addCircleForSpeech (double w, double h) {
		GOval speech = new GOval (speechX, speechY, w, h);
		speech.setFilled(true);
		speech.setColor(Color.BLACK);
		speech.setFillColor(Color.WHITE);
		speech.sendToFront();
		add(speech);
	}
	private void addSpeechTriangle(boolean atRight, boolean atUp) {
		//add triangle
		double size = dogDsz/1.5;
		GPolygon triangle = new GPolygon(dogDsz/1.5, dogDsz); 
		triangle.sendForward();
		triangle.addEdge(size/2,size*2);
		triangle.addVertex(size/2,size*2);
		triangle.addEdge(size, 0);
		triangle.addVertex(size, 0);
		triangle.addEdge(-size/2, size*2);
		//shift triangle to appropriate direction 
		double tx = speechX + dogDsz*3.0;
		double ty = speechY + dogDsz*2.5/2.0;
		//add triangular part of speech
		double triangleX, triangleY;
		if(atRight && atUp) {
			triangle.rotate(130);
			triangleX = tx - dogDsz*1.5;
			triangleY = ty + dogDsz*2.3;
		} else if (atRight && !atUp) {
			triangle.rotate(60);
			triangleX = tx - dogDsz*2.9;
			triangleY = ty - dogDsz*0.8;
		} else if (!atRight && atUp) {
			triangle.rotate(170);
			triangleX = tx + dogDsz*2.5;
			triangleY = ty + dogDsz*1.7;
		} else {
			triangle.rotate(-30);
			triangleX = tx;
			triangleY = ty - dogDsz*1.9;
		}
		add(triangle,triangleX,triangleY);
		triangle.sendToBack();
	}
	private void addFirstDogMessage(String dogN, double sx, double sy) {
		//first part of the conversation 
		GLabel dogtalk = new GLabel("Hi "+userName+"!");			
		GLabel longtalk = new GLabel("My name is "+dogN);
		double averageSize = (DOG_MIN_SIZE+DOG_MAX_SIZE)/2.0;
		if(dogDsz<averageSize/2.0) {
			dogtalk.setFont("Courier-15");
			longtalk.setFont("Courier-15");
		} else if (dogDsz<averageSize) {
			dogtalk.setFont("Courier-19");
			longtalk.setFont("Courier-19");
		} else if (dogDsz<averageSize*3/2) {
			dogtalk.setFont("Courier-22");
			longtalk.setFont("Courier-22");
		} else {
			dogtalk.setFont("Courier-25");
			longtalk.setFont("Courier-25");
		}
		dogtalk.sendToFront();
		longtalk.sendToFront();
		add(dogtalk, sx-dogtalk.getWidth()/2.0, sy-dogtalk.getAscent()/2);
		add(longtalk,sx-longtalk.getWidth()/2.0, sy+longtalk.getAscent());		
	}
	private void addSecondDogMessage(double sx, double sy) {
		//second part of the conversation 
		GLabel dogtalk = new GLabel("My new home is");			
		GLabel longtalk = new GLabel(userAddress);
		double averageSize = (DOG_MIN_SIZE+DOG_MAX_SIZE)/2.0;
		if(dogDsz<averageSize/2.0) {
			dogtalk.setFont("Courier-15");
			longtalk.setFont("Courier-15");
		} else if (dogDsz<averageSize) {
			dogtalk.setFont("Courier-19");
			longtalk.setFont("Courier-19");
		} else if (dogDsz<averageSize*3/2) {
			dogtalk.setFont("Courier-22");
			longtalk.setFont("Courier-22");
		} else {
			dogtalk.setFont("Courier-25");
			longtalk.setFont("Courier-25");
		}
		dogtalk.sendToFront();
		longtalk.sendToFront();
		add(dogtalk, sx-dogtalk.getWidth()/2.0, sy-dogtalk.getAscent()/2);
		add(longtalk,sx-longtalk.getWidth()/2.0, sy+longtalk.getAscent());		
	}
	private void addThirdDogMessage(double sx, double sy) {
		//second part of the conversation 
		GLabel dogtalk = new GLabel("Let's go home ");			
		GLabel longtalk = new GLabel("TOGETHER");
		double averageSize = (DOG_MIN_SIZE+DOG_MAX_SIZE)/2.0;
		if(dogDsz<averageSize/2.0) {
			dogtalk.setFont("Courier-15");
			longtalk.setFont("Courier-15");
		} else if (dogDsz<averageSize) {
			dogtalk.setFont("Courier-19");
			longtalk.setFont("Courier-19");
		} else if (dogDsz<averageSize*3/2) {
			dogtalk.setFont("Courier-22");
			longtalk.setFont("Courier-22");
		} else {
			dogtalk.setFont("Courier-25");
			longtalk.setFont("Courier-25");
		}
		dogtalk.sendToFront();
		longtalk.sendToFront();
		add(dogtalk, sx-dogtalk.getWidth()/2.0, sy-dogtalk.getAscent()/2);
		add(longtalk,sx-longtalk.getWidth()/2.0, sy+longtalk.getAscent());		
	}
	private void removeAllDogs() {
		for(Dog each: dogs) {
			for (GObject obj: each.returnDog()) {
				remove(obj);
			}
		}
		dogs.clear();
	}
	//special addition of dogs by user input
	public void addCDog (int index, String size, Color dcol) {
		Dog NewDog = new Dog(); 
		if (size.contentEquals("small")) NewDog.resize(0.5);
		else if (size.contentEquals("big")) NewDog.resize(2.0);
		NewDog.setDogColor(dcol);
		dogs.add(index,NewDog);	
		for (GObject obj: dogs.get(index).returnDog()) {
			obj.sendToFront();
			add(obj);
		}
	}
	//normal addition of dogs
	public void addDog(int index) {
		Dog NewDog = new Dog(); 
		dogs.add(index,NewDog);
		setDogsApart(index);
		for (GObject obj: dogs.get(index).returnDog()) {
			obj.sendToFront();
			add(obj);
		}
	}
	private void setDogsApart(int bornDog) {
		boolean newBirth = false; 
		while(!newBirth) {
			GPoint birth = dogs.get(bornDog).giveDogLocation();
			double birthX = birth.getX();
			double birthY = birth.getY();
			double dogDsize = dogs.get(bornDog).dsize;		
			//check it
			boolean completelyEmpty = false;
			while(!completelyEmpty){
				for (int i=0; i<dogs.size(); i++) {
					if(dogs.size()>1) {
						GRectangle checkerboard = createCheckingBoard(birthX,birthY, dogDsize);	
						GPoint older = dogs.get(i).giveDogLocation();
						boolean isFilled = checkerboard.contains(older);
						if(isFilled){
							birthX = rg.nextDouble(0.4*dogDsize, 
									APPLICATION_WIDTH-dogDsize*4.0);
							birthY = rg.nextDouble(0.4*dogDsize, 
									APPLICATION_HEIGHT-dogDsize*4.0);
							birth = new GPoint(birthX,birthY);
							i = -1;
						}
					}
				}
				completelyEmpty = true;
			}
			dogs.get(bornDog).setDogPosition(birthX, birthY);
			newBirth = true;
			return;
		}
	}
	private GRectangle createCheckingBoard(double x, double y, double ndsize) {
		double leftUpX = x-ndsize*0.5;
		double leftUpY = y; 
		GRectangle result = new GRectangle (leftUpX, leftUpY,ndsize*4.0, ndsize*4.0);
		return result;
	}
	private boolean areSameDogs(Dog cDog, Dog ccDog) {
		GPoint cDogLoc = cDog.giveDogLocation(); 
		GPoint ccDogLoc = ccDog.giveDogLocation(); 
		boolean sameX = (cDogLoc.getX() == ccDogLoc.getX());
		boolean sameY = (cDogLoc.getY() == ccDogLoc.getY());
		if(sameX && sameY) return true;
		else return false; 
	}
	private double getDogsDistance(Dog d1, Dog d2) {
		GPoint dogP1 = d1.giveDogLocation();
		GPoint dogP2 = d2.giveDogLocation();
		double xdis = Math.pow(dogP1.getX()-dogP2.getX(),2);
		double ydis = Math.pow(dogP1.getY()-dogP2.getY(),2);
		double dis = Math.sqrt(xdis+ydis);
		return dis;
	}
	public void removeDog (int index) {
		for (GObject obj: dogs.get(index).returnDog()) {
			remove(obj);
		}
		dogs.remove(index);
	}
	private void clashDog() {
		for (int i=0; i<dogs.size();i++) {
			for (int j=0; j<dogs.size();j++) {
				if(i < dogs.size() && j < dogs.size()) { 
					Dog dog1 = dogs.get(i);
					Dog dog2 = dogs.get(j);
					boolean isSame = areSameDogs(dog1,dog2);				
					if(!isSame) {
						double distance = getDogsDistance(dog1,dog2);
						double tolDis = 0.0;
						boolean isGhost = checkForSeriousCollision(i,j);
						boolean isSameD = dog1.isFacingLeft==dog2.isFacingLeft;
						if(isSameD) {
							tolDis = 2*(dog1.dsize+dog2.dsize);
						} else {
							tolDis = 3*(dog1.dsize+dog2.dsize);
						}
						if(distance<tolDis && !isGhost) {
							if(i<j) changeDogsDirection(i,j,isSameD);
						} else if(isGhost) {
							//dogs eat each other 	
							removeDog(i);
						}
					}
				}
			}
		}
	}
	private boolean checkForSeriousCollision(int di1, int di2) {
		int bigDog = 0;
		int smallDog = 0;
		if(dogs.get(di1).dsize >= dogs.get(di2).dsize) {
			bigDog = di1;
			smallDog = di2;
		} else {
			bigDog = di2;
			smallDog = di1;
		}
		//check whether smallDog is within the bigDog by checking head & body
		for (int i=0; i<2; i++) {
			for(int j=0; j<2;j++) {
				GPoint loc = dogs.get(smallDog).returnDog().get(j).getLocation();
				double dsize = dogs.get(smallDog).dsize;
				for(int w=0; w<2; w++) {
					for (int h=0; h<2; h++) {
						double checkX = loc.getX()+dsize*w;
						double checkY = loc.getY()+dsize*h;
						GPoint cpt = new GPoint(checkX, checkY);
						boolean isIn = dogs.get(bigDog).returnDog().get(i).contains(cpt);
						if(isIn) return true;						
					}
				}
			}
		}
		return false;
	}
	private void changeDogsDirection(int key1, int key2, boolean isSameD) {
		if(!isSameD) {
			dogs.get(key1).isFacingLeft = !dogs.get(key1).isFacingLeft;
			dogs.get(key1).goingDog();
			dogs.get(key2).goingDog();
		} else if (isSameD) {
			if(dogs.get(key1).getY()<dogs.get(key2).getY()) {
				dogs.get(key1).dy = rg.nextDouble(-1,-3);
				dogs.get(key2).dy = rg.nextDouble(1,3);
				dogs.get(key1).goingDog();
				dogs.get(key2).goingDog();
			} else {
				dogs.get(key1).dy = rg.nextDouble(1,3);
				dogs.get(key2).dy = rg.nextDouble(-1,-3);
				dogs.get(key1).goingDog();
				dogs.get(key2).goingDog();
			}
		}
	}

	/* Reference:
	 * - CS106A class codes (ex: Bouncing ball, fish tank) 
	 * - CLair help 
	 * - Section leader Maria for Q! 
	 * 
	 * Inspiration: 
	 * - Nintendog 
	 * 
	 * I dedicate this program to by puppy back home! 
	 * 
	 */
}
