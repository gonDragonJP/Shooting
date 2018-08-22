package takada.shooting;

import java.util.Hashtable;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.library.InitGL;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;

public class AnimationManager {
	
	public enum AnimeObject {
		MYPLANE, MYBULLET, MYCHARGINGBALL, MYCHARGEDBALL, MYCONVERSION, MYLASER, MYSHIELD,
		MYBURNER, SHIELDENERGY, WEAPONENERGY, ENEMY};
	
	public enum AnimeKind {NORMAL, EXPLOSION, NODEACTION, SPECIAL};
	
	public enum RepeatAttribute {LOOP, ONCE, STOP};
	
	public enum RotateAttribute
				{DEFAULT, TENDTOPLANE, CLOCKWISE, TENDAHEAD, 
		TENDPARENTFACEON, SETPARENTFACEONANDCW};
	
	Context context;
	
	public class TextureSheet{
		
		int textureID, resourceID;
		int frameNumberX, frameNumberY
		;
		RectF texRect = new RectF();
		
		public TextureSheet(int resourceID, int nx, int ny){
			
			textureID = InitGL.loadTexture(context.getResources(), resourceID);
			this.resourceID = resourceID;
			this.frameNumberX = nx;
			this.frameNumberY = ny;
		}
		
		public RectF getTexPositionRect(int frameIndex){
			
			final float texFrameSizeX = 1.0f / frameNumberX;
			final float texFrameSizeY = 1.0f / frameNumberY;
			
			float left  = (frameIndex % frameNumberX) * texFrameSizeX;
			float right = left + texFrameSizeX;
			float top   = (frameIndex / frameNumberX) * texFrameSizeY;;
			float bottom= top + texFrameSizeY;
			
			texRect.set(left, bottom, right, top);
			
			return texRect;
		}
		
		public void release(){
			
			InitGL.TextureManager.deleteTexture(resourceID);
		}
	}
	
	public class AnimationData{
		
		PointF drawSize = new PointF();
		TextureSheet textureSheet;
		int frameOffset, frameNumber, frameInterval;
		RepeatAttribute repeatAttribute;
		RotateAttribute rotateAction;
		int rotateOffset;
		double angularVelocity;
		
		public AnimationData(){
			
		}
	}
	
	public class AnimationSet{
	
		AnimationData normalAnime = new AnimationData();
		AnimationData explosionAnime = new AnimationData();
		Map<Integer, AnimationData> nodeActionAnime
		  = new Hashtable<Integer, AnimationData>();
		
		public AnimationData getData(AnimeKind kind, int nodeIndex){
			
			switch(kind){
			
			case NORMAL:
				return normalAnime;
				
			case EXPLOSION:
				return explosionAnime;
				
			case NODEACTION:
				return nodeActionAnime.get(nodeIndex);
		
			default: 
				return null;
			}
		}
	}
	
	AnimationSet myPlaneSet = new AnimationSet();
	AnimationSet myBulletSet = new AnimationSet();
	AnimationSet myChargingBallSet = new AnimationSet();
	AnimationSet myChargedBallSet = new AnimationSet();
	AnimationSet myConversionSet = new AnimationSet();
	AnimationSet myLaserSet = new AnimationSet();
	AnimationSet myShieldSet = new AnimationSet();
	AnimationSet myBurnerSet = new AnimationSet();
	AnimationSet shieldEnergySet = new AnimationSet();
	AnimationSet weaponEnergySet = new AnimationSet();
	public Map<Integer, AnimationSet> enemyAnimationMap
		= new Hashtable<Integer, AnimationSet>();
	
	TextureSheet planeTex;
	TextureSheet effectTex0, effectTex1, effectTex2, effectTex3;
	TextureSheet bulletTex1, bulletTex2;
	TextureSheet itemTex;
	
	final int stageLimitedEnemyTexSheetNumber = 9;
	final int maxEnemyTexSheetNumber = 30;
	TextureSheet[] enemyTex = new TextureSheet[maxEnemyTexSheetNumber];
	
