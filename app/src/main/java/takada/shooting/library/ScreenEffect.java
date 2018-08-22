package takada.shooting.library;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.Global;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class ScreenEffect {
	
	enum CallbackKind{ CUTIN, CHANGINGCOLOR, TYPEOUT, WIPESCREEN};
	public enum WipeKind{ HOLEWIPE, REEDSCREENWIPE};
	static final int frameIntervalTime = Global.frameIntervalTime;
	
	static WorkThread workThread = new ScreenEffect().new WorkThread();
	private static RectF screenRect = new RectF();
	
	
	public class WorkThread extends Thread {
		
		boolean isActive;
		List<Callback> callbackList0 = new ArrayList<Callback>();
		List<Callback> callbackList1 = new ArrayList<Callback>();
		List<Callback> callbackList2 = new ArrayList<Callback>();
		
		public void addCallbackPreDraw(Callback callback){
			
			callbackList0.add(callback);
			
			if(isActive == false){
				isActive = true;
				this.start();	
			}
		}
		
		public void addCallback(Callback callback){
		
			callbackList1.add(callback);
			
			if(isActive == false){
				isActive = true;
				this.start();	
			}
		}
		
		public void addCallbackOverScreen(Callback callback){
			
			callbackList2.add(callback);
			
			if(isActive == false){
				isActive = true;
				this.start();	
			}
		}
		
		public void resumeThread(){
			
			isActive = true;
		}
		
		public void pauseThread(){
		
			isActive = false;
		}
		
		@Override
		public void run(){
			
			long lastUpdateTime = System.currentTimeMillis();
			
			while(true){//(callbackList0.size()!=0 || callbackList1.size()!=0 || callbackList2.size()!=0){
				
				if(!isActive){
					
					lastUpdateTime = System.currentTimeMillis();
					continue;
				}
			
				long nowTime = System.currentTimeMillis();
				long difference = nowTime - lastUpdateTime;
			
				while(difference >= frameIntervalTime){
					difference -=frameIntervalTime;
					update();
				}
			
				lastUpdateTime = nowTime - difference;
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				
					e.printStackTrace();
				}
			
			}
		}
		
		private void update(){
			
			for(int i=callbackList0.size()-1; i>=0; i--){
				
				Callback callback = callbackList0.get(i);
				
				if(callback.isActive)
				
					callback.update();
				
				else
					callbackList0.remove(i);
			}
			
			for(int i=callbackList1.size()-1; i>=0; i--){
				
				Callback callback = callbackList1.get(i);
				
				if(callback.isActive)
				
					callback.update();
				
				else
					callbackList1.remove(i);
			}
			
			for(int i=callbackList2.size()-1; i>=0; i--){
				
				Callback callback = callbackList2.get(i);
				
				if(callback.isActive)
				
					callback.update();
				
				else
					callbackList2.remove(i);
			}
		}
	}; 
	
	public class Callback{
		
		CallbackKind kind;
		boolean isActive = true;
		boolean isStarted = false;
		boolean isFinished = false;

		int preWaitingFrame, processFrame, durationFrame;
		int preWaitingFrameCount, processFrameCount, durationFrameCount;
		
		public Callback
			(CallbackKind kind,int preWaitingSec, int processSec, int durationSec){
			
			this.kind = kind;
			preWaitingFrame = preWaitingSec / frameIntervalTime;
			processFrame = processSec / frameIntervalTime;
			durationFrame = durationSec / frameIntervalTime;
		}
		
		public boolean draw(GL10 gl){
			
			if(!isStarted || !isActive) return false;
			if(isFinished){
				
				finishProcess(gl);
				isActive = false;
				return false;
			}
			
			return true;
		};
		
		public void update(){
			
			if(preWaitingFrameCount < preWaitingFrame){
				
				preWaitingFrameCount++;
				return;
			}
			
			isStarted = true;
			
			if(processFrameCount < processFrame){
				
				updateProcess();
				processFrameCount++;
			}
			else{
				
				if(durationFrameCount++ > durationFrame){
				
					isFinished = true;
				}
			}
		};
		
		protected void updateProcess(){};
		protected void finishProcess(GL10 gl){};
	}

	public static void setScreenRect(RectF rect){
		
		screenRect.set(rect);
	}
	
	public static void preDraw(GL10 gl){
		
		for(int i=0; i<workThread.callbackList0.size(); i++){
		
			Callback callback = workThread.callbackList0.get(i);
			
			callback.draw(gl);
		}
	}
	
	public static void draw(GL10 gl){
		
		for(int i=0; i<workThread.callbackList1.size(); i++){
		
			Callback callback = workThread.callbackList1.get(i);
			
			callback.draw(gl);
		}
		
		for(int i=0; i<workThread.callbackList2.size(); i++){
			
			Callback callback = workThread.callbackList2.get(i);
			
			callback.draw(gl);
		}
	}
	
	public static void cutinText(
			RectF startRect, RectF endRect, String string,
			int preWaitingSec, int processSec, int durationSec, 
			float startAngle, float endAngle,
			ChangingColor changingColor){
		
		Cutin cutin = new ScreenEffect().new Cutin
		(CallbackKind.CUTIN, preWaitingSec, processSec, durationSec);
		
		cutin.setParam(
				startRect, endRect, string, 
				startAngle, endAngle, changingColor
				);
	
		workThread.addCallback(cutin);
	}
	
	private class Cutin extends Callback{
			
		RectF startRect, endRect;
		String string;
		
		float startAngle, endAngle;
		
		RectF drawRect = new RectF();
		RectF convRect = new RectF();
		float drawAngle;
		
		ChangingColor changingColor;
		
		public Cutin
		(CallbackKind kind, int preWaitingSec, int processSec, int durationSec) {
			
			super(kind, preWaitingSec, processSec, durationSec);
		}
		
		public void setParam(
				RectF startRect, RectF endRect, String string,
				float startAngle, float endAngle,
				ChangingColor changingColor){
			
			this.startRect = startRect;
			this.endRect = endRect;
			this.string = string;
			this.startAngle = startAngle;
			this.endAngle = endAngle;
			this.changingColor = changingColor;
		}

		@Override
		public boolean draw(GL10 gl) {
			
			if(!super.draw(gl)) return false;
			
			if(changingColor!=null)
				InitGL.changeTexColor(changingColor.matrix);
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			gl.glPushMatrix();
			{
				gl.glLoadIdentity();
			
				gl.glTranslatef(drawRect.centerX(), drawRect.centerY(), 0);
				gl.glRotatef(drawAngle, 0, 0, 1);
				
				InitGL.drawText(convRect, string);
			}
			gl.glPopMatrix();
			
			InitGL.changeTexColor(null);
			
			return true;
		}

		@Override
		public void update() {
			
			super.update();
			
		}
		
		@Override
		protected void updateProcess(){
			
			float rate = (float)(processFrameCount + 1) / processFrame;
			
			float left = startRect.left + (endRect.left - startRect.left) * rate;
			float right = startRect.right + (endRect.right - startRect.right) * rate;
			float top = startRect.top + (endRect.top - startRect.top) * rate;
			float bottom = startRect.bottom + (endRect.bottom - startRect.bottom) * rate;
		
			drawRect.set(left, top, right, bottom);
			
			float a = drawRect.width() / 2;
			float b = drawRect.height() / 2;
			
			convRect.set(-a, -b, a, b);
			
			drawAngle = startAngle + (endAngle - startAngle) * rate;
		}
	}
	
	static public ChangingColor getChangingColor(
			int startColor, int endColor, int intervalSec, boolean isPendulum,
			int preWaitingSec, int processSec, int durationSec){
		
		ChangingColor cColor = 
			new ScreenEffect().new ChangingColor
				(CallbackKind.CHANGINGCOLOR, preWaitingSec, processSec, durationSec);
		
		cColor.setParam(startColor, endColor, intervalSec, isPendulum);
	
		workThread.addCallback(cColor);
		
		return cColor;
	}
	
	public class ChangingColor extends Callback{
		
		float[] startColor = new float[4];
		float[] endColor = new float[4];
		int intervalFrame;
		boolean isPendulum = false;
		
		public float[] matrix = new float[4];
		
		public ChangingColor
			(CallbackKind kind,int preWaitingSec, int processSec, int durationSec){
			
			super(kind, preWaitingSec, processSec, durationSec);
		}
		
		public void setParam
			(int startColor, int endColor, int intervalSec, boolean isPendulum){
			
			this.startColor[0] = Color.red(startColor) / 255f;
			this.startColor[1] = Color.green(startColor) / 255f;
			this.startColor[2] = Color.blue(startColor) / 255f;
			this.startColor[3] = Color.alpha(startColor) / 255f;
			
			this.endColor[0] = Color.red(endColor) / 255f;
			this.endColor[1] = Color.green(endColor) / 255f;
			this.endColor[2] = Color.blue(endColor) / 255f;
			this.endColor[3] = Color.alpha(endColor) / 255f;
			
			intervalFrame = intervalSec / frameIntervalTime;
			this.isPendulum = isPendulum;
		}
		
		@Override
		public boolean draw(GL10 gl) {
			
			if(!super.draw(gl)) return false;
		
			return true;
		}

		@Override
		public void update() {
			super.update();	
		}
		
		@Override
		protected void updateProcess(){
			
			if(matrix == null) return;
			
			float rate = (float)(processFrameCount % intervalFrame) / intervalFrame;
			
			if(isPendulum) rate = 2 * (0.5f - Math.abs(rate - 0.5f));
			
			for(int i=0; i<4; i++)
				matrix[i] = startColor[i] + (endColor[i]-startColor[i]) * rate;	
		}
	}
	
	public static void typeOutText(
			RectF drawRect, String string, int typeIntervalSec,
			int preWaitingSec, int processSec, int durationSec, 
			ChangingColor changingColor){
		
		TypeOut typeOut = new ScreenEffect().new TypeOut
		(CallbackKind.TYPEOUT, preWaitingSec, processSec, durationSec);
		
		typeOut.setParam(drawRect, string, typeIntervalSec, changingColor);
	
		workThread.addCallback(typeOut);
	}
	
	private class TypeOut extends Callback{
		
		String string;
		int typeIntervalFrame;
		int typeIntervalFrameCount;
		int typePosition;
		
		final int promptIntervalFrame = 5;
		int promptIntervalFrameCount;
		boolean isPromptBlinkOn;
		float chrWidth;
		
		RectF drawRect = new RectF();
		RectF typeRect = new RectF();
		
		ChangingColor changingColor;

		public TypeOut(CallbackKind kind, int preWaitingSec, int processSec,
				int durationSec) {
			super(kind, preWaitingSec, processSec, durationSec);
			// TODO Auto-generated constructor stub
		}
		
		public void setParam(
				RectF drawRect, String string, int typeIntervalSec, 
				ChangingColor changingColor){
		
			this.drawRect.set(drawRect);
			this.string = string;
			this.typeIntervalFrame = typeIntervalSec / frameIntervalTime;
			this.changingColor = changingColor;
		
			chrWidth = drawRect.width() / string.length();
			typeRect.top = drawRect.top;
			typeRect.bottom = drawRect.bottom;	
		}
		
		@Override
		public boolean draw(GL10 gl) {
			
			if(!super.draw(gl)) return false;
			
			if(changingColor!=null)
				InitGL.changeTexColor(changingColor.matrix);
			
			for(int i=0; i<typePosition; i++){
			
				String typeStr = ""+string.charAt(i);
				
				typeRect.left = drawRect.left + chrWidth * i;
				typeRect.right = typeRect.left + chrWidth;
				
				InitGL.drawText(typeRect, typeStr);
			}
			
			if(isPromptBlinkOn){
				
				typeRect.left = typeRect.right;
				typeRect.right = typeRect.left + chrWidth;
				
				InitGL.drawText(typeRect, "_");
			}
			
			InitGL.changeTexColor(null);
			return true;
		}

		@Override
		public void update() {
			
			super.update();
		}
		
		@Override
		protected void updateProcess(){
			
			if(promptIntervalFrameCount++ > promptIntervalFrame){
				
				promptIntervalFrameCount = 0;
				isPromptBlinkOn = (isPromptBlinkOn)? false : true;
			}
			
			typePosition = typeIntervalFrameCount++ / typeIntervalFrame;
			
			if(typePosition > string.length()){
				
				typePosition = string.length();
				isPromptBlinkOn = false;
			}
		}
	}
	
	public static void wipeScreen(
			RectF wipeRect, WipeKind wipeKind, int wipeAngle,  boolean isWipeIn,
			int preWaitingSec, int processSec, int durationSec){
		
		WipeScreen wScreen = new ScreenEffect().new WipeScreen
		(CallbackKind.WIPESCREEN, preWaitingSec, processSec, durationSec);
		
		wScreen.setParam(wipeRect, wipeKind, wipeAngle, isWipeIn);
	
		workThread.addCallbackPreDraw(wScreen);
		//workThread.addCallback(wScreen);
	}
	
	private class WipeScreen extends Callback{
		
		RectF wipeRect = new RectF();
		PointF wipeCenter = new PointF();
		WipeKind wipeKind;
		int wipeAngle;
		float wipeHoleRadius, endHoleRadius;
		float reedSize, wipeRectHeight;
		boolean isWipeIn = true;

		public WipeScreen(CallbackKind kind,
				int preWaitingSec, int processSec,int durationSec) {
			super(kind, preWaitingSec, processSec, durationSec);
			
		}
		
		public void setParam
			(RectF wipeRect, WipeKind wipeKind, int wipeAngle, boolean isWipeIn){
		
			this.wipeRect.set(wipeRect);
			this.wipeCenter.set(wipeRect.centerX(), wipeRect.centerY());
			this.wipeKind = wipeKind;
			this.wipeAngle = wipeAngle;
			this.isWipeIn = isWipeIn;
			
			float w = wipeRect.width(), h = wipeRect.height();
			this.endHoleRadius = (float)(Math.sqrt(w * w + h * h) / 2);
			this.reedSize = h / processFrame;
			
		}
		
		@Override
		public boolean draw(GL10 gl) {
			
			if(!super.draw(gl)) return false;
			
			gl.glEnable(GL10.GL_STENCIL_TEST);
			gl.glClear(GL10.GL_STENCIL_BUFFER_BIT);
			gl.glStencilFunc(GL10.GL_ALWAYS, 0x01, 0x01);
			gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
			gl.glColorMask(false, false, false, false);
			
			switch(wipeKind){
			
			case HOLEWIPE:
				
				cutHole(gl);
				break;
				
			case REEDSCREENWIPE:
				
				cutReedScreen(gl);
				break;
			}
			
			gl.glStencilFunc(GL10.GL_EQUAL, 0x01, 0x01);
			gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
			gl.glColorMask(true, true, true, true);
			
			return true;
		}
		
		private void cutHole(GL10 gl){
			
			InitGL.drawFillCircle(wipeCenter, wipeHoleRadius, 32);
		}
		
		private void cutReedScreen(GL10 gl){
			
			RectF rect = new RectF();
			
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			gl.glPushMatrix();
			{
				gl.glLoadIdentity();
			
				gl.glTranslatef(wipeCenter.x, wipeCenter.y, 0);
				gl.glRotatef(wipeAngle, 0, 0, 1);
				gl.glTranslatef(-wipeCenter.x, -wipeCenter.y, 0);
			
			
			rect.set(wipeRect);
			rect.bottom = rect.top + reedSize;
			
			do{	
				InitGL.setColor(Color.argb(255,255, 255, 255));
				
				InitGL.drawRectAngle(rect);
				
				rect.bottom += reedSize * 2;
				rect.top += reedSize * 2;
			
			}while(rect.top < (wipeRect.top + wipeRectHeight));
			
			rect.set(wipeRect);
			rect.top = rect.bottom - reedSize;
			
			do{	
				InitGL.setColor(Color.argb(255,255, 255, 255));
				
				InitGL.drawRectAngle(rect);
				
				rect.bottom -= reedSize * 2;
				rect.top -= reedSize * 2;
			
			}while(rect.bottom > (wipeRect.bottom - wipeRectHeight));
			
			}
			gl.glPopMatrix();
		}
		
		@Override
		public void update() {
			
			super.update();
		}
		
		@Override
		protected void updateProcess(){
			
			float rate = (isWipeIn)?
			 (float)(processFrameCount + 1) / processFrame
			:(float)(processFrame - processFrameCount) / processFrame;
			
			wipeHoleRadius = endHoleRadius * rate;
			
			wipeRectHeight = wipeRect.height() * rate;
		}
		
		@Override
		protected void finishProcess(GL10 gl){
		
			//gl.glDisable(GL10.GL_STENCIL_TEST);
		}
	}
}
