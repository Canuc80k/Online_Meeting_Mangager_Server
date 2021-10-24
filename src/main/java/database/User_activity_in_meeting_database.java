package database;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import gsheet.GoogleDriveSnippets;
import gsheet.SpreadSheetSnippets;

public class User_activity_in_meeting_database {
	public enum USER_ACTIVITY_IN_MEETING_DATABASE {
		
	}
	
	public static synchronized void create_database(String meeting_information, String meeting_id) throws Exception {
		if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.createService();
		if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
			
		String parent_folder_id;
		try {parent_folder_id = GoogleDriveSnippets.getGoogleSubFolderByName(null, "User Activity In Meeting").get(0).getId();} 
		catch (Exception e) {parent_folder_id = GoogleDriveSnippets.createGoogleFolder(null, "User Activity In Meeting").getId();}
		
		try {	
			String spreadSheetID = GoogleDriveSnippets.createNewSpreadSheet(meeting_id);
			GoogleDriveSnippets.moveFileToFolder(spreadSheetID, parent_folder_id);
			SpreadSheetSnippets.rename_worksheet(spreadSheetID, "Running_Temp_1");
			
			List<List<Object>> values = new ArrayList<List<Object>>();
			List<Object> first_row = new ArrayList<Object>();
			first_row.add("ID"); first_row.add("Số Lần Chuyển Tab"); first_row.add("Số Lần Chuyển App"); first_row.add("Dữ Liệu Thô");
			values.add(first_row);
			SpreadSheetSnippets.appendValues(spreadSheetID, "Running_Temp_1", "RAW", values);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static synchronized boolean add_new_joiner_to_database(String meeting_id_need_to_join, String joiner_id) {
		boolean add_successfully = false;
		try {
			if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.createService();
			if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
			
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
}
