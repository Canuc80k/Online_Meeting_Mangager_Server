package meeting;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import database.Meeting_information_database;
import database.User_account_database;
import database.User_activity_in_meeting_database;
import general_function.FileTool;
import gsheet.GoogleDriveSnippets;

public class Meeting {
	private static final String MEETING_FOLDER_PATH = "src/main/resources/meeting";
	private static final String CURRENT_AVAILABLE_MEETING_ID_FILE_PATH = "src/main/resources/meeting/current_available_meeting_id";
	private static final String MEETING_SPECIFIED_DATA_FOLDER_PATH = "src/main/resources/meeting/meeting_specified_data/";
	
	private String current_available_meeting_id, meeting_creator_id, created_meeting_information;
	private String meeting_id_need_to_join, joiner_id;
	private String user_id_of_data_recieved, meeting_id_of_data_recieved, app_activity_received;
	private String meeting_id_need_to_out, account_id_need_to_out_meeting;
	
	public void general_init() throws Exception {
		if (!(new File(MEETING_FOLDER_PATH)).exists()) new File(MEETING_FOLDER_PATH).mkdirs();
		if (!(new File(CURRENT_AVAILABLE_MEETING_ID_FILE_PATH)).exists()) FileTool.write_file("", CURRENT_AVAILABLE_MEETING_ID_FILE_PATH);
		if (!(new File(MEETING_SPECIFIED_DATA_FOLDER_PATH)).exists()) new File(MEETING_SPECIFIED_DATA_FOLDER_PATH).mkdirs();
	
		this.current_available_meeting_id = this.meeting_creator_id = this.created_meeting_information = "";
		this.meeting_id_need_to_join = this.joiner_id = "";
		this.user_id_of_data_recieved = this.meeting_id_of_data_recieved = this.app_activity_received = "";
		this.meeting_id_need_to_out = this.account_id_need_to_out_meeting = "";
	}
	
	public synchronized String out_meeting(String user_data) throws Exception {
		general_init();
		out_meeting_init(user_data);
		String out_successfully = "OUT_SUCCESSFULLY";
		if (!User_account_database.out_meeting(meeting_id_need_to_out, account_id_need_to_out_meeting)) 
			out_successfully = "FAIL_TO_OUT_MEETING";
		return out_successfully;	
	}
	
	
	private synchronized void out_meeting_init(String user_data) {
		List<String> user_data_list = Arrays.asList(user_data.split("\n"));
		account_id_need_to_out_meeting = user_data_list.get(0).trim();
		meeting_id_need_to_out = user_data_list.get(1).trim();
	}

	public synchronized String receive_meeting_data(String meeting_data_recieved) throws Exception {
		general_init();
		receive_meeting_data_init(meeting_data_recieved);
		String spreadSheetID = GoogleDriveSnippets.getGoogleFilesByName(meeting_id_of_data_recieved).get(0).getId().trim();
		
		String receive_successfully = "SERVER_RECEIVE_JOINER_APP_ACTIVITY_SUCCESSFULLY";
		if (!User_activity_in_meeting_database.add_raw_data(spreadSheetID, user_id_of_data_recieved, app_activity_received)) 
			receive_successfully = "FAIL_TO_RECEIVE_JOINER_APP_ACTIVITY";
		return receive_successfully;
	}
	
	public synchronized void receive_meeting_data_init(String meeting_data_recieved) {
		List<String> meeting_data_recieved_list = Arrays.asList(meeting_data_recieved.split("\n"));
		user_id_of_data_recieved = meeting_data_recieved_list.get(0).trim();
		meeting_id_of_data_recieved = meeting_data_recieved_list.get(1).trim();
		for (int i = 2; i < meeting_data_recieved_list.size(); i ++)
			app_activity_received += meeting_data_recieved_list.get(i).trim() + '\n';
	}
	
	public synchronized String join_meeting(String joiner_meeting_information) throws Exception {
		general_init();
		join_meeting_init(joiner_meeting_information);
		String meeting_information = "";
		if (!User_activity_in_meeting_database.add_new_joiner_to_database(meeting_id_need_to_join, joiner_id)) 
			meeting_information = "FAIL_TO_JOIN_MEETING";
		if (!User_account_database.add_new_joined_meeting(meeting_id_need_to_join, joiner_id))
			meeting_information = "FAIL_TO_JOIN_MEETING";
		
		try {
			meeting_information = Meeting_information_database.get_meeting_info(meeting_id_need_to_join); 
		} catch(Exception e) {}
		
		return meeting_information;
	}
	
	private synchronized void join_meeting_init(String joiner_meeting_information) {
		List<String> joiner_meeting_information_list = Arrays.asList(joiner_meeting_information.split("\n"));
		meeting_id_need_to_join = joiner_meeting_information_list.get(0).trim();
		joiner_id = joiner_meeting_information_list.get(1).trim();
	}
	
	public synchronized String create_meeting(String created_meeting_data) throws Exception {	
		general_init();
		create_meeting_init(created_meeting_data);
		Meeting_information_database.add_new_meeting(created_meeting_information, current_available_meeting_id);
		User_activity_in_meeting_database.create_database(current_available_meeting_id);
		User_account_database.add_new_created_meeting(current_available_meeting_id, meeting_creator_id);
		
		return current_available_meeting_id ;
	}
	
	private synchronized void create_meeting_init(String created_meeting_data) throws Exception {
		List<String> created_meeting_data_list = Arrays.asList(created_meeting_data.split("\n"));
		meeting_creator_id = created_meeting_data_list.get(0).trim();
		created_meeting_information = created_meeting_data_list.get(1).trim();
		for (int i = 2; i < created_meeting_data_list.size(); i ++) 
			created_meeting_information += created_meeting_data_list.get(i).trim() + '\n';
		
		current_available_meeting_id = FileTool.read_file(CURRENT_AVAILABLE_MEETING_ID_FILE_PATH).trim();
		if (current_available_meeting_id == null || current_available_meeting_id.equals("")) current_available_meeting_id = "0000000000";
		String new_id = Meeting_ID.get_next_meeting_id(current_available_meeting_id);
		FileTool.write_file(new_id, CURRENT_AVAILABLE_MEETING_ID_FILE_PATH);
	}
}
