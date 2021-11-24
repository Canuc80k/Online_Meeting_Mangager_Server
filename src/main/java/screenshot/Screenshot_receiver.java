package screenshot;

import java.io.File;
import java.util.Arrays;

public class Screenshot_receiver {
    private static final String MEETING_SCREENSHOTS_FOLDER_PATH = "src/main/resources/screenshot/";

    public static synchronized String get_specified_screenshot_folder(String joiner_data) {
        String account_id = Arrays.asList(joiner_data.split("\n")).get(0).trim();
        String meeting_id = Arrays.asList(joiner_data.split("\n")).get(1).trim();

        File meeting_folder = new File (MEETING_SCREENSHOTS_FOLDER_PATH + meeting_id);
        if (!meeting_folder.exists()) meeting_folder.mkdirs();
        File account_folder = new File (meeting_folder.getPath() + '/' + account_id);
        if (!account_folder.exists()) account_folder.mkdirs();

        return account_folder.getPath();
    }
}
