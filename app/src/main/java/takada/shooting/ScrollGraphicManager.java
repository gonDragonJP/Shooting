package takada.shooting;

import takada.shooting.Global.StagePlace;
import takada.shooting.library.InitGL;
import takada.shooting.library.InitGL.TextureManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;

public class ScrollGraphicManager {
	
	static private Resources resources;
	static private float screenX, screenY, screenCenterX, screenCenterY;
	
	static private Bitmap scrollBitmap;
	static private final int maxStageTexNumber = 5;
	static private final int[] stageTexNumber = {1, 4, 1, 1, 1, 1, 1};
	static private final int[] stageTexResID = new int[maxStageTexNumber];
	static private int[] scrollTexID = new int[maxStageTexNumber];
	
	static private PointF scrollCenter = new PointF();
	static private PointF scrollTexDrawSize = new PointF();
	static private RectF tempRect = new RectF();
	static private float scrollOffset;
	
	static public void setResources(Resources res){
		
		resources = res;
	}
	
	static public void initialize(){
		
		setScreenSize();
	}
	
	static private void setScreenSize(){
		
		screenX = Global.virtualScreenSize.x;
		screenY = Global.virtualScreenSize.y;
		screenCenterX = screenX / 2;
		screenCenterY = screenY / 2;
		
		scrollTexDrawSize.set(
				Global.virtualScreenLimit.width(),
				Global.virtualScreenLimit.height()
				);
		
		scrollOffset = screenY - scrollTexDrawSize.y / 2;
	}
	
	static public void setupScroll(StagePlace place){
		
		freeTexture();
		
		switch(place.stage){
		
		case 1:
			stageTexResID[0] = R.drawable.bg_stg1_1;
			break;
			
		case 2:
			stageTexResID[0] = R.drawable.bg_stg2_1;
			stageTexResID[1] = R.drawable.bg_stg2_2;
			stageTexResID[2] = R.drawable.bg_stg2_3;
			stageTexResID[3] = R.drawable.bg_stg2_4;
			break;
			
		default:
			stageTexResID[0] = R.drawable.bg_stg1_1;
			break;
		}
		
		for(int i=0; i<stageTexNumber[place.stage-1]; i++){
		
			scrollBitmap = BitmapFactory.decodeResource(resources, stageTexResID[i]);
			scrollBitmap = Bitmap.createScaledBitmap(scrollBitmap, 512, 512, false);
			scrollTexID[i] = InitGL.setTexture(scrollBitmap);
			TextureManager.addTexture(stageTexResID[i], scrollTexID[i]);
			scrollBitmap.recycle();
		}
		
		place.isPreparedScroll = true;
	}
	
	static private void freeTexture(){
		
		for(int i=0; i<maxStageTexNumber; i++){
			
			if(stageTexResID[i]!=0){
			
				TextureManager.deleteTexture(stageTexResID[i]);
				stageTexResID[i]=0;
			}
		}
	}
	
	static public void onDraw(StagePlace place){
	
		int scrollPoint = place.scrollPoint;
		int texNumber = stageTexNumber[place.stage-1];
		
		int scrollMovement = scrollPoint % (int)scrollTexDrawSize.y;
		int texQuotient    = scrollPoint / (int)scrollTexDrawSize.y;
		int infTexIndex = texQuotient % texNumber;
		int supTexIndex = (infTexIndex + 1) % texNumber; 
		float drawY = scrollOffset + scrollMovement;
		
		tempRect.set(0, 1, 1, 0);
		InitGL.setTextureSTCoords(tempRect);
		
		scrollCenter.set(screenCenterX, drawY);
		InitGL.drawTexture(scrollCenter , scrollTexDrawSize, scrollTexID[infTexIndex]);
		scrollCenter.set(screenCenterX, drawY - scrollTexDrawSize.y);
		InitGL.drawTexture(scrollCenter , scrollTexDrawSize, scrollTexID[supTexIndex]);
	}

}
