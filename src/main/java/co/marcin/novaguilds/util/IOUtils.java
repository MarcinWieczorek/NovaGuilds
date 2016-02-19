/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
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

package co.marcin.novaguilds.util;

import com.google.common.io.CharStreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
				catch(IOException e) {
					LoggerUtils.exception(e);
				}
			}
			if(outputStream != null) {
				try {
					outputStream.close();
				}
				catch(IOException e) {
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
					if(org.apache.commons.lang.StringUtils.contains(name, '.')) {
						name = org.apache.commons.lang.StringUtils.split(name, '.')[0];
						list.add(name);
					}
				}
			}
		}

		return list;
	}

	public static String toString(InputStream in, String encoding) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len;

		while((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}

		return new String(baos.toByteArray(), encoding);
	}

	public static String read(File file) {
		try {
			return inputStreamToString(new FileInputStream(file));
		}
		catch(FileNotFoundException e) {
			LoggerUtils.exception(e);
			return null;
		}
	}

	public static void write(File file, String string) {
		try(PrintWriter out = new PrintWriter(file)) {
			out.println(string);
		}
		catch(FileNotFoundException e) {
			LoggerUtils.exception(e);
		}
	}
}
