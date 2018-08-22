package takada.shooting.structure;

public class EventData {
	
	public enum EventCategory{ENEMYAPPEARANCE, BRIEFING, BOSSAPPEARANCE, PLAYBGM};
	
	public int scrollPoint;
	public EventCategory eventCategory;
	public int eventObjectID;
	
	public void initialize(){
		
		scrollPoint = -1;
		eventCategory = EventCategory.ENEMYAPPEARANCE;
		eventObjectID = -1;
	}

}
