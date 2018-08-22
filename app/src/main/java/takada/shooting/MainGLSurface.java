package takada.shooting;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MainGLSurface extends GLSurfaceView{
	
	MainRenderer renderer;

	public MainGLSurface(Context context) {
		super(context);

	}
	
	public void setRenderer(GLSurfaceView.Renderer renderer){
		super.setRenderer(renderer);
		
		this.renderer = (MainRenderer) renderer;
	}
	
	public boolean onTouchEvent(MotionEvent event){
		
		renderer.objects.pad.onTouch(event);
		
		return true;
	}

}
