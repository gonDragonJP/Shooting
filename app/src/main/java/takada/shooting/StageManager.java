package takada.shooting;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.library.InitGL;
import takada.shooting.library.ScreenEffect;
import takada.shooting.structure.EnemyData;
import takada.shooting.structure.EventData;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class StageManager {
	
	Context context;

	public Global.StagePlace currentPlace = new Global().new StagePlace();
	
	public ArrayList<EnemyData> enemyList = new ArrayList<EnemyData>();
	public ArrayList<EventData> eventList = new ArrayList<EventData>();
	private FileAccess fileAccess;
	private StageEffect stageEffect;
	private EnemyGenerator enemyGenerator;
	private AnimationManager animationManager;
	
	private int stageLength[] = {8000,8000,8000,8000,8000};
	private boolean isStageShadowOn[] = {false,true,false,false,false};
		
	RectF tempRect = new RectF();

	
	public StageManager(Context context){
		
		fileAccess = new FileAccess(context);	
		stageEffect = new StageEffect(this);
		this.context = context;
		
		ScrollGraphicManager.setResources(context.getResources());
	}
	
	public void initialize(ObjectsContainer objectsContainer){
		
		enemyGenerator = objectsContainer.enemyGenerator;
		animationManager = objectsContainer.animationManager;
		fileAccess.setManager(objectsContainer.animationManager);
		stageEffect.initialize(objectsContainer);
		
		ScrollGraphicManager.initialize();
		
		InitGL.setupFont(
				context.getResources(), R.drawable.chrsheet,
				16, 16, 0
		);
		
		currentPlace.initialize(1);
	}
	
	public void prepareStarting(){
		
		if(!currentPlace.isPreparedScroll){
			ScrollGraphicManager.setupScroll(currentPlace);
		}
		
		if(!currentPlace.isPreparedStageData){
			setStageData();
		}
		
		stageEffect.startStageEffect();
		
		/*try {
			Thread.sleep(50);
		}
		catch (InterruptedException e) {

			e.printStackTrace();
		}*/
	}
	
	public void setStageData(){
		
		currentPlace.isShadowOn = isStageShadowOn[currentPlace.stage-1];
		
		animationManager.setStageEnemyTexSheet(currentPlace.stage);
		animationManager.initializeEnemyAnime();
	
		fileAccess.setEnemyList(enemyList, currentPlace.stage);
		fileAccess.setEventList(eventList, currentPlace.stage);
		
		currentPlace.isPreparedStageData = true;
	}
	
	synchronized public void onDraw(GL10 gl){
		
		ScrollGraphicManager.onDraw(currentPlace);
		
		tempRect.set(
				0, Global.virtualScreenSize.y, 
				200, Global.virtualScreenSize.y - 40);
		InitGL.drawText(
				tempRect,
				"ListSize:"+enemyGenerator.list.size()
				//"Stage:"+currentPlace.stage+" Scroll:"+currentPlace.scrollPoint
		);
		/*tempRect.set(0, screenY - 50, 200, screenY - 90);
		InitGL.drawText(
				tempRect,
				"ScreenX:"+screenX+" Y:"+screenY
		);*/
	}
	
	synchronized public void periodicalProcess(){
		
		currentPlace.scrollPoint += Global.scrollSpeedPerFrame;
		checkStageFinish();
		checkEvent(currentPlace.scrollPoint);		
	}
	
	private void checkEvent(int scrollPoint){
	
		int eventIndex = currentPlace.eventIndex;
		
		while(eventIndex < eventList.size()){
			EventData eventData = eventList.get(eventIndex);
		
			if (eventData.scrollPoint<=scrollPoint){ 
				
				switch(eventData.eventCategory){
				
				case ENEMYAPPEARANCE:
					addRootEnemy(eventData.eventObjectID);
					break;
					
				case BRIEFING:
					stageEffect.briefing(eventData.eventObjectID);
					stageEffect.stageEndPlaneCruising();
					break;
					
				case BOSSAPPEARANCE:
					addRootEnemy(eventData.eventObjectID);
					break;
					
				case PLAYBGM:
					BGMManager.playBGM(eventData.eventObjectID);
					break;
				}
				
				eventIndex++;
			}
			else break;	
		};
		
		currentPlace.eventIndex = eventIndex;
	}
	
	private void enemyAppearance(){
		
	}
	
	private void addRootEnemy(int objectID){
		
		int enemyIndex = getEnemyIndexFromObjectID(objectID);
		
		EnemyData enemyData = enemyList.get(enemyIndex);
			
		enemyGenerator.setEnemy(enemyData);
		enemyGenerator.addEnemy(null, null);
	}
	
	public Enemy addChildEnemy(int requestObjectID, Point requestStartPos, Enemy parentEnemy){
		
		int enemyIndex = getEnemyIndexFromObjectID(requestObjectID);
		
		EnemyData enemyData = enemyList.get(enemyIndex);
		
		enemyGenerator.setEnemy(enemyData);
		return enemyGenerator.addEnemy(requestStartPos, parentEnemy);
	}
	
	private int getEnemyIndexFromObjectID(int objectID){
		
		int index=-1;
		
		for(int i=0; i<enemyList.size(); i++){
			if(enemyList.get(i).objectID == objectID){
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	private void checkStageFinish(){
	
		if(currentPlace.scrollPoint > stageLength[currentPlace.stage-1])
			clearStage();
	}
	
	private void clearStage(){
		
		int nextStage = currentPlace.stage + 1;
		currentPlace.initialize(nextStage);
		checkClearGame();
	}
	
	private void checkClearGame(){

		if(currentPlace.stage > stageLength.length)
			clearGame();
	}
	
	private void clearGame(){
		//Šª‚«–ß‚µ
		currentPlace.initialize(1);
	}
}