	public AnimationManager(Context context){
		
		this.context = context;
	}
	
	public void initialize(){
		
		planeTex = new TextureSheet(R.drawable.myplanesheet, 4, 4);
		effectTex0 = new TextureSheet(R.drawable.effect_sheet000, 8, 8);
		effectTex1 = new TextureSheet(R.drawable.effect_sheet001, 8, 8);
		effectTex2 = new TextureSheet(R.drawable.effect_sheet002, 4, 4);
		effectTex3 = new TextureSheet(R.drawable.effect_sheet003, 8, 8);
		bulletTex1 = new TextureSheet(R.drawable.bullet_sheet000, 8, 8);
		bulletTex2 = new TextureSheet(R.drawable.bullet_sheet001, 8, 8);
		itemTex = new TextureSheet(R.drawable.item_sheet, 8, 8);
		
		initializeMyPlaneAnime();
		initializeMyBulletAnime();
		initializeMyChargingBallAnime();
		initializeItemAnime();
		
		enemyTex[10] = bulletTex1;
		enemyTex[11] = bulletTex2;
		enemyTex[20] = effectTex1;
		enemyTex[21] = effectTex2;
	}
	
	private void clearStageLimitedEnemyTexSheets(){
		
		for(int i=0; i<stageLimitedEnemyTexSheetNumber; i++){
			if(enemyTex[i]!=null){
				enemyTex[i].release();
				enemyTex[i]=null;
			}
		}
	}
	
	public void setStageEnemyTexSheet(int stage){
		
		clearStageLimitedEnemyTexSheets();
		
		switch(stage){
		
		case 1:
		
			enemyTex[0]= new TextureSheet(R.drawable.enemy_sheet001, 8, 8);
			enemyTex[1]= new TextureSheet(R.drawable.enemy_sheet000, 8, 8);
			enemyTex[2]= new TextureSheet(R.drawable.enemy_sheet002, 8, 8);
			enemyTex[3]= new TextureSheet(R.drawable.enemy_sheet003, 8, 8);
			enemyTex[5]= new TextureSheet(R.drawable.midenemy_sheet000, 4, 2);
			enemyTex[8]= new TextureSheet(R.drawable.boss01_sheet, 1, 1);
			
		break;
		
		case 2:
			
			enemyTex[0]= new TextureSheet(R.drawable.enemy_sheet001, 8, 8);
			enemyTex[1]= new TextureSheet(R.drawable.enemy_sheet000, 8, 8);
			enemyTex[2]= new TextureSheet(R.drawable.enemy_sheet002, 8, 8);
			enemyTex[3]= new TextureSheet(R.drawable.enemy_sheet003, 8, 8);
			enemyTex[4]= new TextureSheet(R.drawable.enemy_sheet004, 8, 8);
			enemyTex[5]= new TextureSheet(R.drawable.midenemy_sheet000, 4, 2);
			enemyTex[6]= new TextureSheet(R.drawable.midenemy_sheet001, 4, 4);
			enemyTex[8]= new TextureSheet(R.drawable.boss01_sheet, 1, 1);
			
		break;
		}
	}
	
	private void initializeMyPlaneAnime(){
	
		myPlaneSet.normalAnime.drawSize.set(64, 64);
		myPlaneSet.normalAnime.textureSheet = planeTex;
		myPlaneSet.normalAnime.frameOffset = 0;
		
		myShieldSet.normalAnime.drawSize.set(64, 64);
		myShieldSet.normalAnime.textureSheet = effectTex0;
		myShieldSet.normalAnime.frameOffset = 48;
		myShieldSet.normalAnime.frameNumber = 12;
		myShieldSet.normalAnime.frameInterval = 3;
		myShieldSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.STOP;
		
		myConversionSet.normalAnime.drawSize.set(64, 64);
		myConversionSet.normalAnime.textureSheet = effectTex0;
		myConversionSet.normalAnime.frameOffset = 20;
		myConversionSet.normalAnime.frameNumber = 20;
		myConversionSet.normalAnime.frameInterval = 1;
		myConversionSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
		
		myBurnerSet.normalAnime.drawSize.set(32, 64);
		myBurnerSet.normalAnime.textureSheet = effectTex3;
		myBurnerSet.normalAnime.frameOffset = 0;
		myBurnerSet.normalAnime.frameNumber = 15;
		myBurnerSet.normalAnime.frameInterval = 1;
		myBurnerSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
	}
	
