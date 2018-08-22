package takada.derivativeenemies;

import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;
import takada.shooting.SoundEffect;
import takada.shooting.SoundEffect.SoundKind;

public class DE_harricane extends Enemy{
	
	static int frameCountOfSE;
	static final int thresholdDistance = 110;
	static int pullPower;
	
	private double distanceToPlane;

	public DE_harricane(ObjectsContainer container) {
		super(container);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void periodicalProcess(){
	 super.periodicalProcess();
	 
	 /*if(!isFinishedInitialSound){
		 SoundEffect.play(SoundKind.EXPLOSION1);
		 isFinishedInitialSound = true;
	 }*/
	 
	 if (checkApproachingPlane()){
		 
		 pullPlane();
		 
		 if(frameCountOfSE==0){
			 float volumeRate 
			 	= 0.5f +  0.5f- (float)distanceToPlane/thresholdDistance/2;
			 SoundEffect.play(SoundKind.HARRICANE, volumeRate);
		 	frameCountOfSE=40;
		 }
	 }
	 
	 if(frameCountOfSE>0) frameCountOfSE--;
	}
	
	private boolean checkApproachingPlane(){
	
		eVelocity.set(plane.x-x, plane.y-y);
		
		distanceToPlane = eVelocity.length();
		if (distanceToPlane ==0) return false;
		else return (distanceToPlane < thresholdDistance);
	}
	
	private void pullPlane(){
		
		pullPower = (distanceToPlane * 2 < thresholdDistance) ? 3 : 2; 
		eVelocity.normalize(pullPower);
		
		plane.x -= eVelocity.x;
		plane.y -= eVelocity.y;
	}

}
