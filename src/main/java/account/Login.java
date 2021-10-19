package account;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import general_function.FileTool;

public class Login {
	private static final String USER_ACCOUNT_NAME_AND_PASS_FILE_PATH = "user_accounts/user_account_name_and_pass";
	private static final String USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH = "user_accounts/user_account_specified_data/";
	private static final String FAIL_TO_LOGIN_SIGNAL = "FAIL_TO_LOGIN";
	
	private static String username, password;
	
	/*
	 *	@param account_data format: "username + ' ' + password"
	 *	@do check login data in USER_ACCOUNT_DATA_FILE_PATH
	 *	@TODO update to database
	 */
	public static String login(String account_data) throws Exception {
		if (!(new File(USER_ACCOUNT_NAME_AND_PASS_FILE_PATH).exists())) FileTool.write_file("", USER_ACCOUNT_NAME_AND_PASS_FILE_PATH);
		if (!(new File(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH).exists())) new File(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH).mkdirs();
			
		
		String account_id = FAIL_TO_LOGIN_SIGNAL;
		
		login_init(account_data);
		
		File[] files = new File(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
		        String file_data = FileTool.read_file(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + '/' + file.getName());
		        List<String> file_data_list = Arrays.asList(file_data.split("\n"));
		        
		        if (file_data_list.get(0).trim().equals(username) && file_data_list.get(1).trim().equals(password)) {
		        	account_id = file.getName().trim();
		        }
		    }
		}
		
		return account_id;
	}
	
	private static void login_init(String account_data) {
		List<String> account_data_list = Arrays.asList(account_data.split(" "));
		username = account_data_list.get(0).trim();
		password = account_data_list.get(1).trim();
	}
}
