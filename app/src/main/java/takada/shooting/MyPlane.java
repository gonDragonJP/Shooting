package takada.shooting;

import javax.microedition.khronos.opengles.GL10;
import android.graphics.Point;
import android.graphics.PointF;
import takada.shooting.library.Double2Vector;
import takada.shooting.AnimationManager.AnimationData;
import takada.shooting.AnimationManager.AnimeKind;
import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.StageEffect.CruisingProgram;
import takada.shooting.library.InitGL;


public class MyPlane {
	
	final int drawSize = 64;
	final int collisionRadius = 16;
	final PointF size = new PointF(drawSize, drawSize);
	
	private int screenX, screenY, screenBottomLimit;
	private GraphicPad pad;
	private AnimationManager animationManager;
	private AnimationManager.AnimationSet 
		animeSet, chargingBallAnimeSet, chargedBallAnimeSet, conversionAnimeSet, 
		shieldAnimeSet, burnerAnimeSet;
	
	public int x, y;
	public static final float maxHP = 500;
	public int hitPoints = (int)maxHP;
	public boolean isNowCharging, isAlreadyCharged;
	public boolean isShielding = false;
	public boolean isNowConversion;
	public boolean isBurnerOn=false;
	
	private boolean isAutoCruisingMode = false;
	private CruisingProgram cruisingProgram;
	
	public Double2Vector velocity = new Double2Vector();
	private int maxSpeed = 6;
	private double animeDivSpeed = maxSpeed * 2 / 5d;
	private int moveAnimeFrameIndex;
	private int chargeBallAnimeFrameIndex, chargeAnimeFrame;
	private int conversionAnimeFrameIndex, conversionAnimeFrame;
	private int shieldAnimeFrameIndex, shieldAnimeFrame;
	private int burnerAnimeFrameIndex, burnerAnimeFrame;
	private PointF drawCenter = new PointF();

	public MyPlane(){	
	}
	
	public void initialize(ObjectsContainer objectsContainer){
		
		animationManager = objectsContainer.animationManager;
		pad = objectsContainer.pad;
		
		setScreenSize();
		setStartingState();
		
		animeSet = animationManager.getAnimationSet
				(AnimationManager.AnimeObject.MYPLANE, 0);
		
		chargingBallAnimeSet = animationManager.getAnimationSet
		(AnimationManager.AnimeObject.MYCHARGINGBALL, 0);
		
		chargedBallAnimeSet = animationManager.getAnimationSet
		(AnimationManager.AnimeObject.MYCHARGEDBALL, 0);
		
		conversionAnimeSet = animationManager.getAnimationSet
		(AnimationManager.AnimeObject.MYCONVERSION, 0);
		
		shieldAnimeSet = animationManager.getAnimationSet
		(AnimationManager.AnimeObject.MYSHIELD, 0);
		
		burnerAnimeSet = animationManager.getAnimationSet
		(AnimationManager.AnimeObject.MYBURNER, 0);
	}
	
	private void setScreenSize(){
		
		screenX = (int)Global.virtualScreenSize.x;
		screenY = (int)Global.virtualScreenSize.y;
		
		screenBottomLimit = screenY - drawSize;
	}
	
	public void setStartingState(){
		
		x = screenX / 2;
		y = screenY / 4 * 3;
		
		resetAnimeState();
	}
	
	private void resetAnimeState(){
	
		isNowCharging = isAlreadyCharged = isShielding = isNowConversion = false;
		
		moveAnimeFrameIndex = 2;
		chargeBallAnimeFrameIndex = chargeAnimeFrame = 0;
		conversionAnimeFrameIndex = conversionAnimeFrame = 0;
		shieldAnimeFrameIndex = shieldAnimeFrame = 0;
	}
	
	final float[] shadowColor = {0, 0, 0, 0};
	
	synchronized public void onDrawShadow(GL10 gl){
		
		InitGL.changeTexColor(shadowColor);
		
		drawCenter.set(x+Global.shadowDeflectionX, y+Global.shadowDeflectionY);
		
		animationManager.setFrame(
				animeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
				moveAnimeFrameIndex
				);
		animationManager.drawScaledFrame
			(drawCenter, Global.shadowScaleX, Global.shadowScaleY);
		
		InitGL.changeTexColor(null);
	}
	
