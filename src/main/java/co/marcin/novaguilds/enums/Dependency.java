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
	private DependencyManager.RunnableWithException additionalTask;

	Dependency(String name, boolean hardDependency) {
		this.name = name;
		this.hardDependency = hardDependency;
	}

	Dependency(String name, boolean hardDependency, DependencyManager.RunnableWithException additionalTask) {
		this(name, hardDependency);
		this.additionalTask = additionalTask;
	}

	public String getName() {
		return name;
	}

	public boolean isHardDependency() {
		return hardDependency;
	}

	public boolean hasAdditionalTask() {
		return additionalTask != null;
	}

	public void runAdditionalTask() throws Exception {
		additionalTask.run();
	}
}
