package gsheet;

import java.io.IOException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

import database.User_account_database.USER_ACCOUNT_DATABASE;

public class Testing {
	public static void main(String[] args) throws IOException, Exception {
		if (SpreadSheetSnippets.getService() == null) SpreadSheetSnippets.createService();
		
		ValueRange response = SpreadSheetSnippets.getService().spreadsheets().values()
                .get("1AL9VQF1BD2TPdL1uCQheckNVivCyvVKQTTUloOjYqm0", "User Account Database")
                .execute();
		
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row.get(0), row.get(4));
            }
        }
	}
}







