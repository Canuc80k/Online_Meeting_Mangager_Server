package account;

public class User_ID {
	private static final String CHARACTERS_OF_62BASE[] = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
	};
	private static final int POWER_OF_62[] = {
			1, 62, 3844, 238328, 14776336, 916132832
	};
	
	public static String covert_decimal_to_62base(int input_at_decimal) {
		String result_at_62base = "";	
		
		while (input_at_decimal > 0) {
			int remain = input_at_decimal % 62;
			result_at_62base += CHARACTERS_OF_62BASE[remain];
			input_at_decimal /= 62;
		}
		
		result_at_62base = new StringBuilder(result_at_62base).reverse().toString(); 
		
		int amount_of_front_0 = 5 - result_at_62base.length();
		String bonus_front_0 = "";
		for (int i = 0; i < amount_of_front_0; i ++) bonus_front_0 += "0";
		
		return bonus_front_0 + result_at_62base;
	}
	
	public static int covert_62base_to_decimal(String input_at_62base) {
		int  result_at_decimal = 0;
		
		for (int i = input_at_62base.length() - 1; i >= 0; i --) {
			int current_index = 0;
			char current_62base_character = (char) input_at_62base.charAt(i);
			if (current_62base_character >= '0' && current_62base_character <= '9') {
				current_index = 0 - (int) '0' + (int) current_62base_character;
			}
			if (current_62base_character >= 'a' && current_62base_character <= 'z') {
				current_index = 10 - (int) 'a' + (int) current_62base_character;
			}
			if (current_62base_character >= 'A' && current_62base_character <= 'Z') {
				current_index = 36 - (int) 'A' + (int) current_62base_character;
			}
			
			result_at_decimal += current_index * POWER_OF_62[input_at_62base.length() - i - 1];
		}
		
		return result_at_decimal;
	}

	public static String get_next_user_account_id(String current_id) {
		return covert_decimal_to_62base(covert_62base_to_decimal(current_id) + 1);
	}
}
