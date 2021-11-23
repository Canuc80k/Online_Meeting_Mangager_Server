package datapack;

import java.util.Arrays;
import java.util.List;

import general_function.FileTool;

public class Datapack_analyst {
    private static final String APP_ACTIVITY_DATA_SPLIT_SIGNAL = "_   _";
    private static final String DATAPACK_FOLDER_PATH = "src/main/resources/datapack/";
    private static final String MEETING_IN_DATAPACK_SPLIT_SIGNAL = "MEETING_SPLIT_SIGNAL";
    private static final String MEETING_DATA_IN_DATAPACK_SPLIT_SIGNAL = "MEETING_DATA_SPLIT_SIGNAL";

    public synchronized String analize(String account_id, List<SubDatapack> meetings_data) throws Exception {
        String result = ""; 

        for (int i = 0; i < meetings_data.size(); i ++) {
            result += analize_meeting(account_id, meetings_data.get(i));
            result += MEETING_IN_DATAPACK_SPLIT_SIGNAL;
        }

        return result;
    }

    private synchronized String analize_meeting(String account_id, SubDatapack meeting_data) throws Exception {
        String result = "";
        
        try {
            String meeting_id = meeting_data.meeting_id;
            String previous_app_activity_log = FileTool.read_file(DATAPACK_FOLDER_PATH + meeting_id + '/' + account_id + "/app_activity_log");
            String current_app_activity_log = meeting_data.app_activity_log;
            
            result += String.valueOf(count_tab_change(previous_app_activity_log, current_app_activity_log)) + MEETING_DATA_IN_DATAPACK_SPLIT_SIGNAL;
            result += String.valueOf(count_app_change(previous_app_activity_log, current_app_activity_log)) + MEETING_DATA_IN_DATAPACK_SPLIT_SIGNAL;
        } catch (Exception e) {e.printStackTrace();}

        return result;
    }

    private synchronized int count_tab_change(String previous_app_activity_log, String current_app_activity_log) {
        int tab_change = 0;

        List<String> prev_log_list = Arrays.asList(previous_app_activity_log.split("\n"));
        List<String> cur_log_list = Arrays.asList(current_app_activity_log.split("\n"));
 
        String app_name = "";
        if (prev_log_list.size() > 0) {
            List<String> temp_list = Arrays.asList(prev_log_list.get(prev_log_list.size() - 1).split(APP_ACTIVITY_DATA_SPLIT_SIGNAL));
            String temp_app_title = temp_list.get(0).trim();  
            List<String> temp1_list = Arrays.asList(temp_app_title.split(" - "));
            app_name = temp1_list.get(temp1_list.size() - 1).trim();
        }

        for (int i = prev_log_list.size(); i < cur_log_list.size(); i ++) {
            try {
				List<String> data_part_list = Arrays.asList(cur_log_list.get(i).split(APP_ACTIVITY_DATA_SPLIT_SIGNAL));
				String app_title = data_part_list.get(0).trim();
				
				List<String> app_title_list = Arrays.asList(app_title.split(" - "));
                String new_app_name = app_title_list.get(app_title_list.size() - 1).trim();
                if (app_name.equals(new_app_name)) tab_change ++;
				app_name = new_app_name;
			} catch(Exception e) {e.printStackTrace();}
        }

        return tab_change;
    }

    private synchronized int count_app_change(String previous_app_activity_log, String current_app_activity_log) {
        int app_change = 0;

        List<String> prev_log_list = Arrays.asList(previous_app_activity_log.split("\n"));
        List<String> cur_log_list = Arrays.asList(current_app_activity_log.split("\n"));
 
        String app_name = "";
        for (int i = prev_log_list.size(); i < cur_log_list.size(); i ++) {
            try {
				List<String> data_part_list = Arrays.asList(cur_log_list.get(i).split(APP_ACTIVITY_DATA_SPLIT_SIGNAL));
				String app_title = data_part_list.get(0).trim();
				
				List<String> app_title_list = Arrays.asList(app_title.split(" - "));
                String new_app_name = app_title_list.get(app_title_list.size() - 1).trim();
                if (!app_name.equals(new_app_name)) app_change ++;
				app_name = new_app_name;
			} catch(Exception e) {e.printStackTrace();}
        }

        return app_change;
    }
}

