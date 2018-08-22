package takada.shooting;

import takada.shooting.BGMManager.MusicName;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Shooting extends Activity {

	private MediaPlayer mPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        SoundEffect.initialize(this);
        BGMManager.initialize(this);
        ObjectsContainer objects = new ObjectsContainer(this);
        GameThread gameThread = new GameThread(objects);
        MainRenderer renderer = new MainRenderer(objects);
        MainGLSurface mainSurface = new MainGLSurface(this);     
        
        mainSurface.setRenderer(renderer);
        setLayout(mainSurface);
       
        gameThread.start();
    }
    
    private final int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
    private final int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    
    private void setLayout(MainGLSurface mainSurface){
    	
    	LinearLayout mainLayout = new LinearLayout(this);
    	mainLayout.setLayoutParams(getParams(matchParent,matchParent));
 
        mainLayout.addView(mainSurface);
    	setContentView(mainLayout);
    }
    
    private LinearLayout.LayoutParams getParams(int arg0, int arg1){
    	
    	return new LinearLayout.LayoutParams(arg0, arg1);
    }
}