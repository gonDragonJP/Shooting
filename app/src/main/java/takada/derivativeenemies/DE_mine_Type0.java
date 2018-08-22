package takada.derivativeenemies;

import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;
import takada.shooting.SoundEffect;
import takada.shooting.SoundEffect.SoundKind;

public class DE_mine_Type0 extends Enemy{

	public DE_mine_Type0(ObjectsContainer container) {
		super(container);
	}
	
	@Override
	public void setExplosion(){
		
		super.setExplosion();
		
		cueingGenerating(0 , 999);
	}
	
	@Override
	public void periodicalProcess(){
	 super.periodicalProcess();
	 
	 	if((isInExplosion)&&(genNumber > genIndex)) checkGeneratingCount();
	}
}
