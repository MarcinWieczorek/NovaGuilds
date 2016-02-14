/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.runnable.RunnableAutoSave;
import co.marcin.novaguilds.runnable.RunnableInactiveCleaner;
import co.marcin.novaguilds.runnable.RunnableLiveRegeneration;
import co.marcin.novaguilds.runnable.RunnableRefreshHolograms;
import co.marcin.novaguilds.runnable.RunnableRefreshTablist;
import co.marcin.novaguilds.util.LoggerUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TaskManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();
	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	private final Map<Task, ScheduledFuture<?>> taskRunnableMap = new HashMap<>();

	public enum Task {
		LIVEREGENERATION(RunnableLiveRegeneration.class, Config.LIVEREGENERATION_TASKINTERVAL),
		CLEANUP(RunnableInactiveCleaner.class, Config.CLEANUP_STARTUPDELAY, Config.CLEANUP_INTERVAL),
		HOLOGRAM_REFRESH(RunnableRefreshHolograms.class, Config.HOLOGRAPHICDISPLAYS_REFRESH),
		AUTOSAVE(RunnableAutoSave.class, Config.SAVEINTERVAL),
		TABLIST_REFRESH(RunnableRefreshTablist.class, Config.TABLIST_REFRESH);

		private final Config start;
		private final Config interval;
		final Class clazz;

		Task(Class<? extends Runnable> clazz, Config both) {
			this.clazz = clazz;
			start = both;
			interval = both;
		}

		Task(Class<? extends Runnable> clazz, Config start, Config interval) {
			this.clazz = clazz;
			this.start = start;
			this.interval = interval;
		}

		public Class getClazz() {
			return clazz;
		}

		public long getStart() {
			return start.getSeconds();
		}

		public long getInterval() {
			return interval.getSeconds();
		}
	}

	public void startTask(Task task) {
		try {
			Runnable taskInstance = (Runnable) task.getClazz().newInstance();
			ScheduledFuture<?> future = worker.scheduleAtFixedRate(taskInstance, task.getStart(), task.getInterval(), TimeUnit.SECONDS);
			taskRunnableMap.put(task, future);
		}
		catch(InstantiationException | IllegalAccessException e) {
			LoggerUtils.exception(e);
		}
	}

	public void stopTask(Task task) {
		taskRunnableMap.get(task).cancel(true);
	}
}
