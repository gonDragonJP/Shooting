package takada.derivativeenemies;

import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;

public class DE_blocker_Type0 extends Enemy{
	
	private double evaAngle1, evaAngle2;

	public DE_blocker_Type0(ObjectsContainer container) {
		super(container);
		
	}
	
	protected void checkGeneratingCount(){
		
		if(++genFrameCount<genStartFrame) return;

		if(checkSightOnPlane())	requestGenerating();
		else{
			
			--genFrameCount;
			return;
		}

		if (--genRepeat<1) {
			
			if (++genIndex < genNumber) setGenerating(genIndex);
		}
		else 
			genStartFrame +=genIntervalFrame;	
	
	}
	
	private boolean checkSightOnPlane(){
		
		evaAngle1 = getAngleOfTendToPlane();
		evaAngle2 = drawAngle;
		
		if(Math.abs(evaAngle1 - evaAngle2)<25) return true;
		
		return false;
	}

}
