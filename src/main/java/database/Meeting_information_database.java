package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gsheet.SpreadSheetSnippets;

public class Meeting_information_database {
	public enum MEETING_INFORMATION_DATABASE {
		
	}
	
	public static synchronized void add_new_meeting_to_database(String new_meeting_information, String meeting_id) {
		List<Object> append_row = new ArrayList<Object>();
		List<String> meeting_information_list = Arrays.asList(new_meeting_information.split("\n"));
		append_row.add(meeting_id);
		append_row.add(meeting_id);
		for (int i = 0; i < meeting_information_list.size(); i ++)
			append_row.add(meeting_information_list.get(i));
		List<List<Object>> values = new ArrayList<List<Object>>();
		values.add(append_row);
		
		try {
			if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
			String spreadSheetID = SpreadSheetSnippets.get_meeting_information_database_spread_sheet_id();
			SpreadSheetSnippets.appendValues(spreadSheetID, "Meeting Information Database", "RAW", values);
		} catch (Exception e) {e.printStackTrace();}
	}
}
