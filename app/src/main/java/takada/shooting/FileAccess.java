package takada.shooting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import takada.shooting.AnimationManager.AnimationData;
import takada.shooting.AnimationManager.AnimationSet;
import takada.shooting.AnimationManager.RepeatAttribute;
import takada.shooting.AnimationManager.RotateAttribute;
import takada.shooting.R;
import takada.shooting.FileAccess.Token;
import takada.shooting.structure.EnemyData;
import takada.shooting.structure.EventData;
import takada.shooting.structure.EnemyData.CollisionRegion;
import takada.shooting.structure.EnemyData.GeneratingChild;
import takada.shooting.structure.EnemyData.MovingNode;
import takada.shooting.structure.EventData.EventCategory;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class FileAccess {
	
	enum Token{	_EnemyData("/EnemyData"), EnemyData("EnemyData"), Start("Start"),
				_MovingNode("/MovingNode"), MovingNode("MovingNode"),
				_GeneratingChild("/GeneratingChild"), 
				GeneratingChild("GeneratingChild"),
				_CollisionRegion("/CollisionRegion"),
				CollisionRegion("CollisionRegion"),
				_Sight("/Sight"), Sight("Sight"),
				Node("Node"), Generating("Generating"), Collision("Collision"),
				NormalAnime("NormalAnime"), ExplosionAnime("ExplosionAnime"),
				NodeActionAnime("NodeActionAnime"),
				_EventData("/EventData"),EventData("EventData"),
				EnemyAppearance("EnemyAppearance"),
				Default("");
		
		String tokenStr;
		
		Token(String tokenStr){
			
			this.tokenStr = tokenStr;
		}
	};
	
	Context context;
	AnimationManager animationManager;
	AnimationSet animationSet;
	
	EnemyData enemyData;
	ArrayList<EnemyData> enemyList;
	
	EventData eventData;
	ArrayList<EventData> eventList;
		
	String phrase;
	int phraseLine, readPoint;
	
	ArrayList<String> fileText = new ArrayList<String>();
	
	
	public FileAccess(Context context){
		
		this.context = context;
		
	}
	
	public void setManager(AnimationManager animationManager){
		
		this.animationManager = animationManager;
	}
	
	public void setEnemyList(ArrayList<EnemyData> list,int stageNumber){
		
		int[] enemyFileID ={
				
				R.raw.enemy_stage1,
				R.raw.enemy_stage2,
				R.raw.enemy_stage3,
				R.raw.enemy_stage3,
				R.raw.enemy_stage3,
				R.raw.enemy_stage3,
				R.raw.enemy_stage3
		};
		
		enemyList = list;
		enemyList.clear();
		
		try {
			readFile(context, enemyFileID[stageNumber-1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		interpretText();
	}
	
	public void setEventList(ArrayList<EventData> list,int stageNumber){
		
		int[] eventFileID ={
				
				R.raw.event_stage1,
				R.raw.event_stage2,
				R.raw.event_stage3,
				R.raw.event_stage3,
				R.raw.event_stage3,
				R.raw.event_stage3,
				R.raw.event_stage3
		};
	
		eventList = list;
		eventList.clear();
		
		try {
			readFile(context, eventFileID[stageNumber-1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		interpretText();
	}
	
	private void readFile(Context context, int resID) throws IOException{
		
		fileText.clear();
	
		Resources resource = context.getResources();
		InputStreamReader in 
			= new InputStreamReader(resource.openRawResource(resID));
		
		BufferedReader reader = new BufferedReader(in);
		
		String buffer;
		
		while((buffer = reader.readLine()) !=null){
			
			fileText.add(buffer);
		}
	}

	private void interpretText(){
		
		int textLine = fileText.size();
		
		for(phraseLine=0; phraseLine<textLine; phraseLine++){
			
			if(extractPhrase()){
				
				Token type = getPhraseType();
				readParameter(type);
			}
		}	
	}
	
	private boolean extractPhrase(){
		
		String lineText = fileText.get(phraseLine);
		int startBracket = lineText.indexOf('<');
		int endBracket   = lineText.indexOf('>');
		
		if(startBracket<endBracket){
		
			phrase = lineText.substring(startBracket + 1, endBracket);
			return true;
		}
		return false;
	}
	
	private Token getPhraseType(){
		
		Token[] token = {
				Token._EnemyData, Token.EnemyData, Token.Start,
				Token._MovingNode, Token.MovingNode,
				Token._GeneratingChild, Token.GeneratingChild, 
				Token._CollisionRegion, Token.CollisionRegion,
				Token._Sight, Token.Sight, Token.NormalAnime,
				Token.ExplosionAnime, Token.NodeActionAnime,
				Token.Node, Token.Generating, Token.Collision,
				Token._EventData, Token.EventData, Token.EnemyAppearance
		};

		Token type = Token.Default;

		for(int i=0; i<token.length; i++){
		
			if(phrase.indexOf(token[i].tokenStr,0) !=-1){

				type = token[i]; 
				break;
			}
		}
		return type;
	}
	
	private void readParameter(Token type){
		
		readPoint = 0;

		switch(type){

			case _EnemyData:
				enemyList.add(enemyData);
				
				animationManager.setEnemyAnimationSet(enemyData.objectID, animationSet);
				break;

			case EnemyData:
				enemyData = new EnemyData();
				enemyData.initialize();
				enemyData.name = getStrValue();
				enemyData.objectID = getIntValue();
				enemyData.isDerivativeType = (getIntValue() == 1)? true : false;
				
				animationSet = animationManager.new AnimationSet();
				break;

			case Start:
				enemyData.startPosition.x = (int)getDblValue();
				enemyData.startPosition.y = (int)getDblValue();
				enemyData.startPositionAttribute.x = getIntValue();
				enemyData.startPositionAttribute.y = getIntValue();
				break;
			
			case _MovingNode:
				break;

			case MovingNode:
				break;

			case _GeneratingChild:
				break;

			case GeneratingChild:
				break;
				
			case _CollisionRegion:
				break;
				
			case CollisionRegion:
				enemyData.hitPoints = getIntValue();
				enemyData.atackPoints = getIntValue();
				break;
				
			case _Sight:
				break;
			
			case Sight:
				break;
			
			case NormalAnime:
				addNormalAnime();
				break;
				
			case ExplosionAnime: 
				addExplosionAnime();
				break;
				
			case NodeActionAnime:
				addNodeActionAnime();
				break;

			case Node:
				addNode();
				break;

			case Generating:
				addGenerating();
				break;
				
			case Collision:
				addCollision();
				break;
				
			case _EventData:
				eventList.add(eventData);
				break;
			
			case EventData:
				readEventPhrase();
				break;
			
			case EnemyAppearance:
				eventData.eventObjectID = getIntValue();
				break;
		}

		return;
	}
	
	private String getStrValue(){

		String valueText = extractValueText();

		return valueText;
	}

	private int getIntValue(){

		String valueText = extractValueText();
		if(valueText==null) return -1;

		return Integer.valueOf(valueText);
	}

	private double getDblValue(){

		String valueText = extractValueText();
		if(valueText==null) return -1;

		return Double.valueOf(valueText);
	}

	private String extractValueText(){

		String valueText="";
		
		try{

		int startWquotation = phrase.indexOf('\"',readPoint);
		if(startWquotation ==-1) {
			throw new Exception("error: can't find start quotation.");
		}
		readPoint = startWquotation + 1;

		int endWquotation = phrase.indexOf('\"',readPoint);
		if(endWquotation ==-1) {
			throw new Exception("error: can't find end quotation.");
		}
		readPoint = endWquotation + 1;
		
		valueText = phrase.substring(startWquotation+1, endWquotation);

		}catch(Exception e){
			android.util.Log.e("",e.getMessage());
			valueText = null;
		}
		
		return valueText;
	}
	
	private void readEventPhrase(){
		
		EventCategory[] eventCategory ={
				EventCategory.ENEMYAPPEARANCE,
				EventCategory.BRIEFING,
				EventCategory.BOSSAPPEARANCE,
				EventCategory.PLAYBGM
		};
	
		eventData = new EventData();
		eventData.initialize();
		eventData.scrollPoint =  getIntValue();
		eventData.eventCategory = eventCategory[getIntValue()];
	}
	
	private void addNode(){
		
		MovingNode node = new EnemyData().new MovingNode();

		node.startVelocity.x = getDblValue();
		node.startVelocity.y = getDblValue();
		node.startAcceleration.x = getDblValue();
		node.startAcceleration.y = getDblValue();
		node.homingAcceleration.x = getDblValue();
		node.homingAcceleration.y = getDblValue();
		node.nodeDurationFrame = getIntValue();
		node.startVelocityAttribute.x = getIntValue();
		node.startVelocityAttribute.y = getIntValue();
		node.startAccelerationAttribute.x = getIntValue();
		node.startAccelerationAttribute.y = getIntValue();

		enemyData.node.add(node);
	}
	
	private void addGenerating(){
		
		GeneratingChild gen = new EnemyData().new GeneratingChild();

		gen.objectID = getIntValue();
		gen.repeat = getIntValue();
		gen.startFrame = getIntValue();
		gen.intervalFrame = getIntValue();
		gen.centerX = getIntValue();
		gen.centerY = getIntValue();

		enemyData.generating.add(gen);	
	}
	
	private void addCollision(){
	
		CollisionRegion col = new EnemyData().new CollisionRegion();
		
		col.centerX = getIntValue();
		col.centerY = getIntValue();
		col.size = getIntValue();
		col.collisionShape = col.shapeByID[getIntValue()];
		
		enemyData.collision.add(col);
	}
	
	private void addNormalAnime(){
		
		readAnimePhrase(animationSet.normalAnime);
	}
	
	private void addExplosionAnime(){
		
		readAnimePhrase(animationSet.explosionAnime);
	}
	
	private void addNodeActionAnime(){
		
		AnimationData animeData = animationManager.new AnimationData();
		int keyNode = getIntValue();
		animationSet.nodeActionAnime.put(keyNode, animeData);
		
		readAnimePhrase(animeData);
	}
	
	private void readAnimePhrase(AnimationData animeData){
		
		RepeatAttribute[] repeatAttrib ={
				RepeatAttribute.LOOP,
				RepeatAttribute.ONCE,
				RepeatAttribute.STOP
		};
		
		RotateAttribute[] rotateAttrib ={
				RotateAttribute.DEFAULT,
				RotateAttribute.TENDTOPLANE,
				RotateAttribute.CLOCKWISE,
				RotateAttribute.TENDAHEAD,
				RotateAttribute.TENDPARENTFACEON,
				RotateAttribute.SETPARENTFACEONANDCW
		};
	
		animeData.drawSize.x = getIntValue();
		animeData.drawSize.y = getIntValue();
		animeData.textureSheet = animationManager.enemyTex[getIntValue()];
		animeData.frameOffset = getIntValue();
		animeData.frameNumber = getIntValue();
		animeData.frameInterval = getIntValue();
		animeData.repeatAttribute = repeatAttrib[getIntValue()];
		animeData.rotateAction = rotateAttrib[getIntValue()];
		animeData.rotateOffset = getIntValue();
		animeData.angularVelocity = getDblValue();
	}
}
