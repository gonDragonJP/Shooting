package takada.shooting;

import takada.shooting.library.Double2Vector;
import takada.shooting.structure.EnemyData.CollisionRegion;

public class CollisionDetection {
	
	private MyPlane plane;
	private MyShotGenerator shotGenerator;
	private EnemyGenerator enemyGenerator;
	private ItemGenerator itemGenerator;
	
	private Double2Vector vecA = new Double2Vector();
	private Double2Vector vecB = new Double2Vector();
	private Double2Vector vecC = new Double2Vector();
	
	public void initialize(ObjectsContainer objectsContainer){
	
		plane = objectsContainer.plane;
		shotGenerator = objectsContainer.shotGenerator;
		enemyGenerator = objectsContainer.enemyGenerator;
		itemGenerator = objectsContainer.itemGenerator;
	}

	synchronized public void doAllDetection(){
		
		shotVsEnemy();
		planeVsEnemy();
		planeVsItem();
	}
	
	private void shotVsEnemy(){
		
		int shotCount = shotGenerator.list.size();
		int enemyCount = enemyGenerator.list.size();
		
		for(int i=0; i<shotCount; i++){
			
			MyShot shot = shotGenerator.list.get(i);
			if(shot.isInExplosion) break;
			
			int shotRadius = shot.shotRadius;
			
			for(int j=0; j<enemyCount; j++){
	
				Enemy enemy = enemyGenerator.list.get(j);	
				
				if(enemy.hitPoints<=0) continue;
				
				for(int k=0; k<enemy.colNumber; k++){
					
					CollisionRegion col = enemy.collisionRotated.get(k);
					int x = enemy.x + col.centerX;
					int y = enemy.y + col.centerY;
					int radius = col.size + shotRadius;
				
					vecA.set(shot.x, shot.y);
					vecB.set(x, y);
					vecC.copy(vecA);
					vecC.minus(vecB);
				
					if(vecC.length()<radius) shootEnemy(shot, enemy);
				}
			}
		}
	}
	
	private void shootEnemy(MyShot shot, Enemy enemy){
		
		shot.setExplosion();
		
		enemy.hitPoints -= shot.shotPower;
		if(enemy.hitPoints <= 0){
			
			enemy.setExplosion();
			enemy.dropItem(shot.isLaser);
		}
	}
	
	private void planeVsEnemy(){
		
		int enemyCount = enemyGenerator.list.size();
		int planeRadius = plane.collisionRadius;
			
		for(int j=0; j<enemyCount; j++){
	
			Enemy enemy = enemyGenerator.list.get(j);		
			if(enemy.isInExplosion|enemy.isGrounder) continue;
				
			for(int k=0; k<enemy.colNumber; k++){
					
				CollisionRegion col = enemy.collisionRotated.get(k);
				int x = enemy.x + col.centerX;
				int y = enemy.y + col.centerY;
				int radius = col.size + planeRadius;
				
				vecA.set(plane.x, plane.y);
				vecB.set(x, y);
				vecC.copy(vecA);
				vecC.minus(vecB);
				
				if(vecC.length()<radius) shootPlane(enemy);
			}
		}
	}
	
	private void shootPlane(Enemy enemy){
		
		enemy.setExplosion();
	
		plane.setDamaged(enemy.atackPoints);
	}
	
	private void planeVsItem(){
		
		int itemCount = itemGenerator.list.size();
		int planeRadius = plane.collisionRadius;
		int radius;
		
		for(int i=0; i<itemCount; i++){
				
			Item item = itemGenerator.list.get(i);	
			radius = planeRadius + item.radius;
				
			vecA.set(plane.x, plane.y);
			vecB.set(item.x, item.y);
			vecC.copy(vecA);
			vecC.minus(vecB);
				
			if(vecC.length()<radius) getItem(item);
		}
	}
	
	private void getItem(Item item){
		
		item.gotByPlane();
		
	}
}
