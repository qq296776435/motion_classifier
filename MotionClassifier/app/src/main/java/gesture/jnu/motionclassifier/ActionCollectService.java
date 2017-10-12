package gesture.jnu.motionclassifier;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

public class ActionCollectService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private Sensor acce;
    private Sensor gyro;

    private int group_id;

    private CountDownTimer startTimer;
    private CountDownTimer tickTimer;

    private ArrayList<AccData> accDatas;
    private ArrayList<GyrData> gyrDatas;

    private SoundPool sp;
    private int TICK, FINISH, START;

    public void onCreate(){
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakeTag");

        acce = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sp = new SoundPool.Builder().setMaxStreams(10).build();

        START = sp.load(this, R.raw.start, 1);
        TICK = sp.load(this, R.raw.tick, 1);
        FINISH = sp.load(this, R.raw.finish, 1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int action_duration = intent.getIntExtra("action_duration", 0);
        group_id = intent.getIntExtra("group_id", 0);
        startTimer = new CountDownTimer(6000, 6000) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                accDatas = new ArrayList<>();
                gyrDatas = new ArrayList<>();
                registerSensor();
                wakeLock.acquire();
                tickTimer.start();
            }
        };
        sp.play(START, 1, 1, 0, 0, 1);
        startTimer.start();

        tickTimer = new CountDownTimer(1000*action_duration+100, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                sp.play(TICK, 1, 1, 0, 0, 1);
            }
            @Override
            public void onFinish() {
                stopService();
            }
        };

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        sp.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accDatas.add(new AccData(event.values, event.timestamp));
            popMsg(ActionCollectActivity.ACC_DATA, event.values);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyrDatas.add(new GyrData(event.values, event.timestamp));
            popMsg(ActionCollectActivity.GYR_DATA, event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }

    class Binder extends android.os.Binder{
        void cancel(){
            stopService();
        }
    }

    private void registerSensor(){
        sensorManager.registerListener(this, acce, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void stopService(){
        if(sensorManager != null)
            sensorManager.unregisterListener(this);
        sp.play(FINISH, 1, 1, 0, 0, 1);
        stopSelf();
//        saveCollectedData();
        if (wakeLock.isHeld())
            wakeLock.release();

    }

//    private void saveCollectedData() {
//        int i = accDatas.size() - 1 , j = gyrDatas.size() - 1;
//        ArrayList<DataRow> dataRows = new ArrayList<>();
//        while (i >= 0 && j >= 0){
//            if (accDatas.get(i).timestamp > gyrDatas.get(j).timestamp)
//                dataRows.add(new DataRow(accDatas.get(i--), group_id));
//
//            else if (accDatas.get(i).timestamp < gyrDatas.get(j).timestamp)
//                dataRows.add(new DataRow(gyrDatas.get(j--), group_id));
//            else
//                dataRows.add(new DataRow(accDatas.get(i--), gyrDatas.get(j--), group_id));
//        }
//        while (i >= 0)
//            dataRows.add(new DataRow(accDatas.get(i--), group_id));
//        while (j >= 0)
//            dataRows.add(new DataRow(gyrDatas.get(j--), group_id));
//
//        DataSupport.saveAll(dataRows);
//        int size = dataRows.size();
//        popMsg(ActionCollectActivity.POP_SUCCESS, size);
//    }


    private void popMsg(int what, int value){
        Message msg = new Message();
        msg.what = what;
        Bundle bundle = new Bundle();
        bundle.putInt("value", value);
        msg.setData(bundle);
        ActionCollectActivity.handler.sendMessage(msg);
    }
    private void popMsg(int what, float[] value){
        Message msg = new Message();
        msg.what = what;
        Bundle bundle = new Bundle();
        bundle.putFloatArray("value", value);
        msg.setData(bundle);
        ActionCollectActivity.handler.sendMessage(msg);
        Log.v("Msg","poped");
    }
}
