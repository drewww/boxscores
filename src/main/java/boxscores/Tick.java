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
}
