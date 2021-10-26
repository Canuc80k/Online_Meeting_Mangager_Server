package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gsheet.SpreadSheetSnippets;

public class User_account_database {
	public enum USER_ACCOUNT_DATABASE {
		INDEX(0),
		USER_NAME(1),
		PASSWORD(2),
		FULLNAME(3),
		PHONE(4),
		CITY(5),
		DISTRICT(6),
		SCHOOL(7),
		BLOCK(8),
		CLASS(9),
		BORN_DATE(10),
		JOINED_MEETING(11),
		CREATED_MEETING(12);
		
		private int index;
		
		USER_ACCOUNT_DATABASE(int new_index) {
			this.index = new_index;
		}

		public int get_index() {
			return index;
		}
	}
	
	public static synchronized boolean out_meeting(String meeting_id_need_to_out, String account_id_need_to_out_meeting) {
		boolean add_successfully = false;
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
			String all_joined_meetings = "";
			int joiner_row_index = -1;
			
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "USER ACCOUNT DATABASE").getValues();
			for (int i = 0; i < values.size(); i ++) {
				if (values.get(i).get(USER_ACCOUNT_DATABASE.INDEX.get_index()).toString().trim().equals(account_id_need_to_out_meeting)) {
					joiner_row_index = i + 1;
					try {
						all_joined_meetings = values.get(i).get(USER_ACCOUNT_DATABASE.JOINED_MEETING.get_index()).toString().trim();
						all_joined_meetings += '\n';
					} catch(Exception e) {}
					break;
				}
			}
			
			List<List<Object>> new_values = new ArrayList<List<Object>>();
			List<Object> cell = new ArrayList<Object>();
			String new_joined_meetings = "";
			List<String> all_joined_meetings_list = Arrays.asList(all_joined_meetings.split("\n"));
			for (int i = 0; i < all_joined_meetings_list.size(); i ++) 
				if (!all_joined_meetings_list.get(i).trim().equals(meeting_id_need_to_out)) 
					new_joined_meetings += all_joined_meetings_list.get(i).trim() + '\n';
					
