package gsheet;

import java.io.IOException;
import java.util.List;

public class Testing {
	public static void main(String[] args) throws IOException, Exception {
		if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.createService();
		List<com.google.api.services.drive.model.File> all_user_activity_in_meeting_database = GoogleDriveSnippets.getGoogleFilesByName(
				"0000000018"
		);
		String spreadSheetID = all_user_activity_in_meeting_database.get(0).getId();
		
		System.out.println(spreadSheetID);
	}
}







