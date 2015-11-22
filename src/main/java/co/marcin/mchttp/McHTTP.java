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

package co.marcin.mchttp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.util.IOUtils;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.ResourceExtractor;
import org.apache.commons.lang.StringUtils;

public class McHTTP {
	enum ContentType {
		PNG("image/png"),
		HTML("text/html"),
		PHP("text/html"),
		JPG("image/jpeg"),
		JPEG("image/jpeg"),
		GIF("image/gif"),
		BMP("image/bmp"),
		PDF("application/pdf"),
		YML("text/yaml"),
		ICO("image/x-icon"),
		ZIP("application/zip"),
		RAR("application/x-rar-compressed"),
		MP3(""),
		AVI("video/x-msvideo"),
		MP4("video/mp4"),
		WAV("audio/x-wav"),
		SWF("application/x-shockwave-flash"),
		TXT("text/plain"),
		TTF("application/x-font-ttf");

		private final String header;

		ContentType(String header) {
			this.header = header;
		}

		public String getHeader() {
			return header;
		}

		public static ContentType get(String str) {
			for(ContentType cT : values()) {
				if(cT.name().equalsIgnoreCase(str)) {
					return cT;
				}
			}

			return null;
		}
	}

	public static final String protocolVersion = "HTTP/1.1";
	public static final String HEADER_FIRST_OK = protocolVersion+" 200 OK";
	public static final String HEADER_FIRST_404 = protocolVersion+" 404 Not Found";

	private int port = 80;
	private boolean running = false;
	private Thread thread;
	private ServerSocket serverSocket;
	private File htmlDirectory = new File(NovaGuilds.getInstance().getDataFolder(), "/www");

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void prepareFiles() {
		if(!htmlDirectory.exists()) {
			ResourceExtractor extract = new ResourceExtractor(NovaGuilds.getInstance(), new File(NovaGuilds.getInstance().getDataFolder() + File.separator + "www"), "www", ".+");

			try {
				extract.extract();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
					LoggerUtils.info("Server started: " + serverSocket.getLocalSocketAddress().toString());

					SimpleDateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
					String dateString = date.format(new Date());
					System.out.println(dateString);

					if(!running) {
						this.stop();
					}

					while(Config.WWW_ENABLED.getBoolean() && running) {
						Socket clientSocket = serverSocket.accept();

						PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						dateString = date.format(new Date());

						char[] chars = new char[1024];
						in.read(chars);
						String inputString = new String(chars);
						String path = inputString.split(" ").length > 1 ? inputString.split(" ")[1] : "/";
						File targetFile;

						if(path.equals("/")) {
							path = "index.html";
						}

						targetFile = getHTMLFile(path);
						String[] split = StringUtils.split(path, ".");
						String ext = split[split.length-1];
						ContentType contentTypeEnum = ContentType.get(ext);
						String contentType = contentTypeEnum==null ? ContentType.TXT.getHeader() : contentTypeEnum.getHeader();

						String headerFirst = HEADER_FIRST_OK;
						if(!targetFile.exists()) {
							headerFirst = HEADER_FIRST_404;
							contentType = ContentType.TXT.getHeader();
						}


						String[] headers = new String[]{
								headerFirst,
								"Date: " + dateString + " GMT",
								"Server: HTTPServer 1.0",
//								"Set-Cookie: PSID=d6dd02e9957fb162d2385ca6f2829a73; path=/",
//								"Expires: -1",
//								"Allow: GET, HEAD",
//								"Cache-Control: no-store, no-cache, must-revalidate",
//								"Pragma: no-cache",
//								"Pragma: public",
								"Keep-Alive: timeout=5, max=100",
//								"Transfer-Encoding: chunked",
//								"Content-Type: application/xhtml+xml; charset=utf-8",
//								"Content-Type: text/www.html; charset=utf-8",
								"Content-Type: " + contentType
						};

						if(Config.WWW_VERBOSE.getBoolean()) LoggerUtils.info("Client: " + clientSocket.getRemoteSocketAddress() + " requested file " + path);

//						System.out.println();
//						System.out.println("Output: ");
						for(String str : headers) {
							out.println(str);
//				            System.out.println(str);
						}

						out.println();
//						System.out.println();

						if(headerFirst.equals(HEADER_FIRST_404)) {
							targetFile = getHTMLFile("404.html");
						}

						if(contentType.contains("text")) {
							String content = IOUtils.read(targetFile) + "\r\n";
							out.print(content);
//				            System.out.print(content);
						}
						else {
							byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(targetFile));
							clientSocket.getOutputStream().write(bytes);
						}

						out.println();
						clientSocket.close();
					}
				}
				catch(IOException e) {
					LoggerUtils.exception(e);
				}
			}
		};

		thread.start();
		running = true;
	}

	public void stop() {
		try {
			running = false;
			serverSocket.close();
		}
		catch(IOException e) {
			running = false;
			LoggerUtils.exception(e);
		}
	}

	private File getHTMLFile(String path) {
		return new File(htmlDirectory, path);
	}
}
