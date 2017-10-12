package gesture.jnu.motionclassifier;

public class GyrData {
    float x, y, z;
    long timestamp;

    public GyrData(float[] values, long timestamp){
        x = values[0];
        y = values[1];
        z = values[2];
        this.timestamp = timestamp;
    }

    public float[] getValue(){
        return new float[]{x, y, z};
    }

    public String toString(){
        return String.format("x:%.4f y:%.4f, z:%.4f", x, y, z);
    }
}
