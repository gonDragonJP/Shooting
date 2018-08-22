package takada.shooting;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.library.InitGL;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;

public class Indicator {
	
	Context context;
	MyPlane plane;
	MyShotGenerator shotGenerator;
	int meterTexID;
	
	PointF drawCenter = new PointF();
	PointF drawSize = new PointF();
	RectF texSTRect = new RectF();
	RectF drawRect = new RectF();
	
	static int meterCenterX, meterCenterY;
	
	float barLeft, barRight, barTop, barBottom, addColor;
	static final int barRelativeLeft = -40;
	static final int barMaxWidth = 95;
	static final int barHeight = 5;
	static final int shieldBarRelativeTop = -14;
	static final int weaponBarRelativeTop = 10;
	
	public Indicator(Context context){
		
		this.context = context;
	}
	
	public void initialize(ObjectsContainer container){
		
		plane = container.plane;
		shotGenerator = container.shotGenerator;
		
		meterCenterX = (int)Global.screenCenter.x;
		meterCenterY = (int)Global.screenSize.y - 32;
		
		meterTexID = InitGL.loadTexture(context.getResources(), R.drawable.meter);
	}
	
	public void onDraw(GL10 gl){
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrthof(
				0, Global.screenSize.x, 
				Global.screenSize.y, 0, 
				0.5f, -0.5f
				);
		
		drawBars();
		drawMeter();
		
		gl.glPopMatrix();
	}
	
	private void drawBars(){
		
		barLeft   = meterCenterX + barRelativeLeft;
		barTop    = meterCenterY + shieldBarRelativeTop;
		barRight  = barLeft + plane.hitPoints / plane.maxHP * barMaxWidth;
		barBottom = barTop + barHeight;
		
		InitGL.setColor(0.8f, 0.1f, 0, 1);
		drawRect.set(barLeft, barTop, barRight, barBottom);
		InitGL.drawRectAngle(drawRect);
		
		barTop    = meterCenterY + weaponBarRelativeTop;
		barRight  = barLeft + 
			shotGenerator.weaponEnergy / shotGenerator.maxWeaponEnergy * barMaxWidth;
		barBottom = barTop + barHeight;
		
		addColor = shotGenerator.weaponLevel * 0.3f;
		
		InitGL.setColor(0, 0.5f + addColor, addColor, 1);
		drawRect.set(barLeft, barTop, barRight, barBottom);
		InitGL.drawRectAngle(drawRect);
	}
	
	private void drawMeter(){
		
		drawCenter.set(meterCenterX, meterCenterY);
		drawSize.set(128, 64);
		texSTRect.set(0, 1, 1, 0);
		
		InitGL.setTextureSTCoords(texSTRect);
		InitGL.drawTexture(drawCenter, drawSize, meterTexID);
	}
}
