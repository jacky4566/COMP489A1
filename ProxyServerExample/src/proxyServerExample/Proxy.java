package proxyServerExample;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Logger;

//Helpful links
//See https://stackoverflow.com/questions/9357585/creating-a-java-proxy-server-that-accepts-https
//see http://www.jcgonzalez.com/java-simple-proxy-socket-server-examples#4

public class Proxy implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

	private int proxyPort;
	private ServerSocket serverSocket;

	public Proxy(int port) {
		proxyPort = port;
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			LOGGER.info("Proxy Start");
			serverSocket = new ServerSocket(proxyPort);
			System.out.println("Proxy server is listening on port " + proxyPort);

			try {
				while (true) {
					Socket clientSocket = serverSocket.accept();
					Thread thread = new Thread(() -> handleClientRequest(clientSocket));
					thread.start();
				}
			} catch (IOException e) {
				e.printStackTrace(); // TODO: implement catch
			}

		} catch (java.net.SocketException e) {
			LOGGER.warning("SocketException");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void handleClientRequest(Socket clientSocket) {
	    try (Socket socket = clientSocket) {
	        InputStream clientInput = clientSocket.getInputStream();
	        OutputStream clientOutput = clientSocket.getOutputStream();

            // Read client request
            byte[] requestBuffer = new byte[1024];
            int bytesRead = clientInput.read(requestBuffer);
            String request = new String(requestBuffer, 0, bytesRead);

            // Extract target host and port from the request
            String[] requestLines = request.split("\r\n");
            String[] requestLine = requestLines[0].split(" ");

            // Connect to the target server
            String targetURL = requestLine[1];
            HttpURLConnection connection = (HttpURLConnection) new URL(targetURL).openConnection();
            connection.setRequestMethod(requestLine[0]);
            connection.setDoOutput(true);
            connection.getOutputStream().write(requestBuffer, 0, bytesRead);

            // Forward the response from the target server to the client
            InputStream targetInput = connection.getInputStream();
            byte[] responseBuffer = new byte[1024];
            int targetBytesRead;
            while ((targetBytesRead = targetInput.read(responseBuffer)) != -1) {
                clientOutput.write(responseBuffer, 0, targetBytesRead);
            }

            // Close the sockets
            clientSocket.close();
            targetInput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
