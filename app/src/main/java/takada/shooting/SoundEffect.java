package takada.shooting;

import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundEffect {
	
	public enum SoundKind{
		MYSHOT, MYLASER , SIREN , EXPLOSION1, CHARGE, GETSEED, SHIELDING,
		HARRICANE
	};
	
	private class SoundData{
		
		int soundID; 
		float leftVolume, rightVolume;
		int priority, loop;
		float rate;
		
		public SoundData(int soundID){
			
			this.soundID = soundID;
			leftVolume = rightVolume = 100;
			priority = 1;
			loop = 0;
			rate = 1.0f;
		}
		
		public void setVolume(float volume){
			
			leftVolume = rightVolume = volume;
		}
	}
	
	static final int resourceNumber = 8;
	static private SoundPool soundPool;
	static private Map<SoundKind, SoundData> soundMap
	  = new Hashtable<SoundKind, SoundData>();
	
	static public void initialize(Context context){
		
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		int[] resourceIDs = new int[resourceNumber];
		SoundKind[] soundKind = new SoundKind[resourceNumber];
		float[] volume = new float[resourceNumber];
		int[] loop = new int[resourceNumber];
		
		soundKind[0] = SoundKind.MYSHOT;
		resourceIDs[0] = R.raw.se_myshot;
		volume[0] = 0.5f;
		
		soundKind[1] = SoundKind.MYLASER;
		resourceIDs[1] = R.raw.se_mylaser;
		volume[1] = 0.5f;
		
		soundKind[2] = SoundKind.SIREN;
		resourceIDs[2] = R.raw.se_siren;
		volume[2] = 1.0f;
		loop[2] = 3;
		
		soundKind[3] = SoundKind.EXPLOSION1;
		resourceIDs[3] = R.raw.se_explosion2;
		volume[3] = 0.5f;
		
		soundKind[4] = SoundKind.CHARGE;
		resourceIDs[4] = R.raw.se_charge;
		volume[4] = 1.0f;
		
		soundKind[5] = SoundKind.GETSEED;
		resourceIDs[5] = R.raw.se_getitem;
		volume[5] = 1.5f;
		
		soundKind[6] = SoundKind.SHIELDING;
		resourceIDs[6] = R.raw.se_shielding;
		volume[6] = 1.0f;
		
		soundKind[7] = SoundKind.HARRICANE;
		resourceIDs[7] = R.raw.se_harricane;
		volume[7] = 0.8f;
		
		for(int i=0; i<resourceNumber; i++){
			
			int soundID = soundPool.load(context, resourceIDs[i], 1);
			SoundData data = new SoundEffect().new SoundData(soundID);
			
			if(volume[i]!=0) data.setVolume(volume[i]);
			if(loop[i]!=0) data.loop = loop[i];
			
			soundMap.put(soundKind[i], data);
		}
	}
	
	static public void play(SoundKind soundKind){
		
		SoundData data  = soundMap.get(soundKind);
		soundPool.play(
				data.soundID, data.leftVolume, data.rightVolume,
				data.priority, data.loop, data.rate);
	}
	
	static public void play(SoundKind soundKind, float volumeRate){
		
		SoundData data  = soundMap.get(soundKind);
		soundPool.play(
				data.soundID, data.leftVolume*volumeRate, data.rightVolume*volumeRate,
				data.priority, data.loop, data.rate);
	}
	
	static public void finish(){
		
		soundPool.release();
	}
}
