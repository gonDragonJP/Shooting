package takada.shooting;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.Global.ShotShape;
import takada.shooting.structure.EnemyData;
import takada.shooting.structure.EnemyData.MovingNode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import takada.derivativeenemies.DE_blocker_Type0;
import takada.shooting.library.Double2Vector;

public class EnemyGenerator {
	
	private ObjectsContainer objectsContainer;
	private StageManager stageManager;
	private DerivativeEnemyManager derivativeEnemyManager;
	
	ArrayList<Enemy> list = new ArrayList<Enemy>();
	EnemyData enemyData = new EnemyData();
	Point startPos = new Point();
	Double2Vector velocity = new Double2Vector();
	Double2Vector acceleration = new Double2Vector();
	Double2Vector homingAcceleration = new Double2Vector();
	
	
	public EnemyGenerator(){	
	}
	
	public void initialize(ObjectsContainer objectsContainer){
		
		this.objectsContainer = objectsContainer;
		stageManager = objectsContainer.stageManager;
		derivativeEnemyManager = objectsContainer.derivativeEnemyManager;
	}
	
	public void resetAllEnemies(){
		
		list.clear();
	}
	
	synchronized public void onDrawShadow(GL10 gl){
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).onDrawShadow(gl);
		}
	}
	
	synchronized public void onDrawGrounders(GL10 gl){
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).onDrawIfGrounder(gl);
		}
	}

	synchronized public void onDrawAirs(GL10 gl){
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).onDrawIfAir(gl);
		}
	}
	
	synchronized public void periodicalProcess(){
			
		childPeriodicalProcess();
		checkPositionLimit();
	}
	
	private void childPeriodicalProcess()
	{
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).periodicalProcess();
		}
	}
	
	private void checkPositionLimit(){
		
		int j=list.size()-1;
		for(int i=j; i>=0; i--){
			
			Enemy enemy = list.get(i);
			if(enemy.isInScreen == false) list.remove(i);
		}
	}
	
	public void setEnemy(EnemyData eData){
		
		this.enemyData = eData;	
	}
	
	public Enemy addEnemy(Point requestStartPos, Enemy parentEnemy){
		
		Enemy enemy;
		
		if(enemyData.isDerivativeType)
			
			enemy = derivativeEnemyManager.getDerivativeEnemy
				(stageManager.currentPlace.stage, enemyData);
		else
			enemy = new Enemy(objectsContainer);
		
		enemy.setEnemyData(enemyData, requestStartPos, parentEnemy);
		
		list.add(enemy);
		
		return enemy;
	}
	
	public Enemy requestGenerating
		(int requestObjectID, Point requestStartPos, Enemy parentEnemy){

		return stageManager.addChildEnemy
		(requestObjectID, requestStartPos, parentEnemy);
	}
}
