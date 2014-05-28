package boxscores;

import java.util.ArrayList;
import java.util.List;

import boxscores.GameEvent.Type;

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
		String out = time + ",";
		
		for(GameEvent e : this.events) {
			out += e.toString() + ",";
		}

		return out;
	}
	
	public boolean hasEventType(Type t) {
		if(this.events.size() > 0) {
			for(GameEvent e : this.events) {
				if(e.type==t) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	public List<GameEvent> getEventType(Type t) {
		List<GameEvent> selectedEvents = new ArrayList<GameEvent>();
		
		for(GameEvent e : this.events) {
			if(e.type==t) {
				selectedEvents.add(e);
			}
		}
		
		return selectedEvents;
	}
}
