package takada.shooting;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Paint.Style;

import takada.shooting.library.Double2Vector;
import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.library.InitGL;

public class MyShotGenerator {
	
	final int laserNumber = 25;
	
	Context context;

	private GraphicPad pad;
	private MyPlane plane;
	private AnimationManager animationManager;
	private ObjectsContainer objectsContainer;
	
	ArrayList<MyShot> list = new ArrayList<MyShot>();
	
	Point startPos = new Point();
	Double2Vector velocity = new Double2Vector();
	Global.ShotShape shape = new Global().new ShotShape();
	final double radian = Global.radian;
	
	float maxWeaponEnergy = 500;
	int weaponEnergy = 0;
	int weaponLevel;
	
	boolean isLaser;
	int shotPower;
	
	int laserCount;
	MyLaser previousLaser;
	
	Long currentTime=0l, shotTime=0l, shotTime2=0l, shotTime3=0l, shotTime4=0l;
	Long chargeSEStartTime=0l;
	int sprayAngle;
	
	public MyShotGenerator(Context context){
		
		this.context = context;
	}
	
	public void initialize(ObjectsContainer objectsContainer){
		
		pad = objectsContainer.pad;
		plane = objectsContainer.plane;
		animationManager = objectsContainer.animationManager;
		this.objectsContainer = objectsContainer;
	}
	
	public void resetAllShots(){
		
		list.clear();
	}
	
	public void getWeaponEnergy(int energy){
		
		weaponEnergy += energy;
		if (weaponEnergy > maxWeaponEnergy) weaponEnergy = (int)maxWeaponEnergy;
	}
	
	
	@SuppressLint("WrongCall")
	synchronized public void onDraw(GL10 gl){
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).onDraw(gl);
		}
	}
	
	synchronized public void periodicalProcess(){
		
		if(!shotLaser()){
			
			getPadInput();
			checkConversion();
		}
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).periodicalProcess();
		}
		
		checkPositionLimit();
	}
	
	private boolean shotLaser(){
		
		if(laserCount==0) return false;
		
		if(addLaserShot()) laserCount--;
		
		return true;
	}
	
	private void getPadInput(){
		
		currentTime = System.currentTimeMillis();
		
		if(!pad.isSetRightPadCenter) {
			checkStartLaser(); 
			plane.setConversion(false);
			return;
		}
		
		if(pad.rightPadDirVector.y>10){
			
			if(currentTime> chargeSEStartTime + 500){
			
				SoundEffect.play(SoundKind.CHARGE);
				chargeSEStartTime = currentTime;
			}
			
			if(!plane.isAlreadyCharged)
				plane.isNowCharging = true;
			return;
		}
		else checkStartLaser();
		
		checkWeaponLevel();
		
		if(pad.rightPadDirVector.y<-10){
			
			if(currentTime> chargeSEStartTime + 500){
			
				SoundEffect.play(SoundKind.CHARGE);
				chargeSEStartTime = currentTime;
			}
			
			plane.setConversion(true);
			return;
		}
		else plane.setConversion(false);
	
		switch(weaponLevel){
		
			case 3: addSprayShot();
			case 2: addLateralShots();
			case 1: addDualShots();
			default: addSingleShot();
		}
	}
	
	private void checkStartLaser(){
		
		if(plane.isAlreadyCharged){
			
			laserCount = laserNumber;
			previousLaser = null;
			
			SoundEffect.play(SoundKind.MYLASER);
		}
		
		plane.resetCharging();
	}
	
	private void checkWeaponLevel(){
		
		if(weaponEnergy<0) {weaponEnergy=0; weaponLevel=0;}
		
		int currentLevel = (int)(weaponEnergy / maxWeaponEnergy * 3);
		
		if(currentLevel>=weaponLevel) weaponLevel = currentLevel;
		if(currentLevel<(weaponLevel-1)) weaponLevel--;
	}
	
	private void checkConversion(){
		
		if(plane.isNowConversion){
			
			if(weaponEnergy == (int)maxWeaponEnergy){
				
				plane.setConversion(false);
				return;
			}
			if(plane.requestConversion()) weaponEnergy +=1;
		}
	}
	
	private void checkPositionLimit(){
		
		int j=list.size()-1;
		for(int i=j; i>=0; i--){
			
			MyShot shot = list.get(i);
			if(shot.isInScreen == false) list.remove(i);
		}
	}
	
	private boolean addLaserShot(){
		
		isLaser = true;
		
		startPos.set(plane.x, plane.y - plane.drawSize / 2);
		velocity.set(0, -16);
		
		MyLaser newShot = new MyLaser(objectsContainer);

		switch(laserCount){
		
			case 1:
				newShot.normalFrameIndex = 2;
				break;
				
			case laserNumber:
				newShot.normalFrameIndex = 0;
				break;
				
			default:
				newShot.normalFrameIndex = 1;
		}
		
		newShot.setShape(isLaser);
		newShot.setVectors(startPos, velocity);
		newShot.shotPower = 100;
		newShot.shotRadius = 16;
		newShot.setFromPreviousLaser(previousLaser);
		
		list.add(newShot);
		previousLaser = newShot;
		return true;
	}
	
	private void addAShot(){
		
		isLaser = false;
		
		MyShot newShot = new MyShot(objectsContainer);
		newShot.setShape(isLaser);
		newShot.setVectors(startPos, velocity);
		newShot.shotPower = shotPower;
		newShot.shotRadius = 12;
		list.add(newShot);
	}
	
	private void addSingleShot(){
		
		if(currentTime< shotTime+150) return;
				
		startPos.set(plane.x, plane.y - plane.drawSize / 3);
		velocity.set(0, -10);
		shotPower = 100;
		addAShot();
		
		shotTime = currentTime;
		
		SoundEffect.play(SoundKind.MYSHOT);
	}
	
	private void addDualShots(){
		
		if(currentTime< shotTime2+200) return;
		
		shotPower = 50;
		
		startPos.set(plane.x + 5, plane.y - plane.drawSize / 3);
		velocity.set(2, -8);
		addAShot();
		
		startPos.set(plane.x - 5, plane.y - plane.drawSize / 3);
		velocity.set(-2, -8);
		addAShot();
		
		shotTime2 = currentTime;
		weaponEnergy -= 3;
	}
	
	private void addLateralShots(){
		
		if(currentTime< shotTime3+500) return;
		
		shotPower = 50;
		
		startPos.set(plane.x + 5, plane.y - plane.drawSize / 5);
		velocity.set(4, -4);
		addAShot();
		
		startPos.set(plane.x - 5, plane.y - plane.drawSize / 5);
		velocity.set(-4, -4);
		addAShot();
		
		shotTime3 = currentTime;
		weaponEnergy -= 4;
	}
	
	private void addSprayShot(){
		
		if(currentTime< shotTime4+20) return;
		
		shotPower = 50;
		
		startPos.set(plane.x, plane.y - plane.drawSize / 3);
		sprayAngle = (sprayAngle + 15) % 360;
		double angle = Math.cos(sprayAngle * radian) - 1.57;
		double vx = Math.cos(angle) * 10;
	    double vy = Math.sin(angle) * 10;	
		velocity.set(vx, vy);
		addAShot();
	
		shotTime4 = currentTime;
		weaponEnergy -= 5;
	}	
}
