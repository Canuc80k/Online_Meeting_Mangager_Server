package account;

import java.util.Arrays;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import gsheet.SpreadSheetSnippets;


public class Login {
	private static final String FAIL_TO_LOGIN_SIGNAL = "FAIL_TO_LOGIN";
	
	private static String username, password;

	public static String login(String account_data) throws Exception {
		List<String> account_data_list = Arrays.asList(account_data.split(" "));
		username = account_data_list.get(0).trim();
		password = account_data_list.get(1).trim();
		
		String account_id = FAIL_TO_LOGIN_SIGNAL;
		final String range = "User Account Database!A2:L";

        ValueRange response = SpreadSheetSnippets.getService().spreadsheets().values()
                .get(SpreadSheetSnippets.get_user_account_database_spread_sheet_id(), range)
                .execute();
        
        List<List<Object>> values = response.getValues();
        for (List<Object> row : values) {
        	if (row.get(1).toString().equals(username) && row.get(2).toString().equals(password)) {
        		account_id = row.get(0).toString();
        		break;
        	}
        }
		return account_id;
	}
}
