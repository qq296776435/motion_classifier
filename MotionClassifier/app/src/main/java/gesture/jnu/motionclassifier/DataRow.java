package gesture.jnu.motionclassifier;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class DataRow extends DataSupport {
    @Column
    private float acc_x;
    private float acc_y;
    private float acc_z;

    private float gyr_x;
    private float gyr_y;
    private float gyr_z;

    private long timestamp;
    private int group_id;

    public DataRow(AccData accData, int group_id){
        try {
            setAcc(accData.getValue());
            setTimestamp(accData.timestamp);
            setGroup_id(group_id);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public DataRow(GyrData gyrData, int group_id){
        try {
            setGyr(gyrData.getValue());
            setTimestamp(gyrData.timestamp);
            setGroup_id(group_id);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public DataRow(AccData accData, GyrData gyrData, int group_id){
        try {
            setAcc(accData.getValue());
            setGyr(gyrData.getValue());
            setTimestamp(accData.timestamp);
            setGroup_id(group_id);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public DataRow(){}

    public void setAcc(float[] acc) throws Exception{
        if (acc.length != 3) throw new Exception("Wrong Parameter!");
        acc_x = acc[0];
        acc_y = acc[1];
        acc_z = acc[2];
    }

    public void setGyr(float[] gyr) throws Exception{
        if (gyr.length != 3) throw new Exception("Wrong Parameter!");
        gyr_x = gyr[0];
        gyr_y = gyr[1];
        gyr_z = gyr[2];
    }

    public void setTimestamp(long t) {
        timestamp = t;
    }

    public float getAccX(){
        return acc_x;
    }

    public float getAccY(){
        return acc_y;
    }

    public float getAccZ(){
        return acc_z;
    }

    public float getGyrX(){
        return gyr_x;
    }

    public float getGyrY(){
        return gyr_y;
    }

    public float getGyrZ(){
        return gyr_z;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String toString(){
        return String.format("(%f, %f, %f, %f, %f, %f)", acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z);
    }
}
