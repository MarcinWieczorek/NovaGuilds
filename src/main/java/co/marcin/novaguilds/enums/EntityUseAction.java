package co.marcin.novaguilds.enums;

public enum EntityUseAction {
	ATTACK(0),
	INTERACT(1),
	INTERACT_AT(2);

	private final int id;

	EntityUseAction(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
