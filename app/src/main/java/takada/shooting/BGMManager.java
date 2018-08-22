package takada.shooting;

import java.util.Hashtable;
import java.util.Map;
import takada.shooting.SoundEffect.SoundKind;
import takada.shooting.library.ScreenEffect.Callback;
import android.content.Context;
import android.media.MediaPlayer;

public class BGMManager {
	
	static final int frameIntervalTime = Global.frameIntervalTime;

	public enum MusicName{
		GALAXY,
		BOSSFIGHT
	};
	
	static MusicName musicNameFromID[] =
		{	MusicName.GALAXY,
			MusicName.BOSSFIGHT
		};
	
	private enum WorkKind{fadeOut};
	
	static Context context;
	static MediaPlayer mPlayer;
	static private Map<MusicName, Integer> musicMap
	  = new Hashtable<MusicName, Integer>();
	
	public class WorkThread extends Thread{
		
		boolean isActive = false;
		int workActiveCount;
		WorkKind workKind;
		
		float nowVolume, decrementVolume;
		
		public void fadeOut(long fadeOutTime){
			
			if(isActive == false){
				isActive = true;
				workKind = WorkKind.fadeOut;
				workActiveCount = (int)(fadeOutTime / frameIntervalTime);
				nowVolume = 1.0f;
				decrementVolume = 1.0f / workActiveCount; 
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
			
			while(true){
				
				if(!isActive){
					
					if(workKind == WorkKind.fadeOut) mPlayer.stop();
					
					break;
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
			
			switch(workKind){
			
			case fadeOut:
				
				nowVolume -= decrementVolume;
				mPlayer.setVolume(nowVolume, nowVolume);
				
				break;
			
			default:
				break;
			}
			
			if(--workActiveCount < 1){
				
				isActive = false;
			};
		}
		
	}
	
	static public void initialize(Context ct){
		
		context = ct;
		
		musicMap.put(MusicName.GALAXY, R.raw.bgm_stg1);
		musicMap.put(MusicName.BOSSFIGHT, R.raw.bgm_boss);
	}
	
	static public void play(MusicName musicName){
		
		int musicResourceID = musicMap.get(musicName);
		
		mPlayer = MediaPlayer.create(context, musicResourceID);
		
		mPlayer.start();
	}
	
	static public void playBGM(int eventObjectID){
		
		play(musicNameFromID[eventObjectID]);
	}
	
	static public void fadeOutVolume(int fadeOutSec){
		
		new BGMManager().new WorkThread().fadeOut(fadeOutSec * 1000);
	}
}
