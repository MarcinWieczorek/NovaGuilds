package co.marcin.novaguilds.util;

import com.google.common.io.CharStreams;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class IOUtils {
	public static String inputStreamToString(InputStream inputStream) {
		try {
			return CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
		return null;
	}

	public static void saveInputStreamToFile(InputStream inputStream, File file) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);

			int read;
			byte[] bytes = new byte[1024];

			while((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

		}
		catch(IOException e) {
			LoggerUtils.exception(e);
		}
		finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
					LoggerUtils.exception(e);
				}
			}
			if(outputStream != null) {
				try {
					outputStream.close();
				}
				catch (IOException e) {
					LoggerUtils.exception(e);
				}

			}
		}
	}

	public static List<String> getFilesWithoutExtension(File directory) {
		List<String> list = new ArrayList<>();
		File[] filesList = directory.listFiles();

		if(filesList != null) {
			for(File file : filesList) {
				if(file.isFile()) {
					String name = file.getName();
					LoggerUtils.debug(name);
					if(org.apache.commons.lang.StringUtils.contains(name, '.')) {
						name = org.apache.commons.lang.StringUtils.split(name, '.')[0];
						list.add(name);
					}
				}
			}
		}

		return list;
	}

	public static String toString(InputStream in, String encoding) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len;

		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}

		return new String(baos.toByteArray(), encoding);
	}
}
