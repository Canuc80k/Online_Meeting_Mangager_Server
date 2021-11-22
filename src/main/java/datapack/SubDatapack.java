package datapack;

public class SubDatapack {
    String meeting_id;
    String app_activity_log;
    String clipboard;

    public SubDatapack(String meeting_id, String app_activity_log) {
        this.meeting_id = meeting_id;
        this.app_activity_log = app_activity_log;
    }

    public SubDatapack(String meeting_id, String app_activity_log, String clipboard) {
        this.meeting_id = meeting_id;
        this.app_activity_log = app_activity_log;
        this.clipboard = clipboard;
    }
}
