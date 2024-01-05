package proxyServerExample;

import java.util.logging.Logger;

/*
 * Jackson Wiebe
 * 3519635
 * 05/01/2024
 * 
 * Main APP
 * 
 * Main start loop for proxy demo. 
 * Creates an instance of the proxy server, Web server, and HTTP client.
 * 
 */

public class MainApp {
	private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

	final static String PROXPORT = "8080";
	final static String PROXADDR = "localhost";

	public static void main(String[] args) {
		// Set a custom format for the Logger
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$-7s] [%2$s] %5$s%6$s%n");
		LOGGER.info("Main start");

		// Start Proxy Server in its own thread
		Proxy proxyInstance = new Proxy(Integer.parseInt(MainApp.PROXPORT));
		Thread proxyThread = new Thread(proxyInstance);
		proxyThread.start();

		// Run the client app
		ClientApp.run();

		// Shutdown
		try {
			proxyInstance.stop();
			proxyThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.info("Done");
	}
}
