package takada.shooting;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import takada.shooting.library.Double2Vector;
import takada.shooting.AnimationManager.AnimeKind;
import takada.shooting.library.InitGL;

public class MyShot {
	
	private Rect screenLimit = new Rect();
	protected AnimationManager animationManager;
	protected MyPlane plane;
	private MyShotGenerator shotGenerator;
	
	public int x;
	public int y;
	public boolean isInScreen;
	public boolean isInExplosion;
	public boolean isLaser;
	public int shotPower, shotRadius;
	
	protected Double2Vector velocity = new Double2Vector();
	protected PointF drawCenter = new PointF();
	private RectF drawRect = new RectF();
	
	protected float angle;
	protected int totalAnimeFrame;
	protected int animeFrame;
	protected AnimationManager.AnimationSet animeSet;
	protected AnimeKind animeKind = AnimeKind.NORMAL;
	
	
	public MyShot(ObjectsContainer objectsContainer){
		
		animationManager = objectsContainer.animationManager;
		plane = objectsContainer.plane;
		shotGenerator = objectsContainer.shotGenerator;
		
		initialize();
	}
	
	private void initialize(){
		
		setScreenLimit();
		
		isInScreen = true;
		isInExplosion = false; 
	}
	
	private void setScreenLimit(){
		
		screenLimit.left  = (int)Global.virtualScreenLimit.left;
		screenLimit.right = (int)Global.virtualScreenLimit.right;
		screenLimit.top   = 0;// âÊñ äOè„ïîñ≥å¯Å@(int)Global.virtualScreenLimit.top;
		screenLimit.bottom= (int)Global.virtualScreenLimit.bottom;
	}
	
	public void setShape(boolean isLaser){
		
		this.isLaser = isLaser;
		
		if(isLaser){
			animeSet = animationManager.getAnimationSet
				(AnimationManager.AnimeObject.MYLASER, 0);
		}
		else{
			animeSet = animationManager.getAnimationSet
				(AnimationManager.AnimeObject.MYBULLET, 0);
		}
	}
	
	public void setVectors(Point startPos, Double2Vector velocity){
		
		this.x = startPos.x;
		this.y = startPos.y;
		
		this.velocity.copy(velocity);
		
		angle = 90+(float)(Math.atan2(velocity.y, velocity.x) / Global.radian);
	}
	
	public void setExplosion(){
		
		isInExplosion = true;
		animeKind = AnimeKind.EXPLOSION;
		totalAnimeFrame = 0;
	}
	
	public void onDraw(GL10 gl){
		
		if(!isInExplosion){
			
			drawCenter.set(x, y);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			gl.glPushMatrix();
			{
				gl.glLoadIdentity();
			
				gl.glTranslatef(drawCenter.x, drawCenter.y, 0);
				gl.glRotatef(angle, 0, 0, 1);
				
				drawCenter.set(0, 0);
			
				animationManager.setFrame(
						animeSet.getData(AnimeKind.NORMAL,0), 
						animeFrame
						);
				animationManager.drawFrame(drawCenter);
			}
			gl.glPopMatrix();	
		}
		
		else{
		
			drawCenter.set(x, y);
		
			animationManager.setFrame(
					animeSet.getData(AnimeKind.EXPLOSION,0),
					animeFrame
					);
			animationManager.drawFrame(drawCenter);
		}
	}
	
	public void periodicalProcess(){
		
		flyAhead();
		checkPositionLimit();
		animate();
	}
	
	protected void flyAhead(){
		
		x += velocity.x;
		y += velocity.y;
	}
	
	private void checkPositionLimit(){
		
		if(
				x<screenLimit.left || y<screenLimit.top || 
				x>screenLimit.right || y>screenLimit.bottom
				) isInScreen = false;
	}
	
	protected void animate(){
		
		int eva = animationManager.checkAnimeLimit
			(animeSet.getData(animeKind, 0), ++totalAnimeFrame);
		
		if (eva == -1) isInScreen = false;
		else
			animeFrame = eva;
	}
}
