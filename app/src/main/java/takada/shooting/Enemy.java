package takada.shooting;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import takada.shooting.library.Double2Vector;
import takada.shooting.AnimationManager.AnimeKind;
import takada.shooting.Global.EnemyCategory;
import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.library.InitGL;
import takada.shooting.structure.EnemyData;
import takada.shooting.structure.EnemyData.CollisionRegion;
import takada.shooting.structure.EnemyData.GeneratingChild;
import takada.shooting.structure.EnemyData.MovingNode;

public class Enemy {

	public MyPlane plane;
	private EnemyGenerator generator;
	private AnimationManager animation;
	private ItemGenerator itemGenerator;
	private Rect screenLimit = new Rect();
	
	public Double2Vector velocity = new Double2Vector();
	public Double2Vector acceleration = new Double2Vector();
	private boolean isHoming = false;
	protected Double2Vector eVelocity = new Double2Vector();
	private Double2Vector homingAcceleration = new Double2Vector();
	
	private ArrayList<MovingNode> node = new ArrayList<MovingNode>();
	private ArrayList<GeneratingChild> generating = new ArrayList<GeneratingChild>();
	private ArrayList<CollisionRegion> collision;
	public ArrayList<CollisionRegion> collisionRotated = new ArrayList<CollisionRegion>();
	
	public int objectID;
	public int x, y;
	protected float fx, fy;
	public boolean isInScreen, isInExplosion, hasShadow, isGrounder;
	
	protected int nodeFrameCount;
	protected int nodeDurationFrame;
	protected int nodeNumber;
	protected int nodeIndex;
	
	protected int genFrameCount;
	protected int genNumber, genIndex;
	protected int genStartFrame, genIntervalFrame, genRepeat;
	int genObjectID;
	protected Enemy tempGenChild;
	
	protected int colNumber;
	boolean neededCheckingWithBullet;
	int hitPoints, atackPoints;
	
	int animeFrame, totalAnimeFrame;
	public AnimationManager.AnimationSet animeSet;
	AnimationManager.AnimeKind animeKind = AnimationManager.AnimeKind.NORMAL;
	public AnimationManager.AnimationData drawAnimeData;
	
	PointF drawCenter = new PointF();
	public double drawAngle, oldDrawAngle;
	
	public Enemy parentEnemy;
	Point tempChildPosition = new Point();
	
	
	public Enemy(ObjectsContainer container){
		
		generator = container.enemyGenerator;
		animation = container.animationManager;
		itemGenerator = container.itemGenerator;
		plane = container.plane;
		
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
		screenLimit.top   = (int)Global.virtualScreenLimit.top;
		screenLimit.bottom= (int)Global.virtualScreenLimit.bottom;
	}
	
	public void setEnemyData(EnemyData enemyData, Point requestPos, Enemy parentEnemy){
		
		this.parentEnemy = parentEnemy;
		
		enemyData.cloneNodeList(node);
		enemyData.cloneGenList(generating);
		collision = enemyData.collision;	
		nodeNumber = node.size();
		genNumber = generating.size();
		colNumber = collision.size();
		
		for(int i=0; i<colNumber; i++){
			
			CollisionRegion col = enemyData.new CollisionRegion();
			col.copy(collision.get(i));
			collisionRotated.add(col);
		}
		
		objectID = enemyData.objectID;
		
		hitPoints = enemyData.hitPoints;
		neededCheckingWithBullet = (hitPoints != 0);
		atackPoints = enemyData.atackPoints;
		
		animeSet = animation.getAnimationSet
						(AnimationManager.AnimeObject.ENEMY, objectID);
		drawAnimeData = animeSet.getData(AnimeKind.NORMAL,0);
		
		Point startPos;
		
		if(parentEnemy==null)
				startPos = enemyData.getStartPositionWithAtrib (plane);
		
		else
				startPos = parentEnemy.getChildStartPositionFromRequest(requestPos);
		
		fx = this.x = startPos.x;		fy = this.y = startPos.y;
		
		setMovingNode(0);
		
		drawAngle = animation.getEnemyRotateAngle(this, true); 
		//tendAheadの為にnodesetの後でないとマズイ(子がすぐに参照する可能性あり）
		
		if(genNumber>0)	setGenerating(0);
		
		EnemyCategory category = EnemyData.enemyCategoryFromID[objectID/1000];
		hasShadow = (category==EnemyCategory.FLYING);
		isGrounder = (category==EnemyCategory.GROUND);	
	}
	
