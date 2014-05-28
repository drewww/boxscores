package boxscores;

public class GameEvent {
	// being very lazy with all the public fields but whatever.
	public String type;
	public Integer value;
	public Team team;
	
	// this is potentially vestigial, but we'll include it for now.
	public Integer playerId;
}
