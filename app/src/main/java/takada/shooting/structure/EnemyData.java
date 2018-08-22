package takada.shooting.structure;

import java.util.ArrayList;

import android.graphics.Point;
import takada.shooting.library.Double2Vector;
import takada.shooting.Enemy;
import takada.shooting.Global;
import takada.shooting.Global.EnemyCategory;
import takada.shooting.MyPlane;
import takada.shooting.Global.CollisionShape;
import takada.shooting.Global.StartPositionAtrib;
import takada.shooting.Global.StartVectorAtrib;

public class EnemyData {
	
	public static EnemyCategory[] enemyCategoryFromID ={
		
		EnemyCategory.FLYING,
		EnemyCategory.GROUND,
		EnemyCategory.MAKER,
		EnemyCategory.BULLET,
		EnemyCategory.PARTS
	};
	
	public static StartPositionAtrib[] startAtribFromID = {
		StartPositionAtrib.DEFAULT, 
		StartPositionAtrib.PLANESIDE,
		StartPositionAtrib.COUNTERPLANESIDE,
		StartPositionAtrib.SAMEPLANE
	};
	
	public static StartVectorAtrib[] vectorAtribFromID = {
		StartVectorAtrib.DEFAULT,
		StartVectorAtrib.ADOPTSIGNTOCENTER,
		StartVectorAtrib.ADOPTSIGNTOCOUNTERCENTER,
		StartVectorAtrib.TENDTOPLANE,
		StartVectorAtrib.TENDPARENTAHEAD,
		StartVectorAtrib.ADOPTSIGNTOPLANE,
		StartVectorAtrib.ADOPTSIGNTOCOUNTERPLANE,
		StartVectorAtrib.TENDPARENTFACEON
	};
	
	static Double2Vector examinedVel = new Double2Vector();
	static Double2Vector examinedAcc = new Double2Vector();
	
	static boolean signToCenter, signToPlane;
	static double tendToPlaneFactor, tendParentAheadFactor, tendParentFaceOnFactor;
	static Double2Vector tendToPlaneVelocity = new Double2Vector();
	static Double2Vector tendParentAheadVelocity = new Double2Vector();
	static Double2Vector tendParentFaceOnVelocity = new Double2Vector();
	static Double2Vector tendToPlaneAcceleration = new Double2Vector();
	static Double2Vector tendParentAheadAcceleration = new Double2Vector();
	static Double2Vector tendParentFaceOnAcceleration = new Double2Vector();
	
	private static void setSpecialVelocityVectors
					(Enemy enemySelf, Double2Vector startVelocity){
		
		Enemy parent = enemySelf.parentEnemy;
		MyPlane plane = enemySelf.plane;
		double speed = startVelocity.length();
		
		tendToPlaneVelocity.set(plane.x - enemySelf.x, plane.y - enemySelf.y);
		tendToPlaneVelocity.normalize(speed);
		
		if(parent !=null){
			tendParentAheadVelocity.set(parent.velocity.x, parent.velocity.y);
			tendParentAheadVelocity.normalize(speed);
			
			tendParentFaceOnVelocity.copy(parent.getUnitVectorOfFaceOn());
			tendParentFaceOnVelocity.normalize(speed);
		}
	}
	
	private static void setSpecialAccelerationVectors
					(Enemy enemySelf, Double2Vector startAcceleration){

		Enemy parent = enemySelf.parentEnemy;
		MyPlane plane = enemySelf.plane;
		double speed = startAcceleration.length();

		tendToPlaneAcceleration.set(plane.x - enemySelf.x, plane.y - enemySelf.y);
		tendToPlaneAcceleration.normalize(speed);

		if(parent !=null){
			tendParentAheadAcceleration.set(parent.acceleration.x, parent.acceleration.y);
			tendParentAheadAcceleration.normalize(speed);

			tendParentFaceOnAcceleration.copy(parent.getUnitVectorOfFaceOn());
			tendParentFaceOnAcceleration.normalize(speed);
		}
	}
	