	synchronized public void onDraw(GL10 gl){
				
		drawCenter.set(x, y);
		
		animationManager.setFrame(
				animeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
				moveAnimeFrameIndex
				);
		animationManager.drawFrame(drawCenter);
		
		if(isNowCharging){
			drawCenter.set(x, y-32);
		
			animationManager.setFrame(
					chargingBallAnimeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
					chargeBallAnimeFrameIndex
					);
			animationManager.drawFrame(drawCenter);
		}
		
		if(isAlreadyCharged){
			drawCenter.set(x, y-32);
			
				animationManager.setFrame(
						chargedBallAnimeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
						chargeBallAnimeFrameIndex
						);
				animationManager.drawFrame(drawCenter);
		}
		
		if(isNowConversion){
			drawCenter.set(x, y);
			
				animationManager.setFrame(
						conversionAnimeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
						conversionAnimeFrameIndex
						);
				animationManager.drawFrame(drawCenter);
		}
		
		if(isShielding){
			drawCenter.set(x, y);
			
				animationManager.setFrame(
						shieldAnimeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
						shieldAnimeFrameIndex
						);
				animationManager.drawFrame(drawCenter);
		}
		
		if(isBurnerOn){
			drawCenter.set(x, y+60);
			
				animationManager.setFrame(
						burnerAnimeSet.getData(AnimationManager.AnimeKind.NORMAL,0), 
						burnerAnimeFrameIndex
						);
				animationManager.drawFrame(drawCenter);
		}
	}
	
	synchronized public void periodicalProcess(){
		
		if(isAutoCruisingMode)
			isAutoCruisingMode = cruisingProgram.crusing();
		else getPadInput();
		
		changeAnimeFrame();
	}
	
	public void setDamaged(int enemyAtackPoint){
		
		if(!isShielding){
		
			hitPoints -= enemyAtackPoint;
		
			isShielding = true;
			shieldAnimeFrameIndex = 0;
			shieldAnimeFrame = 0;
			
			SoundEffect.play(SoundKind.SHIELDING);
		}
		
		if(hitPoints<0){
			
			hitPoints=0;
		}
	}
	
	public int getShieldEnergy(int energy){
		
		hitPoints += energy;
		
		int overPoints = hitPoints - (int)maxHP;
		if(overPoints>0){
			hitPoints = (int)maxHP;
			return overPoints;
		}
		
		return 0;
	}

	public void resetCharging(){
	
		isAlreadyCharged = false;
		isNowCharging = false;
		chargeAnimeFrame = 0;
	}
	
	public void setConversion(boolean isNowConversion){
		
		if((isNowConversion)&&(hitPoints>1)) this.isNowConversion = true;
		else {
			this.isNowConversion = false;
			conversionAnimeFrameIndex = 0;
			conversionAnimeFrame = 0;
		}
	}
	
	public boolean requestConversion(){
		
		if(hitPoints>1){
			
			hitPoints -=1; 
			return true;
		}
		
		return false;
	}
	
	public void setAutoCruising(CruisingProgram program){
		
		if(program==null){
			isAutoCruisingMode = false;
			return;
		}
		
		cruisingProgram = program;
		resetAnimeState();
		isAutoCruisingMode = true;
		cruisingProgram.initialize();
	}
	
	private void getPadInput(){
		
		if(pad==null || pad.isSetLeftPadCenter==false){
			
			velocity.set(0, 0);
			return;
		}
		
		velocity.set(pad.leftPadDirVector.x, pad.leftPadDirVector.y);
		velocity.limit(maxSpeed);
		
		x += velocity.x;
		y += velocity.y;
		
		limitPosition();
	}
	
	private void limitPosition(){
		
		if(x<0) x=0;
		if(y<0) y=0;
		if(x>screenX) x=screenX;
		if(y>screenBottomLimit) y=screenBottomLimit;
	}

	private void changeAnimeFrame(){
	
		moveAnimeFrameIndex = (int)((velocity.x + maxSpeed) / animeDivSpeed); 
		moveAnimeFrameIndex = moveAnimeFrameIndex > 4 ? 4 : moveAnimeFrameIndex;
		
		if(isNowCharging){
		
			chargeBallAnimeFrameIndex = animationManager.checkAnimeLimit
				(chargingBallAnimeSet.getData(AnimeKind.NORMAL, 0), ++chargeAnimeFrame);
			if(chargeBallAnimeFrameIndex == -1) {
				isNowCharging = false;
				isAlreadyCharged = true;
				chargeAnimeFrame = 0;
			}
		}
		
		if(isAlreadyCharged){
			
			chargeBallAnimeFrameIndex = animationManager.checkAnimeLimit
				(chargedBallAnimeSet.getData(AnimeKind.NORMAL,0), ++chargeAnimeFrame);
		}
		
		if(isNowConversion){
			
			conversionAnimeFrameIndex = animationManager.checkAnimeLimit
				(conversionAnimeSet.getData(AnimeKind.NORMAL,0),++conversionAnimeFrame);
		}
		
		if(isShielding){
			
			shieldAnimeFrameIndex = animationManager.checkAnimeLimit
				(shieldAnimeSet.getData(AnimeKind.NORMAL,0),++shieldAnimeFrame);
			
			if(shieldAnimeFrameIndex == -1){
				
				isShielding = false;
			}
		}
		
		if(isBurnerOn){
			
			burnerAnimeFrameIndex = animationManager.checkAnimeLimit
				(burnerAnimeSet.getData(AnimeKind.NORMAL,0), ++burnerAnimeFrame);
		}
	}

}
