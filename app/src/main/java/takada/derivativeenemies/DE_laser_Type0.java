package takada.derivativeenemies;

import takada.shooting.library.Double2Vector;
import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;

public class DE_laser_Type0 extends Enemy{

	Double2Vector relativePos = new Double2Vector();
	Double2Vector parentFaceOnUnitVec = new Double2Vector();
	
	public DE_laser_Type0(ObjectsContainer container) {
		super(container);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	public void setExplosion(){
		
	}

	@Override
	public void periodicalProcess(){
	
		super.periodicalProcess();
		
		if(parentEnemy.isInExplosion == true){
			isInScreen = false;
		}
	}
	
	@Override
	protected void flyAhead(){
	
		relativePos.x += velocity.x;
		relativePos.y += velocity.y;
		
		double distance = relativePos.length();
	
		velocity.plus(acceleration);
		
		parentFaceOnUnitVec = parentEnemy.getUnitVectorOfFaceOn();
	
		x = (int)(parentEnemy.x + parentFaceOnUnitVec.x * distance);
		y = (int)(parentEnemy.y + parentFaceOnUnitVec.y * distance);
	}
	
}