	private static double getEvaluateVectorValue
		(double evaFactor, StartVectorAtrib atrib){

		double positiveVal = Math.abs(evaFactor);
		double negativeVal = -positiveVal;

		switch(atrib){

		case DEFAULT:

			return evaFactor;

		case ADOPTSIGNTOCENTER:

			if(signToCenter)
				return positiveVal;
			else
				return negativeVal;
	
		case ADOPTSIGNTOCOUNTERCENTER:

			if(signToCenter)
				return negativeVal;
			else
				return positiveVal;

		case TENDTOPLANE:

			return tendToPlaneFactor;
		
		case TENDPARENTAHEAD:
		
			return tendParentAheadFactor;
		
		case ADOPTSIGNTOPLANE:

			if(signToPlane)
				return positiveVal;
			else
				return negativeVal;
	
		case ADOPTSIGNTOCOUNTERPLANE:

			if(signToPlane)
				return negativeVal;
			else
				return positiveVal;
		
		case TENDPARENTFACEON:
		
			return tendParentFaceOnFactor;
		}

		return evaFactor;
	}
	
	private static int getEvaluatePositionValue
		(int posRate, int screenRange, int planePos, StartPositionAtrib atrib){
	
		// planePos‚Í‰æ–Ê¶’[‚©‚ç‚Ì‹——£
	
		int defVal    = (int)(screenRange * posRate / 100);
		int cntVal    = screenRange - defVal;
		int centerVal = screenRange / 2;
	
		boolean isSameSide =(defVal>centerVal && planePos>centerVal)||
									(defVal<centerVal && planePos<centerVal);

		switch(atrib){
	
		case DEFAULT:

			return defVal;

		case PLANESIDE:

			if(isSameSide)
				return defVal;
			else
				return cntVal;
	
		case COUNTERPLANESIDE:

			if(isSameSide)
				return cntVal;
			else
				return defVal;

		case SAMEPLANE:

			return planePos;
		}
	
		return defVal;
	}
	
	public String name;
	public boolean isDerivativeType = false;
	public int objectID = -1;
	public int textureID = -1;
	public int hitPoints;
	public int atackPoints;
	public Point startPosition = new Point();
	public Point startPositionAttribute = new Point();
	
	
	public class MovingNode {
		
		public Double2Vector startVelocity = new Double2Vector();
		public Double2Vector startAcceleration = new Double2Vector();
		public Double2Vector homingAcceleration = new Double2Vector();
		public int nodeDurationFrame;
		public Point startVelocityAttribute = new Point();
		public Point startAccelerationAttribute = new Point();
		
		public void copy(MovingNode srcNode){
			
			startVelocity.copy(srcNode.startVelocity);
			startAcceleration.copy(srcNode.startAcceleration);
			homingAcceleration.copy(srcNode.homingAcceleration);
			nodeDurationFrame = srcNode.nodeDurationFrame;
			startVelocityAttribute.set
			(srcNode.startVelocityAttribute.x, srcNode.startVelocityAttribute.y);
			startAccelerationAttribute.set
			(srcNode.startAccelerationAttribute.x, srcNode.startAccelerationAttribute.y);
		
		}
		
		public Double2Vector getStartVelocityWithAtrib(Enemy enemySelf){
			
			StartVectorAtrib atrib;
			MyPlane plane = enemySelf.plane;
			setSpecialVelocityVectors(enemySelf, startVelocity);
		
			atrib = vectorAtribFromID[startVelocityAttribute.x];
			signToPlane = plane.x > enemySelf.x;
			signToCenter = Global.virtualScreenLimit.centerX() > enemySelf.x;
			tendToPlaneFactor = tendToPlaneVelocity.x;
			tendParentAheadFactor = tendParentAheadVelocity.x;
			tendParentFaceOnFactor = tendParentFaceOnVelocity.x;
			
			examinedVel.x = getEvaluateVectorValue(startVelocity.x, atrib);

			atrib = vectorAtribFromID[startVelocityAttribute.y];
			signToPlane = plane.y > enemySelf.y;
			signToCenter = Global.virtualScreenLimit.centerY() > enemySelf.y;
			tendToPlaneFactor = tendToPlaneVelocity.y;
			tendParentAheadFactor = tendParentAheadVelocity.y;
			tendParentFaceOnFactor = tendParentFaceOnVelocity.y;
			
			examinedVel.y = getEvaluateVectorValue(startVelocity.y, atrib);
			
			return examinedVel;
		}
	
