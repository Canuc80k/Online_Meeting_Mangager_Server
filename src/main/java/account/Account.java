package account;

import java.util.Arrays;
import java.util.List;

import general_function.FileTool;

public class Account {
	private static final String USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH = "user_accounts/user_account_specified_data/";
	
	private static String account_id, account_info;
	
	public static String set_account_information(String account_data) throws Exception {
		String set_successfully = "true";
		
		List<String> account_data_list = Arrays.asList(account_data.split("\n"));
		account_id = account_data_list.get(0).trim();
		account_info = "";
		for (int i = 1; i < account_data_list.size(); i ++) {
			account_info += account_data_list.get(i).trim() + '\n';
		}
		
		String previous_account_specified_data = FileTool.read_file(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + '/' + account_id);
		List<String> previous_account_specified_data_list = Arrays.asList(previous_account_specified_data.split("\n"));
		String account_specified_data = "";
		account_specified_data += previous_account_specified_data_list.get(0) + '\n';
		account_specified_data += previous_account_specified_data_list.get(1) + '\n';
		account_specified_data += account_info;
		
		FileTool.write_file(account_specified_data, USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + account_id);
		return set_successfully;
	}
	
	public static String get_account_information(String account_id) throws Exception {
		account_id = account_id.trim();
		
		String account_specified_data = FileTool.read_file(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + '/' + account_id);
		List<String> account_specified_data_list = Arrays.asList(account_specified_data.split("\n"));
		String account_info = "";
		for (int i = 2; i < account_specified_data_list.size(); i ++) {
			account_info += account_specified_data_list.get(i) + '\n';
		}
		
		return account_info;
	}
}
