package meeting;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import general_function.FileTool;
import gsheet.SpreadSheetSnippets;

public class Meeting {
	private static final String MEETING_FOLDER_PATH = "src/main/resources/meeting";
	private static final String CURRENT_AVAILABLE_MEETING_ID_FILE_PATH = "src/main/resources/meeting/current_available_meeting_id";
	private static final String MEETING_SPECIFIED_DATA_FOLDER_PATH = "src/main/resources/meeting/meeting_specified_data/";
	private static final String JOINER_APP_ACTIVITY_SPLIT_SIGNAL = "\n~!~#@~\n";
	
	private String current_available_meeting_id;
	private String meeting_id_need_to_join, joiner_id;
	private String user_id_of_data_recieved, meeting_id_of_data_recieved, app_activity_received;
	private String changed_meeting_id, new_meeting_infomation;
	
	public void general_init() throws Exception {
		if (!(new File(MEETING_FOLDER_PATH)).exists()) new File(MEETING_FOLDER_PATH).mkdirs();
		if (!(new File(CURRENT_AVAILABLE_MEETING_ID_FILE_PATH)).exists()) FileTool.write_file("", CURRENT_AVAILABLE_MEETING_ID_FILE_PATH);
		if (!(new File(MEETING_SPECIFIED_DATA_FOLDER_PATH)).exists()) new File(MEETING_SPECIFIED_DATA_FOLDER_PATH).mkdirs();
	
		this.current_available_meeting_id = "";
		this.meeting_id_need_to_join = this.joiner_id = "";
		this.user_id_of_data_recieved = this.meeting_id_of_data_recieved = this.app_activity_received = "";
		this.changed_meeting_id = this.new_meeting_infomation = "";
	}
	
	public String change_meeting_infomation(String meeting_data) throws Exception {
		general_init();
		change_meeting_infomation_init(meeting_data);

		String change_successfully = "true"; 
		try {
			FileTool.write_file(new_meeting_infomation, MEETING_SPECIFIED_DATA_FOLDER_PATH + changed_meeting_id + '/' + "meeting_information");
		} catch (Exception e) {
			change_successfully = "false";
		}
		
		return change_successfully;	
	}
	
	public void change_meeting_infomation_init(String meeting_data) {
		List<String> meeting_data_list = Arrays.asList(meeting_data.split("\n"));
		changed_meeting_id = meeting_data_list.get(0).trim();
		new_meeting_infomation = meeting_data_list.get(1) + '\n';
		for (int i = 2; i < meeting_data_list.size(); i ++)
			new_meeting_infomation += meeting_data_list.get(i).trim() + '\n';
	}
	
