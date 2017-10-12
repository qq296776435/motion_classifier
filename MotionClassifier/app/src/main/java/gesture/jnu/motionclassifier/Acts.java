package gesture.jnu.motionclassifier;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Acts extends DataSupport implements Serializable {
    private String action_name;
    @Column(ignore = true)
    private int duration;

    @Column
    private String uid;
    private int group_id;
    private int action_id;

    public Acts(String action_name, int duration, String uid, int group_id, int action_id) {
        this.action_name = action_name;
        this.duration = duration;
        this.uid = uid;
        this.group_id = group_id;
        this.action_id = action_id;
    }

    public String getAction_name() {
        return action_name;
    }

    public void setAction_name(String action_name) {
        this.action_name = action_name;
    }

    public int getDuration(){
        return duration;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getAction_id() {
        return action_id;
    }

    public void setAction_id(int action_id) {
        this.action_id = action_id;
    }

    @Override
    public String toString(){
        return String.format("%s %d", action_name, group_id);
    }
}
