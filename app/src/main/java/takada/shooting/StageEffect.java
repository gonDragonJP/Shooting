package takada.shooting;

import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.library.*;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;

public class StageEffect {

	StageManager parent;
	private float screenX, screenY, screenCenterX, screenCenterY;
	private MyPlane plane;
	
	public StageEffect(StageManager parent){
		
		this.parent = parent;
	}
	
	public void initialize(ObjectsContainer container){
		
		plane = container.plane;
		setScreenSize();
	}
	
	private void setScreenSize(){
		
		screenX = Global.virtualScreenSize.x;
		screenY = Global.virtualScreenSize.y;
		screenCenterX = screenX / 2;
		screenCenterY = screenY / 2;
	}
	
	public void startStageEffect(){
		
		float edgeLength = (float)((screenX + screenY) / Math.sqrt(2));
		RectF wipeRect = new RectF(
				screenCenterX - edgeLength, screenCenterY - edgeLength,
				screenCenterX + edgeLength, screenCenterY + edgeLength);
		ScreenEffect.wipeScreen(wipeRect, 
				ScreenEffect.WipeKind.REEDSCREENWIPE, 45, true, 
				0, 3000, 0);
		
		ScreenEffect.cutinText(
				new RectF(0,0,0,0), 
				new RectF(screenCenterX - 100, screenCenterY + 25,
						screenCenterX + 100, screenCenterY - 25), 
				"Stage "+ parent.currentPlace.stage, 
				2000, 1000, 2000, 0, 720, null);
	}
	
	public void briefing(int eventObjectID){
	
		ScreenEffect.ChangingColor changingColor 
		= ScreenEffect.getChangingColor
		(Color.RED, Color.BLACK, 500, false, 0, 7001, 0);
		
		ScreenEffect.cutinText(
				new RectF(screenCenterX,0,screenCenterX,0), 
				new RectF(screenCenterX - 135, screenCenterY + 30,
						screenCenterX + 135, screenCenterY - 30),
				" Warning!", 0, 2000, 5000, 0, 0, changingColor
				);
		
		SoundEffect.play(SoundKind.SIREN);
		
		ScreenEffect.ChangingColor changingColor2 
		= ScreenEffect.getChangingColor
			(Color.argb(255, 0, 50, 50), Color.argb(255, 50, 200, 150),
					2000, true, 0, 7000, 0);
		
		ScreenEffect.typeOutText(
				new RectF(screenCenterX - 105, screenCenterY + 60,
						screenCenterX + 105, screenCenterY + 30),
				"Enormous Object", 100, 2000, 5000, 0, changingColor2
				);
		
		ScreenEffect.typeOutText(
				new RectF(screenCenterX - 105, screenCenterY + 90,
						screenCenterX + 105, screenCenterY + 60),
				"Type : Carrier", 100, 3400, 3600, 0, changingColor2
				);
	}
	
	public class CruisingProgram{
		
		float speedX, speedY, accY;
		boolean isFinished = false;
		
		int frameToCenter, frameOfBurner, frameToTop;
		
		public void initialize(){
			
			speedX = 2;
			speedY = 0;
			accY = -0.3f;
			frameOfBurner = 40;
			
			calcFrameToCenter();
			calcFrameToTop();
		}
		
		private void calcFrameToCenter(){
			
			float dx = Global.screenCenter.x - plane.x;
			if(dx<0) speedX *= -1;
		
			frameToCenter = Math.abs((int)(dx / speedX));
		}
		
		private void calcFrameToTop(){
			
			int dy = plane.y -(int)Global.virtualScreenLimit.top;
			float tempSpeedY = speedY;
			frameToTop = 0;
			
			do{
				dy += tempSpeedY;
				tempSpeedY += accY;
				frameToTop++;
			}while(dy>0);
		}
		
		public boolean crusing(){
			
			if(frameToCenter-->0){
				
				plane.velocity.x = speedX;
				plane.x +=speedX;
			}
			else if(frameOfBurner-->0){
				
				plane.velocity.x = 0;
				plane.isBurnerOn = true;
			}
			else if(frameToTop-->0){
				
				plane.y +=speedY;
				speedY += accY;
			}
			else{
				finish();
				return false;
			}
			
			return true;
		}
		
		private void finish(){
			
			isFinished = true;
			plane.isBurnerOn = false;
		}
	}
	
	public void stageEndPlaneCruising(){
		
		plane.setAutoCruising(new CruisingProgram());
	}
}
