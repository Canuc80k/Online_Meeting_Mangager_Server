package database;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import gsheet.GoogleDriveSnippets;
import gsheet.SpreadSheetSnippets;

public class User_activity_in_meeting_database {
	private static final String SHEET_SPLIT_SIGNAL = "\nPPPPPPPP\n";
	
	public enum USER_ACTIVITY_IN_MEETING_DATABASE {
		INDEX(0),
		TAB_CHANGE(1),
		APP_CHANGE(2),
		APP_TIME_USAGE(3),
		APP_USAGE_RAW_DATA(4),
		CLIPBOARD_USAGE(5);

		private int index;
		
		USER_ACTIVITY_IN_MEETING_DATABASE(int new_index) {
			this.index = new_index;
		}

		public int get_index() {
			return index;
		}
		
		public static String get_last_column() {
			return "F";
		}
	}
	
	public static synchronized String get_raw_data(String account_id, String meeting_id) throws Exception {
		String all_raw_data = "";
		try {
			List<com.google.api.services.drive.model.File> all_user_activity_in_meeting_database = GoogleDriveSnippets.getGoogleFilesByName(
					meeting_id
			);
			
			String spreadSheetID = all_user_activity_in_meeting_database.get(0).getId();
			Spreadsheet spreadSheet = SpreadSheetSnippets.getService().spreadsheets().get(spreadSheetID).execute();
			List<Sheet> sheets = spreadSheet.getSheets();
			
			for (int i = 0; i < sheets.size(); i ++) {
				String sheetName = sheets.get(i).getProperties().getTitle().trim();
				List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, sheetName).getValues();
				String row = String.valueOf(get_joiner_row_by_account_index(account_id, values)).trim();
				char col = (char)('A' + USER_ACTIVITY_IN_MEETING_DATABASE.APP_USAGE_RAW_DATA.get_index());
				String cell = col + row;
				String range = sheetName + '!' + cell + ":" + cell;
				values = SpreadSheetSnippets.getValues(spreadSheetID, range).getValues();
				if (values == null) continue;
				all_raw_data += values.get(0).get(0).toString();
				if (i + 1 < sheets.size()) all_raw_data += SHEET_SPLIT_SIGNAL;
			}
		} catch (Exception e) {e.printStackTrace();}
		
		return all_raw_data;
	}
	
	public static synchronized byte[] download_meeting(String meeting_id) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			List<com.google.api.services.drive.model.File> all_user_activity_in_meeting_database = GoogleDriveSnippets.getGoogleFilesByName(
					meeting_id
			);
			
			String spreadSheetID = all_user_activity_in_meeting_database.get(0).getId();
			GoogleDriveSnippets.getDriveService().files().export(spreadSheetID, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
			    .executeMediaAndDownloadTo(outputStream);
		} catch(Exception e) {e.printStackTrace();}
		
		return outputStream.toByteArray();
	}
	
	public static synchronized void create_database(String meeting_id) throws Exception {
		String parent_folder_id;
		try {parent_folder_id = GoogleDriveSnippets.getGoogleSubFolderByName(null, "User Activity In Meeting").get(0).getId();} 
		catch (Exception e) {parent_folder_id = GoogleDriveSnippets.createGoogleFolder(null, "User Activity In Meeting").getId();}
		
		try {	
			String spreadSheetID = GoogleDriveSnippets.createNewSpreadSheet(meeting_id);
			GoogleDriveSnippets.moveFileToFolder(spreadSheetID, parent_folder_id);
			SpreadSheetSnippets.rename_worksheet(spreadSheetID, "Running_Temp_1");
			GoogleDriveSnippets.createPublicPermission(spreadSheetID);
			
			List<List<Object>> values = new ArrayList<List<Object>>();
			List<Object> first_row = new ArrayList<Object>();
			first_row.add("ID");
			first_row.add("Số Lần Chuyển Tab"); 
			first_row.add("Số Lần Chuyển App"); 
			first_row.add("Thời Gian Sử Dụng Các App");
			first_row.add("Dữ Liệu Sử Dụng App Thô");
			first_row.add("Dữ Liệu Clipboard");
			values.add(first_row);
			SpreadSheetSnippets.appendValues(spreadSheetID, "Running_Temp_1", "RAW", values);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static synchronized boolean add_new_joiner_to_database(String meeting_id_need_to_join, String joiner_id) {
		boolean add_successfully = false;
		try {
			List<com.google.api.services.drive.model.File> all_user_activity_in_meeting_database = GoogleDriveSnippets.getGoogleFilesByName(
					meeting_id_need_to_join
			);
			
			String spreadSheetID = all_user_activity_in_meeting_database.get(0).getId();
			
			Spreadsheet spreadSheets = SpreadSheetSnippets.getService().spreadsheets().get(spreadSheetID).execute();
			List<Sheet> sheets = spreadSheets.getSheets();
			String current_sheet_tab = sheets.get(sheets.size() - 1).getProperties().getTitle();
			List<List<Object>> values = new ArrayList<List<Object>>();
			List<Object> new_row = new ArrayList<Object>();
			new_row.add(joiner_id);
			values.add(new_row);
			SpreadSheetSnippets.appendValues(spreadSheetID, current_sheet_tab, "RAW", values);
			add_successfully = true;
		} catch(Exception e) {}
		return add_successfully;
	}

	public static synchronized boolean update_joiner_data_row(String spreadSheetID, String joiner_id, List<Object> update_row_value) {
		boolean add_successfully = false;
		
		try {
			List<Sheet> sheets = SpreadSheetSnippets.getService().spreadsheets().get(spreadSheetID).setIncludeGridData(false).execute().getSheets();
			String sheetName = sheets.get(sheets.size() - 1).getProperties().getTitle();
			List<List<Object>> values = SpreadSheetSnippets.getValues(spreadSheetID, sheetName).getValues();
			String row = String.valueOf(get_joiner_row_by_account_index(joiner_id, values));
			String range = sheetName + "!" + "B" + row + ":" + USER_ACTIVITY_IN_MEETING_DATABASE.get_last_column() + row;
			List<List<Object>> new_values = new ArrayList<List<Object>>();
			new_values.add(update_row_value);
			SpreadSheetSnippets.batchUpdateValues(spreadSheetID, range, "RAW", new_values);
			add_successfully = true;
		} catch(Exception e) {}
		
		return add_successfully;
	}
	
	public static synchronized String get_meeting_spreadSheetID(String meetingID) throws Exception {
		return GoogleDriveSnippets.getGoogleFilesByName(meetingID).get(0).getId().trim();
	}
	
	public static synchronized int get_joiner_row_by_account_index(String joiner_id, List<List<Object>> values) {
		int joiner_row = -1;
		
		for (int i = 0; i < values.size(); i ++) 
			if (values.get(i).get(USER_ACTIVITY_IN_MEETING_DATABASE.INDEX.get_index()).toString().trim().equals(joiner_id)) {
				joiner_row = i + 1;
				break;
			}
		
		return joiner_row;
	}
}
