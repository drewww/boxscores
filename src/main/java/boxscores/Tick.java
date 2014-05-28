package boxscores;

import java.util.ArrayList;
import java.util.List;

public class Tick {
	public Float time;
	public List<GameEvent> events;
	
	public Tick(float time) {
		this.time = time;
		this.events = new ArrayList<GameEvent>();
	}
	
	public void addEvent(GameEvent e) {
		this.events.add(e);
	}
	
	public String toString() {
		return time + ": " + "[" + this.events.toString() + "]\n";
	}
	
	public boolean hasGoldData() {
		if(this.events.size() > 0 && this.events.get(0).type==GameEvent.Type.TOTAL_GOLD) {
			return true;
		} else {
			return false;
		}
	}
	
	public List<GameEvent> getGoldData() {
		List<GameEvent> goldEvents = new ArrayList<GameEvent>();
		
		for(GameEvent e : this.events) {
			if(e.type==GameEvent.Type.TOTAL_GOLD) {
				goldEvents.add(e);
			}
		}
		
		return goldEvents;
	}
}
