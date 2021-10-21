package gsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import general_function.FileTool;

public class Testing {
	private static final String SPREAD_SHEET_ID_FOLDER_PATH = "src/main/resources/spread_sheet_id/";
	
	private static String spreadSheetID;
	
	private static String get_spread_sheet_id() throws Exception {
		String spreadSheetID = null;

		try {spreadSheetID = FileTool.read_file(SPREAD_SHEET_ID_FOLDER_PATH + "user_account_database_spread_sheet_id").trim();}
		catch(Exception e) {}
		
		if (spreadSheetID == null) {
			try {
				spreadSheetID = SpreadSheetSnippets.create("User Account Database");
				FileTool.write_file(spreadSheetID, SPREAD_SHEET_ID_FOLDER_PATH + "user_account_database_spread_sheet_id");
			} catch (Exception e) {
				spreadSheetID = FileTool.read_file(SPREAD_SHEET_ID_FOLDER_PATH + "user_account_database_spread_sheet_id").trim();
			}
		}
		
		return spreadSheetID;
	}

	
    public static void main(String... args) throws Exception {
		if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
		
		spreadSheetID = get_spread_sheet_id();
		/*final String range = "A:Z";
        ValueRange response = SpreadsheetSnippets.getService().spreadsheets().values()
                .get(spreadSheetID, range)
                .execute();
        List<List<Object>> values = response.getValues();
		
		SpreadsheetSnippets.appendValues(spreadSheetID, range, "RAW", values);*/
		ValueRange appendBody = new ValueRange()
                .setValues(Collections.singletonList(
                        Arrays.asList("ColumnName1", "ColumnName2")));
		SpreadSheetSnippets.getService().spreadsheets().values()
        .append(spreadSheetID, "TITLE_OF_YOUR_NEW_TAB", appendBody)
        .setValueInputOption("USER_ENTERED")
        .setInsertDataOption("INSERT_ROWS")
        .setIncludeValuesInResponse(true)
        .execute();
    }
}
