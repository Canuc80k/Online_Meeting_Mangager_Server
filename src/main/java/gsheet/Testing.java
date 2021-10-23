package gsheet;

import java.io.IOException;

public class Testing {
	public static void main(String[] args) throws IOException, Exception {
		if (GoogleDriveSnippets.getDriveService() == null) GoogleDriveSnippets.getDriveService();
		String spreadSheetID = GoogleDriveSnippets.createNewSpreadSheet("aa");
		System.out.println(spreadSheetID);
		GoogleDriveSnippets.moveFileToFolder(spreadSheetID, "1UKiQPz4cpwGpxBLgGCE-TewwCXRKBQQs");
	}
}







