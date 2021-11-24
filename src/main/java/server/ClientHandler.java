package server;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import account.Account;
import account.Login;
import account.Register;
import database.Meeting_information_database;
import database.User_account_database;
import database.User_activity_in_meeting_database;
import datapack.Datapack_receiver;
import gsheet.GoogleDriveSnippets;
import gsheet.SpreadSheetSnippets;
import meeting.Meeting;
import screenshot.Screenshot_receiver;

public class ClientHandler extends Thread {
	final DataInputStream dis;
	final DataOutputStream dos;
	private Socket socket;

	private static String received;
    private static String toreturn;
    
	public ClientHandler(Socket socket, DataInputStream dis, DataOutputStream dos) {
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
	}

	public synchronized void run() {
		while (true) {
			try {
				if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
				if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.createService();

				received = dis.readUTF();
				handle_client_input(socket, received);
				dos.writeUTF(toreturn);
			} catch (Exception e) {}
		}
	}
	
	public static synchronized BufferedImage receive_image_file(Socket socket) {
		BufferedImage image = null; 
		try {
			image = ImageIO.read(ImageIO.createImageInputStream(socket.getInputStream()));
		} catch (Exception e) {e.printStackTrace();}
		
		return image;
	}

	/*
	 *	@param data_received format: 
	 * 		Line 1        => Action like "LOGIN", "REGIST", ...
	 *   	Line 2 to end => Specified information of the action
	 */
	public static synchronized void handle_client_input(Socket socket, String data_received) throws Exception {
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
		
		if (client_request_action.equals("GET_SPREADSHEET_ID")) {
			toreturn = User_activity_in_meeting_database.get_meeting_spreadSheetID(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("GET_USER_ACTIVITY_RAW_DATA")) {
			toreturn = new Meeting().get_user_activity_raw_data(client_request_specified_data);
			return;
		}

		if (client_request_action.equals("SEND_DATAPACK")) {
			toreturn = new Datapack_receiver().save_datapack_to_cache(client_request_specified_data);
			return;
		}
		
		if (client_request_action.equals("SEND_SCREENSHOT")) {
			String response_to_client = "RECEIVE_SCREENSHOT_SUCCESFULLY";

			String folder_path_to_save_screenshot = Screenshot_receiver.get_specified_screenshot_folder(client_request_specified_data);
			String file_index = String.valueOf(new File(folder_path_to_save_screenshot).listFiles().length + 1);
			String screenshot_file_paht = folder_path_to_save_screenshot + '/' + file_index + ".png";
			BufferedImage image = receive_image_file(socket);
			if (image == null) response_to_client = "FAIL_TO_RECEIVE_SCREENSHOT";
			else ImageIO.write(image, "png", new File(screenshot_file_paht));

			toreturn = response_to_client;
			return;
		}
	}
	
	public static synchronized List<String> get_client_request_data(String data_received) {
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
