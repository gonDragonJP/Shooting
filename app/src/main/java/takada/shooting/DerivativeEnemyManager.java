package takada.shooting;

import java.util.Hashtable;
import java.util.Map;

import takada.derivativeenemies.*;
import takada.shooting.AnimationManager.AnimationData;
import takada.shooting.Global.EnemyCategory;
import takada.shooting.structure.EnemyData;

public class DerivativeEnemyManager {
	
	ObjectsContainer objectsContainer;
	
	enum DerivativeEnemyKind {
		BLOCKER_TYPE0, BOMB_GAS, LASER_TYPE0, MAKE_SCATTER_TYPE0, CARRIER_BOSS,
		HARRICANE, MINE_TYPE0
		};
	
	Map<String, DerivativeEnemyKind> nameToDEKindTable
	  = new Hashtable<String, DerivativeEnemyKind>();
	
	public void initialize(ObjectsContainer container){
	
		objectsContainer = container;
	}
	
	public void clearDETable(){
		
		nameToDEKindTable.clear();
	}
	
	public Enemy getDerivativeEnemy(int stage, EnemyData data){
		
		Enemy derivativeEnemy;
		
		if (EnemyData.enemyCategoryFromID[data.objectID/1000] == EnemyCategory.PARTS)
			
			derivativeEnemy = new DE_Parts(objectsContainer);
		
		else{
			switch(stage){
		
			case 1: 
				derivativeEnemy = getStage1DE(data.name);
				break;
				
			case 2: 
				derivativeEnemy = getStage2DE(data.name);
				break;
		
			default:
				derivativeEnemy = null;
			}
		}
		
		return derivativeEnemy;
	}
	
	private void setStage1DE(){
		
		nameToDEKindTable.put("blocker0", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("blocker1", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("blocker2", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("bomb_gas", DerivativeEnemyKind.BOMB_GAS);
		nameToDEKindTable.put("laser_head", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("laser_body", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("laser_tail", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("mak_scatter0", DerivativeEnemyKind.MAKE_SCATTER_TYPE0);
		nameToDEKindTable.put("bossCarrier", DerivativeEnemyKind.CARRIER_BOSS);
	}
	
	private void setStage2DE(){
		
		nameToDEKindTable.put("blocker0", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("blocker1", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("blocker2", DerivativeEnemyKind.BLOCKER_TYPE0);
		nameToDEKindTable.put("bomb_gas", DerivativeEnemyKind.BOMB_GAS);
		nameToDEKindTable.put("laser_head", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("laser_body", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("laser_tail", DerivativeEnemyKind.LASER_TYPE0);
		nameToDEKindTable.put("mak_scatter0", DerivativeEnemyKind.MAKE_SCATTER_TYPE0);
		nameToDEKindTable.put("bossCarrier", DerivativeEnemyKind.CARRIER_BOSS);
		
		nameToDEKindTable.put("harricane0", DerivativeEnemyKind.HARRICANE);
		nameToDEKindTable.put("harricane1", DerivativeEnemyKind.HARRICANE);
		nameToDEKindTable.put("mine0", DerivativeEnemyKind.MINE_TYPE0);
	}
	
	private Enemy getStage1DE(String enemyName){
		
		if(nameToDEKindTable.isEmpty()) setStage1DE();
		
		Enemy derivativeEnemy;
		DerivativeEnemyKind kind = nameToDEKindTable.get(enemyName);
		
		switch(kind){
		
		case BLOCKER_TYPE0:
			derivativeEnemy = new DE_blocker_Type0(objectsContainer);
			break;
			
		case BOMB_GAS:
			derivativeEnemy = new DE_bomb_gas(objectsContainer);
			break;
			
		case LASER_TYPE0:
			derivativeEnemy = new DE_laser_Type0(objectsContainer);
			break;
			
		case MAKE_SCATTER_TYPE0:
			derivativeEnemy = new DE_make_scatter_Type0(objectsContainer);
			break;
			
		case CARRIER_BOSS:
			derivativeEnemy = new DE_carrier_Boss(objectsContainer);
			break;
			
		default:
			derivativeEnemy = null;
		}
		
		return derivativeEnemy;
	}
	
	private Enemy getStage2DE(String enemyName){
		
		if(nameToDEKindTable.isEmpty()) setStage2DE();
		
		Enemy derivativeEnemy;
		DerivativeEnemyKind kind = nameToDEKindTable.get(enemyName);
		
		switch(kind){
		
		case BLOCKER_TYPE0:
			derivativeEnemy = new DE_blocker_Type0(objectsContainer);
			break;
			
		case BOMB_GAS:
			derivativeEnemy = new DE_bomb_gas(objectsContainer);
			break;
			
		case LASER_TYPE0:
			derivativeEnemy = new DE_laser_Type0(objectsContainer);
			break;
			
		case MAKE_SCATTER_TYPE0:
			derivativeEnemy = new DE_make_scatter_Type0(objectsContainer);
			break;
			
		case CARRIER_BOSS:
			derivativeEnemy = new DE_carrier_Boss(objectsContainer);
			break;
			
		case HARRICANE:
			derivativeEnemy = new DE_harricane(objectsContainer);
			break;
			
		case MINE_TYPE0:
			derivativeEnemy = new DE_mine_Type0(objectsContainer);
			break;
			
		default:
			derivativeEnemy = null;
		}
		
		return derivativeEnemy;
	}
}
