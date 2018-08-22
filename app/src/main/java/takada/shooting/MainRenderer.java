package takada.shooting;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import takada.shooting.Global.StagePlace;
import takada.shooting.library.InitGL;
import takada.shooting.library.ScreenEffect;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;

public class MainRenderer implements GLSurfaceView.Renderer{
	
	ObjectsContainer objects;
	PointF screenSize = new PointF();

	public MainRenderer(ObjectsContainer objects){
		
		this.objects = objects;	
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		if(objects.stageManager.currentPlace.isStarted)	
			renderScreen(gl);
		
		else
			resetForStageStarting();
	}
	
	public void resetForStageStarting(){
		
		StagePlace place = objects.stageManager.currentPlace;
		
		objects.stageManager.prepareStarting();
		objects.enemyGenerator.resetAllEnemies();
		objects.shotGenerator.resetAllShots();
		objects.plane.setStartingState();
		objects.itemGenerator.resetAllItems();
		objects.derivativeEnemyManager.clearDETable();

		place.isStarted = true;
	}
	
	private void renderScreen(GL10 gl){
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float sx = (objects.plane.x - screenSize.x / 2)/4;
		Global.screenProjectionLeft = sx;
		gl.glOrthof(
				(int)sx, (int)(sx + screenSize.x), 
				(int)screenSize.y, 0, 
				0.5f, -0.5f
				);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		InitGL.enableDefaultBlend();
		InitGL.setTextureSTCoords(null);
		InitGL.changeTexColor(null);
		
		ScreenEffect.preDraw(gl);
		
		objects.stageManager.onDraw(gl);
		if(objects.stageManager.currentPlace.isShadowOn){
			
			objects.enemyGenerator.onDrawShadow(gl);
			objects.plane.onDrawShadow(gl);
		}
		objects.enemyGenerator.onDrawGrounders(gl);	
		objects.enemyGenerator.onDrawAirs(gl);	
		objects.plane.onDraw(gl);
		objects.shotGenerator.onDraw(gl);
		objects.itemGenerator.onDraw(gl);
		objects.indicator.onDraw(gl);
		//objects.pad.onDraw(gl);
		
		ScreenEffect.draw(gl);
		drawFPS();
		
	}
	
	private long countStartTime;
	private int frameCount;
	private float FPS = 0;
	static private RectF drawFPSRect = new RectF(0,450, 100, 400);
	
	private void drawFPS(){
		
		frameCount++;
		
		long nowTime = System.currentTimeMillis();
		long diffTime = nowTime - countStartTime;
		
		InitGL.drawText(drawFPSRect, "dFPS:"+FPS);
		
		if(diffTime>500){
			
			FPS = (int)((float)frameCount / diffTime * 10000) / 10f;
			
			countStartTime = nowTime;
			frameCount = 0;
		}	
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		double aspectRatio = 2d/3;
		
		InitGL.init(gl);
		InitGL.setViewPortWithAspectRatio(width, height, aspectRatio);
		Global.setScreenVal(new Point(width, height));
		
		screenSize.x = Global.virtualScreenSize.x;
		screenSize.y = Global.virtualScreenSize.y;
		
		objects.initialize();		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		
	}
}
