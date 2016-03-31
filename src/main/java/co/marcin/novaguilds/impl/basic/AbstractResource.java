package co.marcin.novaguilds.impl.basic;

import co.marcin.novaguilds.api.storage.Resource;

public abstract class AbstractResource implements Resource {
	private boolean added;
	private boolean changed;
	
	@Override
	public final boolean isAdded() {
		return added;
	}

	@Override
	public final void setAdded() {
		added = true;
	}

	@Override
	public final void setNotAdded() {
		added = false;
	}

	@Override
	public final void setChanged() {
		changed = true;
	}

	@Override
	public final void setUnchanged() {
		changed = false;
	}

	@Override
	public final boolean isChanged() {
		return changed;
	}
}
