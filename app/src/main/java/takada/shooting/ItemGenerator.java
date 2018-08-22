package takada.shooting;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import takada.shooting.library.Double2Vector;
import takada.shooting.AnimationManager.AnimationSet;
import takada.shooting.structure.EnemyData;
import android.annotation.SuppressLint;
import android.graphics.Point;

public class ItemGenerator {
	
	public enum ItemKind{ SHIELDENERGY, WEAPONENERGY};
	
	class ItemData{
		
		ItemKind kind;
		Point startPos = new Point();
		Double2Vector startVelocity = new Double2Vector();
		Double2Vector attractAcceleration = new Double2Vector();
		int attractRange;
		double maxVelocity;
		int radius;
		AnimationSet animeSet;
		
		public void initialize(){
			
			kind = null;
			startPos.set(0, 0);
			startVelocity.set(0, 0);
			attractAcceleration.set(0, 0);
			attractRange = 0;
			maxVelocity = 0;
			radius = 0;
			animeSet = null;
		}
	}

	public ObjectsContainer objectsContainer;
	private AnimationManager animationManager;
	static private ItemData itemData = new ItemGenerator().new ItemData();

	ArrayList<Item> list = new ArrayList<Item>();
	
	Point startPos = new Point();
	Double2Vector velocity = new Double2Vector();
	Double2Vector acceleration = new Double2Vector();

	public void initialize(ObjectsContainer objectsContainer){
		
		this.objectsContainer = objectsContainer;
		animationManager = objectsContainer.animationManager;
	}
	
	public void resetAllItems(){
		
		list.clear();
	}
	
	public void addShieldEnergy(int x, int y){
		
		itemData.initialize();
		
		itemData.kind = ItemKind.SHIELDENERGY;
		itemData.startPos.x = x;
		itemData.startPos.y = y;
		
		itemData.startVelocity.set(0, 2);
		itemData.attractAcceleration.set(1, 1);
		itemData.attractRange = 150;
		itemData.maxVelocity = 8;
		itemData.radius = 16;
		
		itemData.animeSet = animationManager.getAnimationSet
			(AnimationManager.AnimeObject.SHIELDENERGY, 0);
		
		addItem();
	}
	
	public void addWeaponEnergy(int x, int y){
		
		itemData.initialize();
		
		itemData.kind = ItemKind.WEAPONENERGY;
		itemData.startPos.x = x;
		itemData.startPos.y = y;
		
		itemData.startVelocity.set(0, 2);
		itemData.attractAcceleration.set(1, 1);
		itemData.attractRange = 150;
		itemData.maxVelocity = 8;
		itemData.radius = 16;
		
		itemData.animeSet = animationManager.getAnimationSet
			(AnimationManager.AnimeObject.WEAPONENERGY, 0);
		
		addItem();
	}
	
	public void addItem(){
		
		Item item = new Item(objectsContainer);
		item.setParameter(itemData);
		
		list.add(item);
	}
	
	@SuppressLint("WrongCall")
	synchronized public void onDraw(GL10 gl){
		
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).onDraw(gl);
		}
	}
	
	synchronized public void periodicalProcess(){
			
		childPeriodicalProcess();
		checkPositionLimit();
	}
	
	private void childPeriodicalProcess()
	{
		int j = list.size();
		for(int i=0; i<j; i++){
			
			list.get(i).periodicalProcess();
		}
	}
	
	private void checkPositionLimit(){
		
		int j=list.size()-1;
		for(int i=j; i>=0; i--){
			
			Item item = list.get(i);
			if(item.isInScreen == false) list.remove(i);
		}
	}

}