	public Point getChildStartPositionFromRequest(Point requestPos){
		
		double c = Math.cos(drawAngle * Global.radian);
		double s = Math.sin(drawAngle * Global.radian);
		
		int childX = x + (int)(requestPos.x * c - requestPos.y * s);
		int childY = y + (int)(requestPos.x * s + requestPos.y * c);
		
		tempChildPosition.set(childX, childY);
		
		return tempChildPosition;
	}

	protected void setMovingNode(int index){
		
		MovingNode nd = node.get(index);

		velocity.copy(nd.getStartVelocityWithAtrib(this));
		acceleration.copy(nd.getStartAccelerationWithAtrib(this));
		homingAcceleration.copy(nd.homingAcceleration);

		if(homingAcceleration.length()!=0)	isHoming = true;
		nodeDurationFrame=nd.nodeDurationFrame;
		nodeFrameCount = 0;
	}
	
	public void addNodeDuration(int index, int addDuration){
		
		MovingNode nd = node.get(index);
		nd.nodeDurationFrame += addDuration;
		
		if(index ==0) {	// 初期nodeは再設定しないと反映されない
			setMovingNode(0);
			drawAngle = animation.getEnemyRotateAngle(this, true);
		}
	}
	
	protected void setNodeActionAnime(int index){
		
		if (animeSet.nodeActionAnime.get(index) == null){
			animeKind = AnimationManager.AnimeKind.NORMAL;
			totalAnimeFrame = 0;
			return;
		}
		
		animeKind = AnimationManager.AnimeKind.NODEACTION;
		totalAnimeFrame = 0;
	}

	protected void setGenerating(int index){

		GeneratingChild gen = generating.get(index);

		genObjectID = gen.objectID;
		genStartFrame = gen.startFrame;
		genRepeat = gen.repeat;
		genIntervalFrame = gen.intervalFrame;
	}
	
	public void slideStartFrame(int index, int slideFrame){
	
		GeneratingChild gen = generating.get(index);
		gen.startFrame += slideFrame;
		
		if(index ==0) {	// 初期generatingは再設定しないと反映されない
			setGenerating(0);
		}
	}
	
	public void cueingGenerating(int index, int skipFrame){
		
		genIndex = index;
		setGenerating(genIndex);
		genFrameCount = skipFrame;
	}
	
	public void setExplosion(){
	
		isInExplosion = true;
		animeKind = AnimationManager.AnimeKind.EXPLOSION;
		totalAnimeFrame = 0;
		
		hasShadow = false;
		SoundEffect.play(SoundKind.EXPLOSION1);
	}
	
	public void dropItem(boolean isShootByLaser){
		
		if(isShootByLaser)
		
			itemGenerator.addWeaponEnergy(x, y);
		else
			itemGenerator.addShieldEnergy(x, y);
	}
	
	public double getAngleOfTendToPlane(){
	
		return -90 +
			Math.atan2(plane.y - y, plane.x - x) / Global.radian;
	}
	
	public double getAngleOfTendAhead(){
		
		return -90 +
			Math.atan2(velocity.y, velocity.x) / Global.radian;
	}
	
	public Double2Vector getUnitVectorOfFaceOn(){
		
		double angle = (drawAngle + 90) * Global.radian;
		eVelocity.set(Math.cos(angle), Math.sin(angle));
		
		return eVelocity;
	}
	
	public PointF getDrawSizeOfNormalAnime(){
		
		return animeSet.normalAnime.drawSize;
	}
	
	final float[] shadowColor = {0, 0, 0, 0};
	
	public void onDrawShadow(GL10 gl){
		
		if(!hasShadow) return;
		
		InitGL.changeTexColor(shadowColor);
		
		drawCenter.set
			(x+Global.shadowDeflectionX, y+Global.shadowDeflectionY);
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glPushMatrix();
		{
			gl.glLoadIdentity();
		
			gl.glTranslatef(drawCenter.x, drawCenter.y, 0);
			gl.glRotatef((float)drawAngle, 0, 0, 1);
			
			drawCenter.set(0, 0);
			animation.setFrame(animeSet.getData(animeKind, nodeIndex), animeFrame);
			animation.drawScaledFrame
				(drawCenter, Global.shadowScaleX, Global.shadowScaleY);
		}
		gl.glPopMatrix();
		
		InitGL.changeTexColor(null);
	}
	
	public void onDrawIfGrounder(GL10 gl){
		
		if(isGrounder) onDraw(gl);
	}
	
	public void onDrawIfAir(GL10 gl){
		
		if(!isGrounder) onDraw(gl);
	}
	
