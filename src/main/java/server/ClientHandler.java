package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import account.Account;
import account.Login;
import account.Register;
import database.Meeting_information_database;
import database.User_account_database;
import gsheet.GoogleDriveSnippets;
import gsheet.SpreadSheetSnippets;
import meeting.Meeting;

public class ClientHandler extends Thread {
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket socket;

	private static String received;
    private static String toreturn;
    
	public ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos) {
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
	}

	public void run() {
		while (true) {
			try {
				if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
				if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.createService();
				
				received = dis.readUTF();
				handle_client_input(received);
				dos.writeUTF(toreturn);
			} catch (Exception e) {}
		}
	}
	
	/*
	 *	@param data_received format: 
	 * 		Line 1        => Action like "LOGIN", "REGIST", ...
	 *   	Line 2 to end => Specified information of the action
	 */
	public static void handle_client_input(String data_received) throws Exception {
		List<String> client_request_data_list = get_client_request_data(data_received);
		String client_request_action = client_request_data_list.get(0).trim();
		String client_request_specified_data = client_request_data_list.get(1).trim();
		
		if (client_request_action.equals("LOGIN")) {
			toreturn = Login.login(client_request_specified_data);
			return;
		}

		if (client_request_action.equals("REGISTER")) {
			boolean register_successful = Register.register(client_request_specified_data);
			toreturn = (register_successful) ? ("true") : ("false");
			return;
		}
		
		if (client_request_action.equals("CREATE_MEETING")) {
			toreturn = new Meeting().create_meeting(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("JOIN_MEETING")) {
			toreturn = new Meeting().join_meeting(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("SEND_MEETING_DATA")) {
			toreturn = new Meeting().receive_meeting_data(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("CHANGE_ACCOUNT_INFO")) {
			toreturn = Account.set_account_information(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("GET_ACCOUNT_INFO")) {
			toreturn = Account.get_account_information(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("GET_JOINED_MEETINGS")) {
			toreturn = User_account_database.get_joined_meetings(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("GET_MEETING_INFO")) {
			toreturn = Meeting_information_database.get_meeting_info(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("GET_CREATED_MEETINGS")) {
			toreturn = User_account_database.get_created_meetings(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("STOP_MEETING")) {
			toreturn = Meeting_information_database.set_meeting_state(client_request_specified_data, "OFF");
			return;
		}
	
		if (client_request_action.equals("START_MEETING")) {
			toreturn = Meeting_information_database.set_meeting_state(client_request_specified_data, "ON");
			return;
		}
		
		if (client_request_action.equals("OUT_MEETING")) {
			toreturn = new Meeting().out_meeting(client_request_specified_data);
			return;
		}
	}
	
	public static List<String> get_client_request_data(String data_received) {
		List<String> data_received_list = Arrays.asList(data_received.split("\n"));
		List<String> client_request_data_list = new ArrayList<String>();
		
		String client_request_action = "";
		String client_request_specified_data = "";
		
		client_request_action = data_received_list.get(0);
		for (int i = 1; i < data_received_list.size(); i ++) {
			client_request_specified_data += data_received_list.get(i) + '\n';
		}
		
		client_request_data_list.add(client_request_action);
		client_request_data_list.add(client_request_specified_data);
		return client_request_data_list;
	}
}
