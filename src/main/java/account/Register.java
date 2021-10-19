package account;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import general_function.FileTool;

public class Register {
	private static final String CURRENT_AVAILABLE_USER_ACCOUNT_ID_FILE_PATH = "user_accounts/current_available_user_account_id";
	private static final String USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH = "user_accounts/user_account_specified_data/";

	private static String current_available_user_account_id;
	private static String register_account_name, register_account_pass;
	
	public static boolean register(String register_account_name_and_pass) throws Exception {
		if (!(new File(CURRENT_AVAILABLE_USER_ACCOUNT_ID_FILE_PATH).exists())) FileTool.write_file("", CURRENT_AVAILABLE_USER_ACCOUNT_ID_FILE_PATH);

		boolean register_successful = false;
		init(register_account_name_and_pass);
		if (check_register(register_account_name_and_pass)) {
			create_user_account_specified_data_file(current_available_user_account_id);
			update_current_available_id(current_available_user_account_id);
			register_successful = true;
		}
		else register_successful = false;
		return register_successful;
	}
	
	public static void init(String register_account_name_and_pass) throws Exception {
		current_available_user_account_id = FileTool.read_file(CURRENT_AVAILABLE_USER_ACCOUNT_ID_FILE_PATH).trim();
		if (current_available_user_account_id == null || current_available_user_account_id.equals("")) current_available_user_account_id = "00000";
		
		register_account_name = Arrays.asList(register_account_name_and_pass.split(" ")).get(0).trim();
		register_account_pass = Arrays.asList(register_account_name_and_pass.split(" ")).get(1).trim();
	}
	
	public static boolean check_register(String register_account_name_and_pass) throws Exception {	
		boolean able_to_register = true;
		
		File[] files = new File(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH).listFiles();
		for (File file : files) {
		    if (file.isFile()) {
		        String file_data = FileTool.read_file(USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + '/' + file.getName());
		        List<String> file_data_list = Arrays.asList(file_data.split("\n"));
		        
		        if (file_data_list.get(0).trim().equals(register_account_name)) {
		        	able_to_register = false;
		        }
		    }
		}
		
		return able_to_register;
	}
	

	public static void create_user_account_specified_data_file(String current_available_id) throws Exception {
		String account_specified_data = "";
		account_specified_data += register_account_name + '\n';
		account_specified_data += register_account_pass + '\n';
		account_specified_data += "Chưa Có Thông Tin\nChưa Có Thông Tin\nChưa Có Thông Tin\nChưa Có Thông Tin\nChưa Có Thông Tin\n";
		
		FileTool.write_file(account_specified_data, USER_ACCOUNT_SPECIFIED_DATA_FOLDER_PATH + '/' + current_available_id);
	}

	public static void update_current_available_id(String current_available_id) throws Exception {
		String new_id = User_ID.get_next_user_account_id(current_available_id);
		FileTool.write_file(new_id, CURRENT_AVAILABLE_USER_ACCOUNT_ID_FILE_PATH);
	}
}