	private void initializeMyBulletAnime(){
		
		myBulletSet.normalAnime.drawSize.set(16, 16);
		myBulletSet.normalAnime.textureSheet = bulletTex1;
		myBulletSet.normalAnime.frameOffset = 16;
		myBulletSet.normalAnime.frameNumber = 4;
		myBulletSet.normalAnime.frameInterval = 1;
		myBulletSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
		
		myBulletSet.explosionAnime.drawSize.set(16, 16);
		myBulletSet.explosionAnime.textureSheet = bulletTex1;
		myBulletSet.explosionAnime.frameOffset = 8;
		myBulletSet.explosionAnime.frameNumber = 4;
		myBulletSet.explosionAnime.frameInterval = 3;
		myBulletSet.explosionAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.STOP;
		
		myLaserSet.normalAnime.drawSize.set(32, 32);
		myLaserSet.normalAnime.textureSheet = bulletTex2;
		myLaserSet.normalAnime.frameOffset = 0;
		myLaserSet.normalAnime.frameNumber = 1;
		myLaserSet.normalAnime.frameInterval = 1;
		myLaserSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.ONCE;
		
		myLaserSet.explosionAnime.drawSize.set(64, 64);
		myLaserSet.explosionAnime.textureSheet = effectTex0;
		myLaserSet.explosionAnime.frameOffset = 40;
		myLaserSet.explosionAnime.frameNumber = 8;
		myLaserSet.explosionAnime.frameInterval = 2;
		myLaserSet.explosionAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
	}
	
	private void initializeMyChargingBallAnime(){
		
		myChargingBallSet.normalAnime.drawSize.set(64, 64);
		myChargingBallSet.normalAnime.textureSheet = effectTex0;
		myChargingBallSet.normalAnime.frameOffset = 0;
		myChargingBallSet.normalAnime.frameNumber = 10;
		myChargingBallSet.normalAnime.frameInterval = 6;
		myChargingBallSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.STOP;
		
		myChargedBallSet.normalAnime.drawSize.set(64, 64);
		myChargedBallSet.normalAnime.textureSheet = effectTex0;
		myChargedBallSet.normalAnime.frameOffset = 10;
		myChargedBallSet.normalAnime.frameNumber = 10;
		myChargedBallSet.normalAnime.frameInterval = 3;
		myChargedBallSet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
	}
	
	private void initializeItemAnime(){
		
		shieldEnergySet.normalAnime.drawSize.set(32, 32);
		shieldEnergySet.normalAnime.textureSheet = itemTex;
		shieldEnergySet.normalAnime.frameOffset = 0;
		shieldEnergySet.normalAnime.frameNumber = 15;
		shieldEnergySet.normalAnime.frameInterval = 4;
		shieldEnergySet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
		
		weaponEnergySet.normalAnime.drawSize.set(32, 32);
		weaponEnergySet.normalAnime.textureSheet = itemTex;
		weaponEnergySet.normalAnime.frameOffset = 16;
		weaponEnergySet.normalAnime.frameNumber = 15;
		weaponEnergySet.normalAnime.frameInterval = 4;
		weaponEnergySet.normalAnime.repeatAttribute = 
			AnimationManager.RepeatAttribute.LOOP;
	}
	
	public void initializeEnemyAnime(){
		
		enemyAnimationMap.clear();
	}
	
	public void setEnemyAnimationSet(int objectID, AnimationSet animeSet){
		
		enemyAnimationMap.put(objectID, animeSet);
	}
	
