package meeting;

public class Meeting_ID {
	public static String get_next_meeting_id(String current_id) {
		String new_id = String.valueOf(Integer.parseInt(current_id) + 1);
		
		int amount_of_front_0 = 10 - new_id.length();
		String bonus_front_0 = "";
		for (int i = 0; i < amount_of_front_0; i ++) bonus_front_0 += "0";
		
		return bonus_front_0 + new_id;
	}
	
	public static boolean check_valid_meeting_id(String meeting_id) {
		boolean result = true;
		
		if (meeting_id.length() != 10) result = false;
		try {
			Integer.parseInt(meeting_id);
		} catch(Exception e) {
			result = false;
		}
		
		return result;		
	}
}
