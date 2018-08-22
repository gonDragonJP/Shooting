package takada.derivativeenemies;

import android.graphics.Point;
import android.graphics.PointF;
import takada.shooting.AnimationManager;
import takada.shooting.Enemy;
import takada.shooting.Global;
import takada.shooting.ObjectsContainer;
import takada.shooting.structure.EnemyData;
import takada.shooting.structure.EnemyData.CollisionRegion;

public class DE_Parts extends Enemy{
	
	PointF partsProportion = new PointF();
	Point tempChildPosition = new Point();

	public DE_Parts(ObjectsContainer container) {
		super(container);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setEnemyData(EnemyData enemyData, Point requestPos, Enemy parentEnemy){
		
		super.setEnemyData(enemyData, requestPos, parentEnemy);
		
		partsProportion.x = enemyData.startPosition.x / 100f;
		partsProportion.y = enemyData.startPosition.y / 100f;
		
		setPartsPosition();
		
		fx = x;		fy = y;
		
		setMovingNode(0);
	}

	public void setPartsPosition(){
		
		PointF drawSize = parentEnemy.getDrawSizeOfNormalAnime();
	
		int relativeX = (int)(drawSize.x * partsProportion.x);
		int relativeY = (int)(drawSize.y * partsProportion.y);
	
		double c = Math.cos(parentEnemy.drawAngle * Global.radian);
		double s = Math.sin(parentEnemy.drawAngle * Global.radian);
	
		x = parentEnemy.x + (int)(relativeX * c - relativeY * s);
		y = parentEnemy.y + (int)(relativeX * s + relativeY * c);
		
	}
	
	@Override
	public void periodicalProcess(){
		
		if(!isInExplosion){
			
			if(genNumber > genIndex) checkGeneratingCount();
		
			if(!checkNodeDuration()){
				isInScreen=false; 
				return;
			}
			
			if(colNumber>0 && oldDrawAngle!=drawAngle) rotateCollisionRegion();
		
			checkPartsParameter();
		}
		animate();
	}
	
	
	private void checkPartsParameter(){
		
		if(parentEnemy.isInScreen == false){
			
			isInScreen = false;
			return;
		}
		
		if(parentEnemy.isInExplosion == true){
			
			setExplosion();
			return;
		}
		
		setPartsPosition();
		
		velocity.copy(parentEnemy.velocity);
	}

}
