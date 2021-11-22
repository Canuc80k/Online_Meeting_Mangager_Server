package datapack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import general_function.FileTool;

public class Datapack_receiver {
    private static final String MEETING_IN_DATAPACK_SPLIT_SIGNAL = "MEETING_SPLIT_SIGNAL";
    private static final String MEETING_DATA_IN_DATAPACK_SPLIT_SIGNAL = "MEETING_DATA_SPLIT_SIGNAL";
    private static final String DATAPACK_FOLDER_PATH = "src/main/resources/datapack/";

    private String account_id;
    private List<SubDatapack> meetings_data = new ArrayList<SubDatapack>(); 

    public synchronized String save_datapack_to_cache(String datapack) throws Exception {
        String save_successfully = "SAVE_DATAPACK_SUCCESSFULLY";

        try {
            save_datapack_to_cache_init(datapack);
            save_datapack_to_file();
        } catch(Exception e) {
            e.printStackTrace();
            save_successfully = "FAIL_TO_SAVE_DATAPACK";
        }

        return save_successfully;
    }

    private synchronized void save_datapack_to_cache_init(String datapack) {
        List<String> datapack_split_by_line = Arrays.asList(datapack.split("\n"));
        this.account_id = datapack_split_by_line.get(0);
        
        String meetings = "";
        for (int i = 1; i < datapack_split_by_line.size(); i ++) {
            meetings += datapack_split_by_line.get(i) + '\n';
        }

        List<String> meetings_list = Arrays.asList(meetings.split(MEETING_IN_DATAPACK_SPLIT_SIGNAL));
        System.out.println(meetings_list.size());
        
        for (int i = 0; i < meetings_list.size(); i ++) {
            List<String> meetingdata_list = Arrays.asList(meetings_list.get(i).split(MEETING_DATA_IN_DATAPACK_SPLIT_SIGNAL));
            if (meetingdata_list.size() >= 2) {
	            this.meetings_data.add(new SubDatapack(
	                meetingdata_list.get(0).trim(),
	                meetingdata_list.get(1)
	            ));
            }
        }
    }

    private synchronized void save_datapack_to_file() throws Exception {
        for (int i = 0; i < meetings_data.size(); i ++) {
            File meeting_folder = new File(DATAPACK_FOLDER_PATH + meetings_data.get(i).meeting_id);
            if (!meeting_folder.exists()) meeting_folder.mkdirs();
            File account_folder = new File(meeting_folder.getPath() + '/' + account_id);
            if (!account_folder.exists()) account_folder.mkdirs();

            File app_activity_log = new File(account_folder.getPath() + "/app_activity_log");
            FileTool.write_file(meetings_data.get(i).app_activity_log, app_activity_log.getPath());
        }
    }
}
