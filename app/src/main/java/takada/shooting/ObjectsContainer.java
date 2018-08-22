package takada.shooting;

import android.content.Context;

public class ObjectsContainer {
	
	boolean isInitialized = false;
	
	StageManager stageManager;
	AnimationManager animationManager;
	GraphicPad pad;
	MyPlane plane;
	MyShotGenerator shotGenerator;
	EnemyGenerator enemyGenerator;
	ItemGenerator itemGenerator;
	CollisionDetection collisionDetection;
	Indicator indicator;
	DerivativeEnemyManager derivativeEnemyManager;
	
	public ObjectsContainer(Context context){
		
		animationManager = new AnimationManager(context);
		pad = new GraphicPad();
		plane = new MyPlane();
		shotGenerator = new MyShotGenerator(context);
		enemyGenerator = new EnemyGenerator();
		itemGenerator = new ItemGenerator();
		collisionDetection = new CollisionDetection();
		stageManager = new StageManager(context);
		indicator = new Indicator(context);
		derivativeEnemyManager = new DerivativeEnemyManager();
	}
	
	public void initialize(){
		
		animationManager.initialize();
		stageManager.initialize(this);
		pad.initialize();
		plane.initialize(this);
		shotGenerator.initialize(this);
		enemyGenerator.initialize(this);
		itemGenerator.initialize(this);
		collisionDetection.initialize(this);
		indicator.initialize(this);
		derivativeEnemyManager.initialize(this);
		
		isInitialized = true;
	}
}
