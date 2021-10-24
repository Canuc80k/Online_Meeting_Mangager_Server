package database;

import java.util.ArrayList;
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
		JOINED_MEETING(11);

		private int index;
		
		USER_ACCOUNT_DATABASE(int new_index) {
			this.index = new_index;
		}

		public int get_index() {
			return index;
		}
	}
	
	public static synchronized boolean add_to_user_account_database(String meeting_id_need_to_join, String joiner_id) {
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
}