	public void onDraw(GL10 gl){
		
		drawCenter.set(x, y);
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		gl.glPushMatrix();
		{
			gl.glLoadIdentity();
		
			gl.glTranslatef(drawCenter.x, drawCenter.y, 0);
			gl.glRotatef((float)drawAngle, 0, 0, 1);
			
			drawCenter.set(0, 0);
			animation.setFrame(animeSet.getData(animeKind, nodeIndex), animeFrame);
			animation.drawFrame(drawCenter);
		}
		gl.glPopMatrix();
		
		
		//InitGL.setColor(0.0f, 1.0f, 0.0f, 1.0f);
		//InitGL.drawFillCircle(drawCenter, 4, 8);
		//drawCollisionRegion(gl);
	}
	
	private void drawCollisionRegion(GL10 gl){
		
		InitGL.setColor(1.0f, 0.5f, 0.0f, 1.0f);
		
		for(int i=0; i<colNumber; i++){
			
			CollisionRegion col = collisionRotated.get(i);
			
			int colX = x + col.centerX;
			int colY = y + col.centerY;
			int colRadius = col.size;
			
			InitGL.drawStrokeCircle
				(new PointF(colX, colY), colRadius, 8);
		}
	}
	
	public void periodicalProcess(){
		
		if(!isInExplosion){
			
			if(genNumber > genIndex) checkGeneratingCount();
		
			if(!checkNodeDuration()){
				isInScreen=false; 
				return;
			}
			
			if(colNumber>0 && oldDrawAngle!=drawAngle) rotateCollisionRegion();
		
			flyAhead();
			checkScreenLimit();
		}
		animate();
	}
	
	protected boolean checkNodeDuration(){

		if(nodeDurationFrame < ++nodeFrameCount){
			
			if(nodeNumber==(++nodeIndex)) return false;
			setMovingNode(nodeIndex);
			setNodeActionAnime(nodeIndex);
		}
		return true;
	}

	protected void flyAhead(){
		
		if(isHoming) homing();
		
		fx += velocity.x;
		fy += velocity.y;
		
		velocity.plus(acceleration);
		
		if(isGrounder) fy += Global.scrollSpeedPerFrame;
		
		x = (int)fx;
		y = (int)fy;
	}
	
	private void homing(){
		
		double speed = velocity.length();
		
		eVelocity.set(plane.x - x, plane.y - y);
		eVelocity.limit(speed);
		
		eVelocity.x *= homingAcceleration.x;
		eVelocity.y *= homingAcceleration.y;
		
		velocity.plus(eVelocity);
		velocity.normalize(speed);
	}
	
	private void checkScreenLimit(){
		
		if(
				x<screenLimit.left || y<screenLimit.top || 
				x>screenLimit.right || y>screenLimit.bottom
				) isInScreen = false;
	}
	
	protected void checkGeneratingCount(){
		
		tempGenChild = null;

		if(genFrameCount++ < genStartFrame) return;

		tempGenChild = requestGenerating();
		//android.util.Log.e("", ""+x+":"+y+":"+nodeFrameCount);

		if (--genRepeat<1) {
			
			if (++genIndex < genNumber) setGenerating(genIndex);
		}
		else 
			genStartFrame +=genIntervalFrame;	
	}
	
	protected Enemy requestGenerating(){

		Point requestStartPos = new Point();
		GeneratingChild gen = generating.get(genIndex);
		int requestObjectID = gen.objectID;
		requestStartPos.x = gen.centerX;
		requestStartPos.y = gen.centerY;
		
		return generator.requestGenerating
			(requestObjectID, requestStartPos, this);
	}
	
	protected void animate(){
		
		drawAnimeData = animeSet.getData(animeKind, nodeIndex);
		drawAngle = animation.getEnemyRotateAngle(this, false);
		
		int eva = animation.checkAnimeLimit(drawAnimeData, ++totalAnimeFrame);
		
		if (eva == -1) isInScreen = false;
		else
			animeFrame = eva;	
	}
	
	protected void rotateCollisionRegion(){
	
		for(int i=0; i<colNumber; i++){
		
			CollisionRegion colSrc = collision.get(i);
			CollisionRegion colDst = collisionRotated.get(i);
			
			if(colSrc.centerX==0 && colSrc.centerY==0) continue;
			
			double c = Math.cos(drawAngle * Global.radian);
			double s = Math.sin(drawAngle * Global.radian);
			
			colDst.centerX = (int)(colSrc.centerX * c - colSrc.centerY * s);
			colDst.centerY = (int)(colSrc.centerX * s + colSrc.centerY * c);
		}
		oldDrawAngle = drawAngle;
	}
}
