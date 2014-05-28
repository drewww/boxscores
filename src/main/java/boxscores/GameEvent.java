package boxscores;

public class GameEvent {
	// being very lazy with all the public fields but whatever.
	public enum Type {TOTAL_GOLD, HERO_KILL, TOWER_KILL, TOWER_DENY, BARRACK_KILL};

	public Type type;
	public int value;
	public Team team;
	
	// this is potentially vestigial, but we'll include it for now.
	public Integer playerId;
	
	public GameEvent(GameEvent.Type type, Team team) {
		this.type = type;
		this.team = team;
	}
	
	public String toString() {
		return this.team + "." + this.type + " : " + this.value;
	}
}
