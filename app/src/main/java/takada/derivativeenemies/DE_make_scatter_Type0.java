package takada.derivativeenemies;

import takada.shooting.Enemy;
import takada.shooting.ObjectsContainer;

public class DE_make_scatter_Type0 extends Enemy{

	public DE_make_scatter_Type0(ObjectsContainer container) {
		super(container);
	}
	
	@Override
	protected void checkGeneratingCount(){
		super.checkGeneratingCount();
		
		if(tempGenChild!=null){
			
			//tempGenChild.addNodeDuration(0, -20);
			tempGenChild.addNodeDuration(0, -(3 - genRepeat)*15);
			tempGenChild.addNodeDuration(1, genRepeat*5);
			tempGenChild.slideStartFrame(0, -(3 - genRepeat)*15);
			
		}
	}

}
