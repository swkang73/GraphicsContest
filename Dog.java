/*
 * File: Dog.java
 * --------------------------
 * Student: Sunwoo Kang 
 * Instructor: Maria 
 * __________________________
 * Title: new class type variable called dog 
 */

import acm.util.*;
import java.util.*;
import acm.io.*;
import acm.program.*;
import acm.graphics.*;
import acm.util.RandomGenerator.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Dog extends GCanvas implements PetConstants{
	/** private parameters used to set each dog*/
	public int dsize = 0;
	private Color dcolor;
	//dog location
	private double xloc, yloc; 
	//dog speed (displacement)
	public double dx, dy;
	//dog direction 
	public boolean isFacingLeft = false;
	//dog emotion
	public boolean isSad = false;
	//dog looking around motion 
	private boolean isPausing = false;
	private boolean isLookingAround = false;
	public int paused, pausingCount;
	private int lookNum,lookCount;
	public boolean ultimatebreak = false;
	public boolean ultimatemove = false;
	public int moved, movingCount;

	/*storage for the objects consisting a dog*/
	private ArrayList<GObject> dog = new ArrayList<GObject>();

	private RandomGenerator rg = new RandomGenerator();

	/*sub-variables of the new variable type dog*/
	public String name = null; 

	public Dog() {
		setBasics();
		makeHead();
		makeBody(); 
		makeTwoEars();
		isSad = rg.nextBoolean(0.5);
		makeFace(isSad);
		makeFourLegs();
		makeTail();	
		initialSpeed();
		facingDirection();
		matchDirection();
	}
	public void setDogPosition(double x, double y) {
		xloc = x;
		yloc = y;
		this.dog.clear();
		boolean check = this.dog.isEmpty();
		//reset dog 
		if(check){
			this.makeHead();
			this.makeBody();
			this.makeTwoEars();
			this.makeFace(isSad);
			this.makeFourLegs();
			this.makeTail();	
			this.initialSpeed();
			this.facingDirection();
			this.matchDirection();
		}
	}
	//give location of the dog's head
	public GPoint giveDogLocation() {
		if(!dog.isEmpty()) {
			double dogXloc = this.dog.get(0).getX();
			double dogYloc = this.dog.get(0).getY();
			GPoint loc = new GPoint (dogXloc, dogYloc);
			return loc;
		}
		return null;
	}
	public void resize(double sizeFactor){
		if(!dog.isEmpty()) {
			dsize = (int) Math.round(this.dsize*sizeFactor);
			this.dog.clear();
			this.makeHead();
			this.makeBody();
			this.makeTwoEars();
			this.makeFace(isSad);
			this.makeFourLegs();
			this.makeTail();	
			this.initialSpeed();
			this.facingDirection();
			this.matchDirection();
		}
	}
	public void setDogColor(Color newColor){
		this.dcolor = newColor;		
		this.dog.clear();
		//reset dog 
		this.makeHead();
		this.makeBody(); 
		this.makeTwoEars();
		this.makeFace(this.isSad);
		this.makeFourLegs();
		this.makeTail();	
		facingDirection();
		matchDirection();
	}
	public ArrayList<GObject> returnDog() {
		return dog;
	}
	public ArrayList<GObject> catchDog(double x, double y) {
		ArrayList<GObject> it = dog;
		for (GObject obj: dog) {
			if(obj.contains(x,y)) return it;
		}
		return null;
	}
	public void initialSpeed() {
		this.dx = rg.nextDouble(1.0,3.0);
		this.dy = rg.nextDouble(1.0, 3.0);
		if(rg.nextBoolean(0.5)) {
			this.dx *=-1;
			isFacingLeft = true;
		}
		if(rg.nextBoolean(0.5)) this.dy *= -1;
	}
	public void heartbeat() {
		//trigger pausing 
		if(ultimatemove && ultimatebreak) {
			ultimatemove = false;
		}
		if(!isPausing && !ultimatemove) {
			isPausing = rg.nextBoolean(0.02); 
			pausingCount = rg.nextInt(50,80);
		}
		if(!isPausing || ultimatemove) {	
			for(GObject obj: this.dog) {
				obj.move(dx,dy);
			}
			moved++;
			//how to avoid
			if(moved>movingCount) {
				ultimatemove = false; 
				moved = movingCount = 0;
			}			
		}
		if(isPausing || ultimatebreak) {
			//trigger looking around by chance 
			if (!isLookingAround && !ultimatebreak) {
				if(lookCount<lookNum) {
					lookAround();
					pausingCount += 100;
					isLookingAround = true;
				} else if(rg.nextBoolean(0.3)) {
					lookAround();
					pausingCount += 100;
					isLookingAround = true;
					lookNum = rg.nextInt(MAX_LOOK);
				}
			}
			//keep track of pausing time
			paused++; 
			//stop the pause
			if(paused>pausingCount) {
				isLookingAround = !isLookingAround;
				paused = 0; 
				pausingCount = 0;
				if(ultimatebreak) {
					ultimatebreak = false;
					isPausing = false;
					ultimatemove = true;
					movingCount+=40;
					return;					
				}
				lookCount++;
			}
			if(lookCount > lookNum) {
				isPausing = false;
				lookNum = 0;
				lookCount = 0;
				facingDirection();
				pausingCount += 50;
				ultimatebreak = true;
			}
		}
		reflectOffWalls();
		facingDirection();
	}
	public void goingDog() {		
		//this.isFacingLeft = !this.isFacingLeft;
		this.ultimatemove = true;
		this.movingCount=20;
		this.facingDirection();
		this.matchDirection();
		reflectOffWalls();
	}
	private void lookAround() {
		isFacingLeft = !isFacingLeft;
		facingDirection();
		initialSpeed();
		matchDirection();
	}
	//matches the direction of the dog 
	private void matchDirection() {
		if(isFacingLeft) dx = -Math.abs(dx);
		else if(!isFacingLeft) dx = Math.abs(dx);
	}
	//changes the direction of the dog
	private void facingDirection () {
		if(!dog.isEmpty()) {
			//catches the body for the position reference 
			double fixedX = dog.get(1).getX();
			double fixedY = dog.get(1).getY();
			double headX = 0.0;
			double headY = 0.0;
			//make the dog face right direction 
			if(!isFacingLeft) {
				headX = fixedX+dsize*4/3.0;
				headY = fixedY-dsize/1.4;	
			}
			else if (isFacingLeft) {	
				headX = fixedX-dsize/3.0;
				headY = fixedY-dsize/1.3;	
			}
			flipAll(headX,headY);
		}
	}
	private void flipAll(double hX, double hY) {
		if(!dog.isEmpty() && dog.size()>7) {
			//flip head
			flipDirection(0,hX, hY);
			//flip the ears (left first, right second)
			flipDirection(2,hX+dsize*0.2,hY+dsize*0.4);
			flipDirection(3,hX+dsize*1.1, hY+dsize*0.18);
			//flip the eyes
			flipDirection(4,hX+dsize/2-dsize/5,hY+dsize/3);
			flipDirection(5,hX+dsize/2+dsize/7, hY+dsize/3);
			//flip the nose
			flipDirection(6,hX+dsize*(4/9.0), hY+dsize*4/7.0);
			//flip the mouse 
			flipDirection(7,hX+dsize*(4/9.0)+dsize*0.03,
					hY+dsize*4/7.0+dsize*0.08);
			//flip the tail 
			if(isFacingLeft) {
				flipDirection(dog.size()-1,dog.get(1).getX()+dsize*1.8, hY+dsize);
			}
			else if (!isFacingLeft) {
				flipDirection(dog.size()-1,dog.get(1).getX()+dsize*.1, hY+dsize*.9);
			}
			//optional flipping if the dog has expression
			if(isSad && dog.size()>10) {
				flipDirection(8,hX+dsize*(12/25.0),hY+dsize*3/4.0);
				flipDirection(9, hX+dsize*(1/2.0),hY+dsize*3/4.0);
			}
		}
	}
	private void flipDirection (int index, double x, double y) {
		if(dog.contains(dog.get(index))) {
			GObject receiver = dog.get(index); 
			dog.remove(index);
			receiver.setLocation(x,y);
			if(index<4) receiver.sendToBack();
			dog.add(index,receiver);
		}
	}
	public void reflectOffWalls() {
		GObject navigator = dog.get(0);
		if(navigator.getX()<dsize) {
			dx = Math.abs(dx);
			isFacingLeft = false;
		}
		else if(navigator.getX()>APPLICATION_WIDTH-dsize*4.0) {
			dx = -Math.abs(dx);
			isFacingLeft = true;
		}
		if(navigator.getY()<dsize) {
			dy = Math.abs(dy);
		}
		else if(navigator.getY()>APPLICATION_HEIGHT-dsize*4.0) {
			dy = -Math.abs(dy);
		}
	}
	private void setBasics() {
		dsize = rg.nextInt(DOG_MIN_SIZE,DOG_MAX_SIZE);
		dcolor = rg.nextColor();
		//set initial position
		randomXandYloc();
		ArrayList<GObject> checker = catchDog(xloc,yloc);
		while(checker!=null) {
			randomXandYloc();
			checker = catchDog(xloc,yloc);
		}
	}	
	private void randomXandYloc () {
		xloc = rg.nextDouble(0.4*dsize, APPLICATION_WIDTH-dsize*3.5);
		yloc = rg.nextDouble(0.4*dsize, APPLICATION_HEIGHT-dsize*3.5);
	}
	private void makeHead() {
		GOval head = new GOval (dsize, dsize); 
		head.setFilled(true);
		head.setColor(dcolor); 
		head.setLocation(xloc,yloc);
		head.sendToFront();
		dog.add(head);
	}
	private void makeTwoEars() {
		addOneEar(xloc+dsize*0.2, yloc+dsize*0.4,true);
		addOneEar(xloc+dsize*1.1, yloc+dsize*0.18,false);
	}
	private void addOneEar(double earX, double earY, boolean isleftear) {
		GPolygon ear = new GPolygon (earX, earY);
		ear.addArc(dsize*0.4,dsize*0.7,ARC_WIDTH,ARC_HEIGHT);
		if(isleftear) ear.rotate(-35);
		else if(!isleftear) ear.rotate(35);
		ear.setFilled(true);
		ear.setColor(dcolor);
		ear.sendToFront();
		dog.add(ear);
	}
	private void makeTail() {
		GPolygon tail = new GPolygon (xloc + 2*dsize, yloc + dsize*1.2);
		tail.addArc(dsize*0.1, dsize, ARC_WIDTH, ARC_HEIGHT);
		tail.rotate(170);
		tail.setFilled(true);
		tail.setColor(dcolor);
		dog.add(tail);
	}
	private void makeFourLegs() {
		leg(0.15);
		leg(0.5);
		leg(1.05);
		leg(1.45);
	}
	private void leg(double pos) {
		GOval leg = new GOval (dsize*0.3, dsize);
		leg.setFilled(true);
		leg.setColor(dcolor);
		leg.setLocation(xloc + dsize/3.0+dsize*pos, yloc + dsize*1.2);
		dog.add(leg);
	}
	private void makeFace(boolean sad) {
		addOneEye(xloc+dsize/2-dsize/5,yloc+dsize/3);
		addOneEye(xloc+dsize/2+dsize/7, yloc+dsize/3);
		addNose(xloc + dsize*(4/9.0), yloc + dsize*4/7.0, sad);
	}
	private void addNose(double noseX , double noseY, boolean sad) {
		GOval nose = new GOval(dsize*0.08, dsize*0.08);
		nose.setFilled(true);
		nose.setLocation(noseX,noseY);
		dog.add(nose);
		addMouth(noseX+nose.getWidth()/2, noseY+nose.getHeight(),sad);
	}
	private void addOneEye(double eyeX, double eyeY) {
		GOval eye = new GOval (dsize*0.1, dsize*0.1);
		eye.setFilled(true);
		eye.setLocation(eyeX,eyeY);
		dog.add(eye);
	}
	private void addMouth(double noseXpos, double noseYpos, boolean sad) {
		GRect upmouth = new GRect (dsize*0.02,dsize/10);
		upmouth.setFilled(true);
		upmouth.setLocation(noseXpos-upmouth.getWidth()/2, noseYpos);
		dog.add(upmouth);
		if(sad) {
			addEmotion(noseXpos, noseYpos);
		}
	}
	private void addEmotion(double noseXpos, double noseYpos) {
		double upmouthEndX = noseXpos-dsize*0.01;
		double upmouthEndY = noseYpos+dsize/10;
		GLine leftmouth = new GLine (upmouthEndX, upmouthEndY,
				upmouthEndX-dsize*0.1, upmouthEndY+dsize*0.07);
		dog.add(leftmouth);
		GLine rightmouth = new GLine (noseXpos, upmouthEndY,
				upmouthEndX+dsize*0.1, upmouthEndY+dsize*0.07);
		dog.add(rightmouth);
	}
	private void makeBody() {
		GOval body = new GOval(dsize*1.8, dsize);
		body.setFilled(true);
		body.setColor(dcolor);
		body.setLocation(xloc + dsize/3.0, yloc + dsize/1.3);
		dog.add(body);
	}
}
