package takada.shooting;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.library.Double2Vector;
import takada.shooting.AnimationManager.AnimeKind;
import takada.shooting.ItemGenerator.ItemData;
import takada.shooting.ItemGenerator.ItemKind;
import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.library.InitGL;
import android.graphics.PointF;
import android.graphics.Rect;

public class Item {
	
	public MyPlane plane;
	private MyShotGenerator shotGenerator;
	private ItemGenerator generator;
	private AnimationManager animation;
	private Rect screenLimit = new Rect();
	
	private ItemKind itemKind;
	private Double2Vector startVelocity = new Double2Vector();
	private Double2Vector attractAcceleration = new Double2Vector();
	private int attractRange;
	private double maxVelocity;
	
	private boolean isAttracted = false;
	public Double2Vector velocity = new Double2Vector();
	public Double2Vector acceleration = new Double2Vector();
	private Double2Vector eVector = new Double2Vector();
	
	public int x, y;
	public int radius;
	float fx, fy;
	boolean isInScreen;
	
	int animeFrame, totalAnimeFrame;
	AnimationManager.AnimationSet animeSet;
	AnimationManager.AnimeKind animeKind = AnimationManager.AnimeKind.NORMAL;
	
	PointF drawCenter = new PointF();
	int drawAngle, oldDrawAngle;
	
	public Item(ObjectsContainer objectsContainer){
		
		generator = objectsContainer.itemGenerator;
		animation = objectsContainer.animationManager;
		plane = objectsContainer.plane;
		shotGenerator = objectsContainer.shotGenerator;
		
		initialize();
	}

	private void initialize(){
		
		setScreenLimit();
		isInScreen = true;
	}
	
	private void setScreenLimit(){
		
		screenLimit.left  = (int)Global.virtualScreenLimit.left;
		screenLimit.right = (int)Global.virtualScreenLimit.right;
		screenLimit.top   = (int)Global.virtualScreenLimit.top;
		screenLimit.bottom= (int)Global.virtualScreenLimit.bottom;
	}
	
	public void setParameter(ItemData itemData){
		
		itemKind = itemData.kind;
		
		fx = x = itemData.startPos.x;
		fy = y = itemData.startPos.y;
		
		startVelocity.set(itemData.startVelocity.x, itemData.startVelocity.y);
		attractAcceleration.set(
				itemData.attractAcceleration.x,
				itemData.attractAcceleration.y);
		
		attractRange = itemData.attractRange;
		maxVelocity = itemData.maxVelocity;
		radius = itemData.radius;
		
		animeSet = itemData.animeSet;
	}
	
	public void gotByPlane(){
		
		isInScreen = false;
		SoundEffect.play(SoundKind.GETSEED);
		
		switch(itemKind){
		
		case SHIELDENERGY:
	
			int overPoints = plane.getShieldEnergy(10);
			if(overPoints>0) shotGenerator.getWeaponEnergy(overPoints);
			break;
		
		case WEAPONENERGY:
			
			shotGenerator.getWeaponEnergy(30);
			break;
		}
	}
	
	public int getAngleOfTendToPlane(){
		
		return -90 +
			(int)(Math.atan2(plane.y - y, plane.x - x) / Global.radian);
	}
	
	public void onDraw(GL10 gl){
		
		drawCenter.set(x, y);
		
		//InitGL.setColor(0.0f, 1.0f, 0.0f, 1.0f);
		//InitGL.drawStrokeCircle(drawCenter, 16, 8);
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glPushMatrix();
		{
			gl.glLoadIdentity();
		
			gl.glTranslatef(drawCenter.x, drawCenter.y, 0);
			gl.glRotatef(drawAngle, 0, 0, 1);
			
			drawCenter.set(0, 0);
			//animation.setFrame(animeSet, AnimeKind.NORMAL, animeFrame);
			animation.setFrame(animeSet.getData(animeKind, 0), animeFrame);
			animation.drawFrame(drawCenter);
		}
		gl.glPopMatrix();
		
	}
	
	public void periodicalProcess(){
			
		checkAttracted();
		flyAhead();
		checkScreenLimit();
		animate();
	}
	
	private void checkAttracted(){
		
		eVector.set(plane.x - x, plane.y - y);
		
		if(eVector.length() < attractRange){
			
			isAttracted = true;
			attractToPlane();
		}
		else{
			
			isAttracted = false;
			velocity.set(startVelocity.x, startVelocity.y);
			acceleration.set(0, 0);
		}
	}
	
	private void attractToPlane(){
		
		eVector.set(plane.x - x, plane.y - y);
		eVector.limit(maxVelocity);
		
		double xa = eVector.x > velocity.x ? attractAcceleration.x : -attractAcceleration.x;
		double ya = eVector.y > velocity.y ? attractAcceleration.y : -attractAcceleration.y;
	
		acceleration.set(xa, ya);
	}
	
	private void flyAhead(){
		
		velocity.plus(acceleration);
		
		fx += velocity.x;
		fy += velocity.y;
		
		x = (int)fx;
		y = (int)fy;
	}
	
	private void checkScreenLimit(){
		
		if(
				x<screenLimit.left || y<screenLimit.top || 
				x>screenLimit.right || y>screenLimit.bottom
				) isInScreen = false;
	}
	
	private void animate(){
		
		if(isAttracted)
			drawAngle = getAngleOfTendToPlane();
		
		int eva = animation.checkAnimeLimit
			(animeSet.getData(AnimeKind.NORMAL,0), ++totalAnimeFrame);
		
		if (eva == -1) isInScreen = false;
		else
			animeFrame = eva;	
	}
}
