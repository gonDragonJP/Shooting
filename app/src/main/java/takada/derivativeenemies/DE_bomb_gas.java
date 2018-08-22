package takada.derivativeenemies;

import takada.shooting.AnimationManager;
import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;
import takada.shooting.SoundEffect;
import takada.shooting.SoundEffect.SoundKind;

public class DE_bomb_gas extends Enemy{
	
	boolean isFinishedInitialSound = false;

	public DE_bomb_gas(ObjectsContainer container) {
		super(container);
		
	}
	
	@Override
	public void setExplosion(){
		
	}
	
	@Override
	public void periodicalProcess(){
	 super.periodicalProcess();
	 
	 if(!isFinishedInitialSound){
		 SoundEffect.play(SoundKind.EXPLOSION1);
		 isFinishedInitialSound = true;
	 }
	}

}
