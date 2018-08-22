package takada.shooting;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import takada.shooting.AnimationManager.AnimeKind;

public class MyLaser extends MyShot{
	
	private static final int defaultSize = 32;
	public MyLaser previousLaser;
	public int normalFrameIndex;
	PointF drawSize = new PointF(defaultSize, defaultSize);
	
	
	public MyLaser(ObjectsContainer objectsContainer) {
		super(objectsContainer);
		
	}
	
	public void setFromPreviousLaser(MyLaser previousLaser){
	
		this.previousLaser = previousLaser;
		
		if(previousLaser!=null){
			
			float preLowerEnd = previousLaser.y + previousLaser.drawSize.y / 2;
			float height = (y - preLowerEnd) * 2;
			drawSize.set(defaultSize, height);
		}
	}
	
	public void setExplosion(){
		
		isInExplosion = true;
	}
	
	public void onDraw(GL10 gl){
			
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
					normalFrameIndex
					);
			
			if(previousLaser == null){
				
				animationManager.drawFrame(drawCenter);
			}
			else{
				
				animationManager.drawFlexibleFrame(drawCenter, drawSize);
			}
		}
		gl.glPopMatrix();	
		
		if(isInExplosion){
		
			drawCenter.set(x, y);
		
			animationManager.setFrame(
					animeSet.getData(AnimeKind.EXPLOSION,0),
					animeFrame
					);
			animationManager.drawFrame(drawCenter);
		}
	}
	
	protected void flyAhead(){
		
		super.flyAhead();
		
		x = plane.x;
	}
	
	protected void animate(){
		
		if(!isInExplosion) return;
		
		int eva = animationManager.checkAnimeLimit
			(animeSet.getData(animeKind.EXPLOSION,0), ++totalAnimeFrame);
		
		if (eva == -1) isInScreen = false;
		else
			animeFrame = eva;
	}
}