	public String send_meeting_data(String meeting_id) throws Exception {
		general_init();
		meeting_id = meeting_id.trim();
		String meeting_data = "";
		
		try {
			String meeting_joiner_app_activity_data_file_path = MEETING_SPECIFIED_DATA_FOLDER_PATH + meeting_id + "/joiner_app_activity/";
			File[] files = new File(meeting_joiner_app_activity_data_file_path).listFiles();

			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				String joiner_id = file.getName();
				String joiner_app_activity_data = "";
				joiner_app_activity_data = FileTool.read_file(meeting_joiner_app_activity_data_file_path + joiner_id);

				if (i + 1 >= files.length) {
					meeting_data += joiner_id + '\n' + joiner_app_activity_data;
				} else {
					meeting_data += joiner_id + '\n' + joiner_app_activity_data + JOINER_APP_ACTIVITY_SPLIT_SIGNAL;
				}
			}
		} catch (Exception e) {
			meeting_data = "FAIL_TO_GET";
		}
		return meeting_data;
	}
	
	public String receive_meeting_data(String meeting_data_recieved) throws Exception {
		general_init();
		String receive_meeting_data_successfully = "true";
		receive_meeting_data_init(meeting_data_recieved);
		
		File all_joiner_app_activity_folder = new File(MEETING_SPECIFIED_DATA_FOLDER_PATH + meeting_id_of_data_recieved + "/joiner_app_activity/");
		if (!all_joiner_app_activity_folder.exists()) all_joiner_app_activity_folder.mkdirs();
		
		FileTool.write_file(app_activity_received, all_joiner_app_activity_folder.getPath() + '/' + user_id_of_data_recieved);
		return receive_meeting_data_successfully;
	}
	
	public void receive_meeting_data_init(String meeting_data_recieved) {
		List<String> meeting_data_recieved_list = Arrays.asList(meeting_data_recieved.split("\n"));
		user_id_of_data_recieved = meeting_data_recieved_list.get(0);
		meeting_id_of_data_recieved = meeting_data_recieved_list.get(1);
		for (int i = 2; i < meeting_data_recieved_list.size(); i ++)
			app_activity_received += meeting_data_recieved_list.get(i) + '\n';
	}
	
	public String join_meeting(String joiner_meeting_information) throws Exception {
		general_init();
		join_meeting_init(joiner_meeting_information);
		String meeting_data = null;
		
		try {
			File[] files = new File(MEETING_SPECIFIED_DATA_FOLDER_PATH).listFiles();
			for (File file : files) {
				if (file.getName().trim().equals(meeting_id_need_to_join.trim())) {
					meeting_data = FileTool.read_file(MEETING_SPECIFIED_DATA_FOLDER_PATH
							+ meeting_id_need_to_join.trim() + "/meeting_information");
					add_joiner_id_to_meeting(meeting_id_need_to_join, joiner_id);
					break;
				}
			}
		} catch (Exception e) {}

		if (meeting_data == null) meeting_data = "FAIL_TO_JOIN_MEETING";
		return meeting_data;
	}
	
	public void join_meeting_init(String joiner_meeting_information) {
		List<String> joiner_meeting_information_list = Arrays.asList(joiner_meeting_information.split("\n"));
		meeting_id_need_to_join = joiner_meeting_information_list.get(0).trim();
		joiner_id = joiner_meeting_information_list.get(1).trim();
	}
	
	public synchronized void add_joiner_id_to_meeting(String meeting_id, String joiner_id) throws Exception {
		String joiner_id_data = "";
		String meeting_joiner_id_file_path = MEETING_SPECIFIED_DATA_FOLDER_PATH + meeting_id + "/joiner_id";
	    File meeting_joiner_id_file = new File(meeting_joiner_id_file_path);
		if (meeting_joiner_id_file.exists()) {
			joiner_id_data = FileTool.read_file(meeting_joiner_id_file_path).trim();
			joiner_id_data += '\n' + joiner_id;
		} else {
			FileTool.write_file("", meeting_joiner_id_file_path);
			
			joiner_id_data = "";
			joiner_id_data += joiner_id;
		}
	    
	    FileTool.write_file(joiner_id_data, meeting_joiner_id_file_path);
	}
	
	public String create_meeting(String meeting_information) throws Exception {	
		general_init();
		current_available_meeting_id = FileTool.read_file(CURRENT_AVAILABLE_MEETING_ID_FILE_PATH).trim();
		if (current_available_meeting_id == null || current_available_meeting_id.equals("")) current_available_meeting_id = "0000000000";
		String new_id = Meeting_ID.get_next_meeting_id(current_available_meeting_id);
		FileTool.write_file(new_id, CURRENT_AVAILABLE_MEETING_ID_FILE_PATH);
		
		List<Object> append_row = new ArrayList<Object>();
		List<String> meeting_information_list = Arrays.asList(meeting_information.split("\n"));
		append_row.add(current_available_meeting_id);
		append_row.add(current_available_meeting_id);
		for (int i = 0; i < meeting_information_list.size(); i ++)
			append_row.add(meeting_information_list.get(i));
		List<List<Object>> values = new ArrayList<List<Object>>();
		values.add(append_row);
		
		try {
			if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
			String spreadSheetID = SpreadSheetSnippets.get_meeting_information_database_spread_sheet_id();
			SpreadSheetSnippets.appendValues(spreadSheetID, "Meeting Information Database", "RAW", values);
		} catch (Exception e) {e.printStackTrace();}
		
		return current_available_meeting_id ;
	}
}
