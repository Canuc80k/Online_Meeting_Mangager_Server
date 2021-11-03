package account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import gsheet.SpreadSheetSnippets;

public class Register {
	private static String spreadSheetID;
	
	public static boolean register(String register_data) throws Exception {
		register_data = register_data.trim();
		spreadSheetID = SpreadSheetSnippets.get_user_account_database_spread_sheet_id();
        
		boolean register_successful = false;		
		if (!is_account_exist(register_data)) {
			try {
				String previous_index = get_previous_index();
				String current_index = String.valueOf(Integer.parseInt(previous_index) + 1);
				List<String> register_data_list = Arrays.asList(register_data.split("\n"));

				List<Object> new_row = new ArrayList<Object>();
				new_row.add(current_index);
				for (int i = 0; i < register_data_list.size(); i ++) 
					new_row.add(register_data_list.get(i));
				
				List<List<Object>> values = new ArrayList<List<Object>>();
				values.add(new_row);

				final String range = "User Account Database";
		        SpreadSheetSnippets.appendValues(spreadSheetID, range, "RAW", values);
		        register_successful = true;
			} catch(Exception e) {}
		} 
		
		return register_successful;
	}
	
	public static boolean is_account_exist(String register_data) throws Exception {
		List<String> register_data_list = Arrays.asList(register_data.split("\n"));
		String username = register_data_list.get(0).trim();

		final String range = "User Account Database!A2:L";
        ValueRange response = SpreadSheetSnippets.getService().spreadsheets().values()
                .get(SpreadSheetSnippets.get_user_account_database_spread_sheet_id(), range)
                .execute();
        List<List<Object>> values = response.getValues();
        
        if (values == null) return false;
        for (List<Object> row : values) 
        	if (row.get(1).toString().equals(username)) 
        		return true;
   
        return false;
	}
	
	public static String get_previous_index() throws Exception {
		String id;
		
		final String range = "User Account Database!A2:L";
        ValueRange response = SpreadSheetSnippets.getService().spreadsheets().values()
                .get(SpreadSheetSnippets.get_user_account_database_spread_sheet_id(), range)
                .execute();
        List<List<Object>> values = response.getValues();
        
        if (values == null) return "0";
        id = values.get(values.size() - 1).get(0).toString();

        return id;
	}
}
