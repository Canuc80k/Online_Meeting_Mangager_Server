package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import database.User_account_database.USER_ACCOUNT_DATABASE;
import gsheet.SpreadSheetSnippets;

public class Meeting_information_database {
	public enum MEETING_INFORMATION_DATABASE {
		INDEX(0),
		MEETING_CODE(1),
		TYPE(2),
		NAME(3),
		T2(4),
		T3(5),
		T4(6),
		T5(7),
		T6(8),
		T7(9),
		CN(10),
		STATE(11),
		HOST(12);
		
		private int index;
		
		MEETING_INFORMATION_DATABASE(int new_index) {
			this.index = new_index;
		}

		public int get_index() {
			return index;
		}
		
		public static String get_last_column() {
			return "M";
		}
	}
	
	public static synchronized void add_new_meeting(String new_meeting_information, String meeting_id) {
		System.out.println(new_meeting_information);
		List<Object> append_row = new ArrayList<Object>();
		List<String> meeting_information_list = Arrays.asList(new_meeting_information.split("\n"));
		append_row.add(meeting_id);
		append_row.add(meeting_id);
		for (int i = 0; i < meeting_information_list.size(); i ++)
			append_row.add(meeting_information_list.get(i));
		List<List<Object>> values = new ArrayList<List<Object>>();
		values.add(append_row);
		
		System.out.println(values);
		try {
			if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
			String spreadSheetID = SpreadSheetSnippets.get_meeting_information_database_spread_sheet_id();
			SpreadSheetSnippets.appendValues(spreadSheetID, "Meeting Information Database", "RAW", values);
		} catch (Exception e) {e.printStackTrace();}
	}

	public static String get_meeting_info(String meeting_id) throws Exception {
		String meeting_info = "";
		
		try {
			String spreadSheetID = SpreadSheetSnippets.get_meeting_information_database_spread_sheet_id();	
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, "Meeting Information Database").getValues();
			int meeting_row_index = get_meeting_row_by_index(meeting_id, values);
			
			String last_column = MEETING_INFORMATION_DATABASE.get_last_column();
			String row = String.valueOf(meeting_row_index);
			String range = "Meeting Information Database!" + "A" + row + ":" + last_column + row;
			List<List<Object>> meeting_info_values = SpreadSheetSnippets.getValues(spreadSheetID, range).getValues();
			
			for (int i = 0; i < meeting_info_values.get(0).size(); i ++) 
				meeting_info += meeting_info_values.get(0).get(i).toString().trim() + '\n';
		} catch (Exception e) {}
		
		if (meeting_info.equals("")) meeting_info = "FAIL_TO_GET_MEETING_INFO";
		return meeting_info;
	}
	
	public static synchronized int get_meeting_row_by_index(String meeting_id, List<List<Object>> values) {
		int meeting_row = -1;
		
		for (int i = 0; i < values.size(); i ++) 
			if (values.get(i).get(USER_ACCOUNT_DATABASE.INDEX.get_index()).toString().trim().equals(meeting_id)) {
				meeting_row = i + 1;
				break;
			}
		
		return meeting_row;
	}
}