	public AnimationSet getAnimationSet(AnimeObject object, int objectID){
		
		AnimationSet drawSet;
	
		switch(object){
		
			case MYPLANE:
				
				drawSet = myPlaneSet;
				break;
				
			case MYBULLET:
				
				drawSet = myBulletSet;
				break;
				
			case MYCHARGINGBALL:
				
				drawSet = myChargingBallSet;
				break;
				
			case MYCHARGEDBALL:
				
				drawSet = myChargedBallSet;
				break;
				
			case MYCONVERSION:
				
				drawSet = myConversionSet;
				break;
				
			case MYLASER:
				
				drawSet = myLaserSet;
				break;
				
			case MYSHIELD:
				
				drawSet = myShieldSet;
				break;
				
			case MYBURNER:
				
				drawSet = myBurnerSet;
				break;
	
			case ENEMY:
		
				drawSet=enemyAnimationMap.get(objectID);
				break;
				
			case SHIELDENERGY:
				
				drawSet = shieldEnergySet;
				break;
				
			case WEAPONENERGY:
				
				drawSet = weaponEnergySet;
				break;	
				
			default: 
				return null;
		}
		
		return drawSet;
	}
	
	private TextureSheet drawSheet;
	private PointF drawSize;
	private PointF tempSize = new PointF();
	
	public void setFrame
		(AnimationData data, int animationFrame){	
			
		drawSheet = data.textureSheet;
		drawSize = data.drawSize;
		int drawSheetOffset = data.frameOffset;
		
		InitGL.setTextureSTCoords(
				drawSheet.getTexPositionRect(animationFrame + drawSheetOffset)
				);
	}
	
	public double getEnemyRotateAngle(Enemy object, boolean isInitialAngle){
		
		AnimationData data = object.drawAnimeData;
		double angle = data.rotateOffset;
				
		switch(data.rotateAction){
		
			case DEFAULT:
				
				break;
		
			case TENDTOPLANE:
				
				angle += object.getAngleOfTendToPlane();
				break;
			
			case CLOCKWISE:
				
				if(isInitialAngle) return angle;
				angle = object.drawAngle + data.angularVelocity;
				break;
				
			case TENDAHEAD:
				
				angle += object.getAngleOfTendAhead();
				break;
				
			case TENDPARENTFACEON:
				
				if(object.parentEnemy != null)
					angle += object.parentEnemy.drawAngle;
				break;
				
			case SETPARENTFACEONANDCW:
				
				if(isInitialAngle){
					if(object.parentEnemy != null)
						angle += object.parentEnemy.drawAngle;
					break;
				}
				angle = object.drawAngle + data.angularVelocity;
				break;
				
			default:
				angle = 0;
		}
		
		return angle;
	}
	
	public void drawFrame(PointF center){
		
		InitGL.drawTexture(center, drawSize, drawSheet.textureID);
	}
	
	public void drawScaledFrame(PointF center, float scaleX, float scaleY){
		
		tempSize.x = drawSize.x * scaleX;
		tempSize.y = drawSize.y * scaleY;
		InitGL.drawTexture(center, tempSize, drawSheet.textureID);
	}
	
	public void drawFlexibleFrame(PointF center, PointF tempSize){
		
		InitGL.drawTexture(center, tempSize, drawSheet.textureID);
	}
	
	public int checkAnimeLimit(AnimationData drawAnimeData, int totalFrame){
		
		int frameNumber, frameInterval, animeFrame;
		RepeatAttribute repeatAttrib;
		
		frameNumber = drawAnimeData.frameNumber;
		frameInterval = drawAnimeData.frameInterval;
		repeatAttrib = drawAnimeData.repeatAttribute;
		
		if(frameInterval==0) return 0;
		animeFrame = totalFrame / frameInterval;
		
		if(animeFrame>=frameNumber){
			
			switch(repeatAttrib){
			
				case LOOP:
					break;
					
				case ONCE:
					return frameNumber-1;
					
				case STOP:
					return -1;
			}
		}
		
		return animeFrame % frameNumber;
	}
}
