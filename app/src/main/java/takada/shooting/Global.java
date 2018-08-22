package takada.shooting;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class Global {
	
	public static final int frameIntervalTime = 1000 /50;
	
	public static final float scrollSpeedPerFrame = 1.0f;
	
	public static final double radian = 3.14159/180;
	
	public static Point screenSize = new Point();
	public static PointF screenCenter = new PointF();

	static final float virtualScreenWideRate = 1.25f;
	static final PointF virtualScreenSize = new PointF(320, 480);
	static public RectF virtualScreenLimit = new RectF();
	
	static public float screenProjectionLeft;
	
	public static final int shadowDeflectionX = 16;
	public static final int shadowDeflectionY = -16;
	public static final float shadowScaleX = 0.75f;
	public static final float shadowScaleY = 0.75f;
	
	static public void setScreenVal(Point size){
	
		screenSize.x = size.x;
		screenSize.y = size.y;
		
		screenCenter.x = size.x / 2f;
		screenCenter.y = size.y / 2f;
		
		float screenSideSpace = virtualScreenSize.x * (virtualScreenWideRate-1) / 2;
		float screenEndSpace = virtualScreenSize.y * (virtualScreenWideRate-1) / 2;
		
		virtualScreenLimit.set(
				-screenSideSpace, -screenEndSpace,
				virtualScreenSize.x + screenSideSpace, virtualScreenSize.y + screenEndSpace);
	}
	
	public class ShotShape{
		int color, length, width;
		public void set(int color, int length, int width){
			this.color = color;	
			this.length = length;
			this.width = width;
		}
	}
	
	public class StagePlace{
		boolean isStarted;
		boolean isPreparedScroll, isPreparedStageData;
		boolean isShadowOn;
		int stage;
		int scrollPoint;
		int eventIndex;
		public void initialize(int stage){
			
			this.isStarted = false;
			this.isPreparedScroll = false;
			this.isPreparedStageData = false;
			this.isShadowOn = false;
			this.stage = stage; 
			this.scrollPoint = 0;
			this.eventIndex = 0;
		}
	}
	
	public enum EnemyCategory{
		
		FLYING, GROUND, MAKER, BULLET, PARTS
	}
	
	public enum StartPositionAtrib{
		DEFAULT, PLANESIDE, COUNTERPLANESIDE, SAMEPLANE
	}
	
	public enum StartVectorAtrib{
		DEFAULT, ADOPTSIGNTOCENTER, ADOPTSIGNTOCOUNTERCENTER, 
		TENDTOPLANE, TENDPARENTAHEAD, ADOPTSIGNTOPLANE, ADOPTSIGNTOCOUNTERPLANE,
		TENDPARENTFACEON, SETPARENTFACEONANDCW
	}
	
	public enum CollisionShape { CIRCLE, RECTANGLE }
	
	
	
}
