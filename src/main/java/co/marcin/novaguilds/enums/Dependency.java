package co.marcin.novaguilds.enums;

import co.marcin.novaguilds.manager.DependencyManager;

public enum Dependency {
	VAULT("Vault", true),
	NORTHTAB("NorthTab", false),
	VANISHNOPACKET("VanishNoPacket", false),
	ESSENTIALS("Essentials", false),
	BOSSBARAPI("BossBarAPI", false),
	BARAPI("BarAPI", false, new DependencyManager.BarAPIVersionCompatilibityCheck()),
	HOLOGRAPHICDISPLAYS("HolographicDisplays", false, new DependencyManager.HolographicDisplaysAPIChecker());

	private final String name;
	private final boolean hardDependency;
	private DependencyManager.RunnableWithException[] additionalTasks = new DependencyManager.RunnableWithException[0];

	Dependency(String name, boolean hardDependency) {
		this.name = name;
		this.hardDependency = hardDependency;
	}

	Dependency(String name, boolean hardDependency, DependencyManager.RunnableWithException... additionalTasks) {
		this(name, hardDependency);
		this.additionalTasks = additionalTasks;
	}

	public String getName() {
		return name;
	}

	public boolean isHardDependency() {
		return hardDependency;
	}

	public boolean hasAdditionalTasks() {
		return additionalTasks.length > 0;
	}

	public DependencyManager.RunnableWithException[] getAdditionalTasks() {
		return additionalTasks;
	}
}
