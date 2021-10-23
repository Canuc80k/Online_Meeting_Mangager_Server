package gsheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveSnippets {

	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static Drive driveService;

	public static synchronized Drive createService() throws Exception {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = SpreadSheetSnippets.getCredentials(HTTP_TRANSPORT);

		driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
		return driveService;
	}
	
	public static synchronized Drive getDriveService() throws Exception {
		return driveService;
	}
	
	public static synchronized String createNewSpreadSheet(String title) throws IOException {
		File fileMetadata = new File();
		fileMetadata.setName(title);
		fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

		File file = driveService.files().create(fileMetadata)
		    .setFields("id")
		    .execute();
		
		return file.getId();
	}
	
	public static synchronized void moveFileToFolder(String fileID, String folderID) throws Exception {
		File file = driveService.files().get(fileID)
			    .setFields("parents")
			    .execute();
			StringBuilder previousParents = new StringBuilder();
			for (String parent : file.getParents()) {
			  previousParents.append(parent);
			  previousParents.append(',');
			}
			// Move the file to the new folder
			file = driveService.files().update(fileID, null)
			    .setAddParents(folderID)
			    .setRemoveParents(previousParents.toString())
			    .setFields("id, parents")
			    .execute();
	}
	
	public static final synchronized File createGoogleFolder(String folderIdParent, String folderName) throws Exception {

        File fileMetadata = new File();

        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (folderIdParent != null) {
            List<String> parents = Arrays.asList(folderIdParent);

            fileMetadata.setParents(parents);
        }
        Drive driveService = GoogleDriveSnippets.getDriveService();

        // Create a Folder.
        // Returns File object with id & name fields will be assigned values
        File file = driveService.files().create(fileMetadata).setFields("id, name").execute();

        return file;
    }

	public static final synchronized List<File> getGoogleSubFolderByName(String googleFolderIdParent, String subFolderName) throws Exception {
        String pageToken = null;
        List<File> list = new ArrayList<File>();

        String query = null;
        if (googleFolderIdParent == null) {
            query = " name = '" + subFolderName + "' " //
                    + " and mimeType = 'application/vnd.google-apps.folder' " //
                    + " and 'root' in parents";
        } else {
            query = " name = '" + subFolderName + "' " //
                    + " and mimeType = 'application/vnd.google-apps.folder' " //
                    + " and '" + googleFolderIdParent + "' in parents";
        }

        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    .setFields("nextPageToken, files(id, name, createdTime)")//
                    .setPageToken(pageToken).execute();
            for (File file : result.getFiles()) {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        
        return list;
    }
	
	public static final synchronized List<File> getGoogleFilesByName(String fileNameLike) throws IOException {
		String pageToken = null;
		List<File> list = new ArrayList<File>();

		String query = " name contains '" + fileNameLike + "' " //
				+ " and mimeType != 'application/vnd.google-apps.folder' ";

		do {
			FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
					// Fields will be assigned values: id, name, createdTime, mimeType
					.setFields("nextPageToken, files(id, name, createdTime, mimeType)")//
					.setPageToken(pageToken).execute();
			for (File file : result.getFiles()) {
				list.add(file);
			}
			pageToken = result.getNextPageToken();
		} while (pageToken != null);
		//
		return list;
	}
}