			cell.add(new_joined_meetings.trim());
			new_values.add(cell);
			char cell_column = (char)('A' + USER_ACCOUNT_DATABASE.JOINED_MEETING.get_index());
			String cell_row = String.valueOf(joiner_row_index);
			String cell_location = cell_column + cell_row;
			String range = "USER ACCOUNT DATABASE!" + cell_location + ":" + cell_location;
			SpreadSheetSnippets.batchUpdateValues(spreadSheetID, range, "RAW", new_values);
			add_successfully = true;
		} catch(Exception e) {}
		
		return add_successfully;
	}
	
	public static synchronized boolean add_new_created_meeting(String meeting_id, String creator_id) {
		boolean add_successfully = false;
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
			String all_created_meetings = "";
			int creator_row_index = -1;
			
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "USER ACCOUNT DATABASE").getValues();
			for (int i = 0; i < values.size(); i ++) {
				if (values.get(i).get(USER_ACCOUNT_DATABASE.INDEX.get_index()).toString().trim().equals(creator_id)) {
					creator_row_index = i + 1;
					try {
						all_created_meetings = values.get(i).get(USER_ACCOUNT_DATABASE.CREATED_MEETING.get_index()).toString().trim();
						all_created_meetings += '\n';
					} catch(Exception e) {}
					break;
				}
			}
			
			List<List<Object>> new_values = new ArrayList<List<Object>>();
			List<Object> cell = new ArrayList<Object>();
			cell.add(all_created_meetings + meeting_id);
			new_values.add(cell);
			char cell_column = (char)('A' + USER_ACCOUNT_DATABASE.CREATED_MEETING.get_index());
			String cell_row = String.valueOf(creator_row_index);
			String cell_location = cell_column + cell_row;
			String range = "USER ACCOUNT DATABASE!" + cell_location + ":" + cell_location;
			SpreadSheetSnippets.batchUpdateValues(spreadSheetID, range, "RAW", new_values);
			add_successfully = true;
		} catch(Exception e) {}
		
		return add_successfully;
	}

	public static synchronized boolean add_new_joined_meeting(String meeting_id_need_to_join, String joiner_id) {
		boolean add_successfully = false;
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
			String all_joined_meetings = "";
			int joiner_row_index = -1;
			
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "USER ACCOUNT DATABASE").getValues();
			for (int i = 0; i < values.size(); i ++) {
				if (values.get(i).get(USER_ACCOUNT_DATABASE.INDEX.get_index()).toString().trim().equals(joiner_id)) {
					joiner_row_index = i + 1;
					try {
						all_joined_meetings = values.get(i).get(USER_ACCOUNT_DATABASE.JOINED_MEETING.get_index()).toString().trim();
						all_joined_meetings += '\n';
					} catch(Exception e) {}
					break;
				}
			}
			
			List<List<Object>> new_values = new ArrayList<List<Object>>();
			List<Object> cell = new ArrayList<Object>();
			cell.add(all_joined_meetings + meeting_id_need_to_join);
			new_values.add(cell);
			char cell_column = (char)('A' + USER_ACCOUNT_DATABASE.JOINED_MEETING.get_index());
			String cell_row = String.valueOf(joiner_row_index);
			String cell_location = cell_column + cell_row;
			String range = "USER ACCOUNT DATABASE!" + cell_location + ":" + cell_location;
			SpreadSheetSnippets.batchUpdateValues(spreadSheetID, range, "RAW", new_values);
			add_successfully = true;
		} catch(Exception e) {}
		
		return add_successfully;
	}

	public static synchronized String get_joined_meetings(String account_id) {
		String joined_meetings = "";
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "USER ACCOUNT DATABASE").getValues();
			int joiner_row_index = get_user_row_by_account_index(account_id, values);
			
			char cell_column = (char)('A' + USER_ACCOUNT_DATABASE.JOINED_MEETING.get_index());
			String cell_row = String.valueOf(joiner_row_index);
			String cell_location = cell_column + cell_row;
			String range = "USER ACCOUNT DATABASE!" + cell_location + ":" + cell_location;
			List<List<Object>> joined_meetings_values = SpreadSheetSnippets.getValues(spreadSheetID, range).getValues();
			
			for (int i = 0; i < joined_meetings_values.get(0).size(); i ++) 
				joined_meetings += joined_meetings_values.get(0).get(i).toString().trim() + '\n';
				
		} catch(Exception e) {};
		
		if (joined_meetings.equals("")) joined_meetings = "FAIL_TO_GET_JOINED_MEETINGS";
		return joined_meetings;
	}
	
	public static synchronized String get_created_meetings(String account_id) {
		String created_meetings = "";
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "USER ACCOUNT DATABASE").getValues();
			int creater_row_index = get_user_row_by_account_index(account_id, values);
			
			char cell_column = (char)('A' + USER_ACCOUNT_DATABASE.CREATED_MEETING.get_index());
			String cell_row = String.valueOf(creater_row_index);
			String cell_location = cell_column + cell_row;
			String range = "USER ACCOUNT DATABASE!" + cell_location + ":" + cell_location;
			List<List<Object>> created_meetings_values = SpreadSheetSnippets.getValues(spreadSheetID, range).getValues();
			
			for (int i = 0; i < created_meetings_values.get(0).size(); i ++) 
				created_meetings += created_meetings_values.get(0).get(i).toString().trim() + '\n';
				
		} catch(Exception e) {};
		
		if (created_meetings.equals("")) created_meetings = "FAIL_TO_GET_CREATED_MEETINGS";
		return created_meetings;
	}
	
	public static synchronized int get_user_row_by_account_index(String joiner_id, List<List<Object>> values) {
		int joiner_row = -1;
		
		for (int i = 0; i < values.size(); i ++) 
			if (values.get(i).get(USER_ACCOUNT_DATABASE.INDEX.get_index()).toString().trim().equals(joiner_id)) {
				joiner_row = i + 1;
				break;
			}
		
		return joiner_row;
	}
}
