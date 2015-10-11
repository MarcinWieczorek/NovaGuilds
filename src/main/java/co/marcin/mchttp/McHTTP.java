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

public class McHTTP {
	public static final String protocolVersion = "HTTP/1.1";
	public static final String HEADER_FIRST_OK = protocolVersion+" 200 OK";
	public static final String HEADER_FIRST_404 = protocolVersion+" 404 Not Found";

	private int port = 80;
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
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
					LoggerUtils.info("Server started: " + serverSocket.getLocalSocketAddress().toString());

					SimpleDateFormat date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
					String dateString = date.format(new Date());
					System.out.println(dateString);

					while(Config.WWW_ENABLED.getBoolean()) {
						Socket clientSocket = serverSocket.accept();

						PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						dateString = date.format(new Date());

						String path;
//						if(DEBUG) System.out.println();
//						if(DEBUG) System.out.println("Input: ");

						char[] chars = new char[1024];
						in.read(chars);
						String inputString = new String(chars);
//			            if(DEBUG) System.out.println(inputString);
						path = inputString.split(" ").length > 1 ? inputString.split(" ")[1] : "/";
						File targetFile;

						if(path.equals("/")) {
							targetFile = getHTMLFile("index.html");
						}
						else {
							targetFile = getHTMLFile(path);
						}

						String contentType = "text/html";

						if(path.toLowerCase().endsWith(".ico")) {
							contentType = "image/x-icon";
						}
						else if(path.toLowerCase().endsWith(".png")) {
							contentType = "image/png";
						}

						String headerFirst = HEADER_FIRST_OK;
						if(!targetFile.exists()) {
							headerFirst = HEADER_FIRST_404;
							contentType = "text/html";
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

						LoggerUtils.info("Client: " + clientSocket.getRemoteSocketAddress() + " requested file " + path);

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
	}

	private File getHTMLFile(String path) {
		return new File(htmlDirectory, path);
	}
}
