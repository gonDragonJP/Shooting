package takada.shooting;

import takada.shooting.Global.StagePlace;

public class GameThread extends Thread{
	
	ObjectsContainer objects;
	
	boolean isActive = true;
	
	public GameThread(ObjectsContainer objects){
	
		this.objects = objects;
	}
	
	public void resumeThread(){
		
		isActive = true;
	}
	
	public void pauseThread(){
	
		isActive = false;
	}
	
	@Override
	public void run(){
		
		waitInitializeObjects();
		
		final int frameIntervalTime = 1000 / 40;
		long lastUpdateTime = System.currentTimeMillis();
		
		while(true){
			
			if(!isActive){
				
				lastUpdateTime = System.currentTimeMillis();
				continue;
			}
		
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
	
			long nowTime = System.currentTimeMillis();
			long difference = nowTime - lastUpdateTime;
		
			while(difference >= frameIntervalTime){
				difference -=frameIntervalTime;
				
				if(objects.stageManager.currentPlace.isStarted) update();
			}
		
			lastUpdateTime = nowTime - difference;
		
		}
	}
	
	private void waitInitializeObjects(){
		
		while(!objects.isInitialized){
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
		}
	}
	
	public void update(){
		
		doPeriodicalProcess();
		checkDetection();
	}
	
	private void doPeriodicalProcess(){
		
		objects.stageManager.periodicalProcess();
		objects.plane.periodicalProcess();
		objects.shotGenerator.periodicalProcess();
		objects.enemyGenerator.periodicalProcess();
		objects.itemGenerator.periodicalProcess();
	}
	
	private void checkDetection(){
		
		objects.collisionDetection.doAllDetection();
	}
}
