package server;

import java.net.*;

public class Server {
	private static Socket socket;
	public static int port = 9000;

	public static void start() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				socket = serverSocket.accept();
				new ServerThread(socket).start();
			}
			
		} catch (Exception e) {
			socket.close();
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Server.start();
	}
}	