		public Double2Vector getStartAccelerationWithAtrib (Enemy enemySelf){
	
			StartVectorAtrib atrib;
			MyPlane plane = enemySelf.plane;
			setSpecialAccelerationVectors(enemySelf, startAcceleration);
		
			atrib = vectorAtribFromID[startAccelerationAttribute.x];
			signToPlane = plane.x > enemySelf.x;
			signToCenter = Global.virtualScreenLimit.centerX() > enemySelf.x;
			tendToPlaneFactor = tendToPlaneAcceleration.x;
			tendParentAheadFactor = tendParentAheadAcceleration.x;
			tendParentFaceOnFactor = tendParentFaceOnAcceleration.x;
			
			examinedAcc.x = getEvaluateVectorValue(startAcceleration.x, atrib);

			atrib = vectorAtribFromID[startAccelerationAttribute.y];
			signToPlane = plane.y > enemySelf.y;
			signToCenter = Global.virtualScreenLimit.centerY() > enemySelf.y;
			tendToPlaneFactor = tendToPlaneAcceleration.y;
			tendParentAheadFactor = tendParentAheadAcceleration.y;
			tendParentFaceOnFactor = tendParentFaceOnAcceleration.y;
			
			examinedAcc.y = getEvaluateVectorValue(startAcceleration.y, atrib);
	
			return examinedAcc;
		}
	}
	
	public class GeneratingChild {
		
		public int objectID = -1;
		public int repeat;
		public int startFrame;
		public int intervalFrame;
		public int centerX, centerY;
		
		public void copy(GeneratingChild srcGen){
			
			objectID = srcGen.objectID;
			repeat = srcGen.repeat;
			startFrame = srcGen.startFrame;
			intervalFrame = srcGen.intervalFrame;
			centerX = srcGen.centerX;
			centerY = srcGen.centerY;
		}
	}
	
	public class CollisionRegion {
		
		public Global.CollisionShape[] shapeByID = {
			Global.CollisionShape.CIRCLE,
			Global.CollisionShape.RECTANGLE,
		};
		
		public int centerX;
		public int centerY;
		public int size;
		public Global.CollisionShape collisionShape = Global.CollisionShape.CIRCLE;
		
		public void copy(CollisionRegion src){
			
			centerX = src.centerX;
			centerY = src.centerY;
			size = src.size;
			collisionShape = src.collisionShape;
		}
	}

	public ArrayList<MovingNode> node = new ArrayList<MovingNode>();
	public ArrayList<GeneratingChild> generating = new ArrayList<GeneratingChild>();
	public ArrayList<CollisionRegion> collision = new ArrayList<CollisionRegion>();
	
	public void initialize(){
		
		name = "";
		isDerivativeType = false;
		objectID = -1;
		textureID = -1;
		
		hitPoints = 0;
		atackPoints = 0;
		
		startPosition.set(0, 0);
		startPositionAttribute.set(0, 0);
		node.clear();
		generating.clear();
		collision.clear();
	}
	
	public Point getStartPositionWithAtrib(MyPlane plane){
		
		Point resultPos = new Point();
		StartPositionAtrib atrib;
		int screenRange, screenStart;
		
		atrib = startAtribFromID[startPositionAttribute.x];
		screenRange = (int)Global.virtualScreenLimit.width();
		screenStart = (int)Global.virtualScreenLimit.left;
		resultPos.x = screenStart + 
			getEvaluatePositionValue
				(startPosition.x, screenRange, plane.x - screenStart, atrib);
		
		atrib = startAtribFromID[startPositionAttribute.y];
		screenRange = (int)Global.virtualScreenLimit.height();
		screenStart = (int)Global.virtualScreenLimit.top;
		resultPos.y = screenStart + 
			getEvaluatePositionValue
				(startPosition.y, screenRange, plane.y - screenStart, atrib);
		
		return resultPos;
	}
	
	public void cloneNodeList(ArrayList<MovingNode> dstNodeList){
		
		for(int i=0; i<node.size(); i++){
			
			MovingNode nd = new MovingNode();
			nd.copy(node.get(i));
			dstNodeList.add(nd);
		}
	}
	
	public void cloneGenList(ArrayList<GeneratingChild> dstGenList){
		
		for(int i=0; i<generating.size(); i++){
			
			GeneratingChild gen = new GeneratingChild();
			gen.copy(generating.get(i));
			dstGenList.add(gen);
		}
	}
}
