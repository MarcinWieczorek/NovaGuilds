package co.marcin.novaguilds.impl.storage.managers.file;

import co.marcin.novaguilds.api.storage.Resource;
import co.marcin.novaguilds.api.storage.Storage;
import co.marcin.novaguilds.impl.storage.AbstractFileStorage;
import co.marcin.novaguilds.impl.storage.managers.AbstractResourceManager;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileResourceManager<T extends Resource> extends AbstractResourceManager<T> {
	private final File directory;

	/**
	 * The constructor
	 *
	 * @param storage       the storage
	 * @param clazz         manager type class
	 * @param directoryPath resource directory path
	 */
	protected AbstractFileResourceManager(Storage storage, Class clazz, String directoryPath) {
		super(storage, clazz);
		directory = new File(getStorage().getDirectory(), directoryPath);
	}

	@Override
	protected AbstractFileStorage getStorage() {
		if(!(super.getStorage() instanceof AbstractFileStorage)) {
			throw new IllegalArgumentException("Invalid storage type");
		}

		return (AbstractFileStorage) super.getStorage();
	}

	@Override
	public void add(T t) {
		try {
			createFileIfNotExists(getFile(t));
			t.setAdded();
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
	}

	/**
	 * Gets resource directory
	 *
	 * @return the directory
	 */
	protected File getDirectory() {
		return directory;
	}

	/**
	 * Gets resource's file
	 *
	 * @param t instance
	 * @return the file
	 */
	public abstract File getFile(T t);

	/**
	 * Gets all stored files
	 *
	 * @return list of files
	 */
	protected List<File> getFiles() {
		File[] files = getDirectory().listFiles();
		List<File> list = new ArrayList<>();

		if(files != null) {
			list.addAll(Arrays.asList(files));
		}

		return list;
	}

	/**
	 * Creates file if doesn't exist
	 *
	 * @param file the file
	 * @throws IOException if failed
	 */
	private void createFileIfNotExists(File file) throws IOException {
		if(!file.exists()) {
			if(!file.createNewFile()) {
				throw new IOException("File creating failed (" + file.getPath() + ")");
			}
		}
	}

	/**
	 * Trims extension from file's name
	 *
	 * @param file the file
	 * @return trimmed name
	 */
	protected final String trimExtension(File file) {
		return StringUtils.substring(file.getName(), 0, StringUtils.lastIndexOf(file.getName(), '.'));
	}
}
