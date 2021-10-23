package gsheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveSnippets {

	private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	// Directory to store user credentials for this application.
	private static final java.io.File CREDENTIALS_FOLDER //
			= new java.io.File(System.getProperty("user.home"), "credentials");

	private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

	// Global instance of the {@link FileDataStoreFactory}.
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	// Global instance of the HTTP transport.
	private static HttpTransport HTTP_TRANSPORT;

	private static Drive _driveService;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIALS_FOLDER);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static Drive createService() throws Exception {
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Credential credential = SpreadSheetSnippets.getCredentials(HTTP_TRANSPORT);
		//
		_driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
				.setApplicationName(APPLICATION_NAME).build();
		return _driveService;
	}
	
	public static Drive getDriveService() throws Exception {
		return _driveService;
	}
	
	public static String createNewSpreadSheet(String title) throws IOException {
		File fileMetadata = new File();
		fileMetadata.setName(title);
		fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

		File file = _driveService.files().create(fileMetadata)
		    .setFields("id")
		    .execute();
		
		return file.getId();
	}
	
	public static void moveFileToFolder(String fileID, String folderID) throws Exception {
		File file = _driveService.files().get(fileID)
			    .setFields("parents")
			    .execute();
			StringBuilder previousParents = new StringBuilder();
			for (String parent : file.getParents()) {
			  previousParents.append(parent);
			  previousParents.append(',');
			}
			// Move the file to the new folder
			file = _driveService.files().update(fileID, null)
			    .setAddParents(folderID)
			    .setRemoveParents(previousParents.toString())
			    .setFields("id, parents")
			    .execute();
	}
	
	public static final File createGoogleFolder(String folderIdParent, String folderName) throws Exception {

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

	public static final List<File> getGoogleSubFolderByName(String googleFolderIdParent, String subFolderName)
            throws Exception {

        Drive driveService = GoogleDriveSnippets.getDriveService();

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
        //
        return list;
    }
}