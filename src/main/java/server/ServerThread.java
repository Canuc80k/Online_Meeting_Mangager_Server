package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {
	final Socket socket;
	 
	public ServerThread(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
        try {
        	DataInputStream dis = new DataInputStream(socket.getInputStream());
        	DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        	Thread client_handler_thread = new ClientHandler(socket, dis, dos);
        	client_handler_thread.start();
 
        } catch (Exception e) {
        	try {socket.close();} 
        	catch (Exception e1) {}
			e.printStackTrace();
        }
    }
}
