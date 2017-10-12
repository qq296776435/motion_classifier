package gesture.jnu.motionclassifier;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.LinkedList;

public class ActionCollectActivity extends AppCompatActivity implements ServiceConnection {
    private Acts acts;
    private boolean hasStarted;
    public static UIHandler handler;

    static final int POP_SUCCESS = 7;
    static final int ACC_DATA = 8;
    static final int GYR_DATA = 9;

    private Button startButton;
//    private Button stopButton;
//    private Button accButton;
//    private Button gyrButton;
    private TextView gccXYZ;
    private TextView D;
    private TextView gravActual;
    private LineChart chart_acc,chart_gyr;

    private ActionCollectService.Binder myBinder;

    private LineDataSet acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z;
    private LineData linedata_acc,linedata_gyr;

    private final int[] RED = {R.color.Red};
    private final int[] GREEN = {R.color.Green};
    private final int[] BLUE = {R.color.Blue};

    private int acc_count, gyr_count;
    private static final int DISPLAY_SIZE = 1000;

    EditText title;
    EditText remainTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_collect_layout);

//        TextView title = (TextView)findViewById(R.id.action_collect_title_txt);
//        TextView remainTime = (TextView)findViewById(R.id.action_colect_remain_txt);

        title = (EditText)findViewById(R.id.action_collect_title_txt);
        remainTime = (EditText)findViewById(R.id.action_colect_remain_txt);
        startButton = (Button)findViewById(R.id.action_collect_start_btn);
//        stopButton = (Button)findViewById(R.id.action_collect_stop_btn);
//        accButton = (Button)findViewById(R.id.action_colect_acc_btn);
//        gyrButton = (Button)findViewById(R.id.action_colect_gyr_btn);
        gccXYZ =(TextView)findViewById(R.id.accXYZ);
        D=(TextView)findViewById(R.id.gravUnit);
        gravActual=(TextView)findViewById(R.id.grav_actual);
        chart_acc = (LineChart)findViewById(R.id.realtime_chart_acc);
        chart_gyr = (LineChart)findViewById(R.id.realtime_chart_gyr);

        handler = new UIHandler();
        Intent intent = getIntent();
//        acts = (Acts)intent.getSerializableExtra("acts");
//
//        title.setText(acts.getAction_name());
//        remainTime.setText(String.valueOf(acts.getDuration()));
        hasStarted = false;

        acc_x = new LineDataSet(new LinkedList<Entry>(), "acc_x");
        acc_x.setColors(RED, getApplicationContext());
        acc_x.setCircleColors(RED, getApplicationContext());
        acc_x.setCircleRadius(1f);

        acc_y = new LineDataSet(new LinkedList<Entry>(), "acc_y");
        acc_y.setColors(GREEN, getApplicationContext());
        acc_y.setCircleColors(GREEN, getApplicationContext());
        acc_y.setCircleRadius(1f);

        acc_z = new LineDataSet(new LinkedList<Entry>(), "acc_z");
        acc_z.setColors(BLUE, getApplicationContext());
        acc_z.setCircleColors(BLUE, getApplicationContext());
        acc_z.setCircleRadius(1f);

        gyr_x = new LineDataSet(new LinkedList<Entry>(), "gyr_x");
        gyr_x.setColors(RED, getApplicationContext());
        gyr_x.setCircleColors(RED, getApplicationContext());
        gyr_x.setCircleRadius(1f);

        gyr_y = new LineDataSet(new LinkedList<Entry>(), "gyr_y");
        gyr_y.setColors(GREEN, getApplicationContext());
        gyr_y.setCircleColors(GREEN, getApplicationContext());
        gyr_y.setCircleRadius(1f);

        gyr_z = new LineDataSet(new LinkedList<Entry>(), "gyr_z");
        gyr_z.setColors(BLUE, getApplicationContext());
        gyr_z.setCircleColors(BLUE, getApplicationContext());
        gyr_z.setCircleRadius(1f);

        acc_count = 0;
        gyr_count = 0;

        linedata_acc = new LineData();
        linedata_gyr = new LineData();

        YAxis yAxis_acc = chart_acc.getAxisLeft();
        yAxis_acc.setAxisMaximum(10f);
        yAxis_acc.setAxisMinimum(-10f);
        chart_acc.setData(linedata_acc);

        YAxis yAxis_gyr = chart_gyr.getAxisLeft();
        yAxis_gyr.setAxisMaximum(10f);
        yAxis_gyr.setAxisMinimum(-10f);
        chart_gyr.setData(linedata_gyr);

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStart();
            }
        });
//        stopButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v){
//                handleStop();
//            }
//        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    class UIHandler extends Handler {

        @Override
        public void handleMessage(Message msg){
            Log.v("handleMessage","enter");
            switch (msg.what) {
                case POP_SUCCESS:
                    int sizes = msg.getData().getInt("value");
                    Toast successToast = Toast.makeText(ActionCollectActivity.this, "Collect Success!\n"+sizes, Toast.LENGTH_LONG);
                    successToast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastView = (LinearLayout)successToast.getView();
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setImageResource(R.drawable.tick);
                    toastView.addView(imageView, 0);
                    successToast.show();
                    acts.save();
                    unbind();
                    break;
                case ACC_DATA:
                    float[] acc = msg.getData().getFloatArray("value"),gravUnit;
                    if(acc_x.getEntryCount() == DISPLAY_SIZE) {
                        acc_x.removeFirst();
                        acc_y.removeFirst();
                        acc_z.removeFirst();
                    }
//                    Log.v("acc",""+acc[0]+acc[1]+acc[2]);
                    gccXYZ.setText(""+acc[0]+acc[1]+acc[2]);
                    double norm=Math.sqrt(acc[0]*acc[0]+acc[1]*acc[1]+acc[2]*acc[2]);
                    gravUnit=acc;
                    gravUnit[0]/=norm;
                    gravUnit[1]/=norm;
                    gravUnit[2]/=norm;
//                    Log.v("D",""+gravUnit[0]+gravUnit[1]+gravUnit[2]);
                    D.setText(""+gravUnit[0]+gravUnit[1]+gravUnit[2]);
                    float[] grav_actual = new float[3];
                    grav_actual[0]=acc[0]*gravUnit[0];
                    grav_actual[1]=acc[1]*gravUnit[1];
                    grav_actual[2]=acc[2]*gravUnit[2];
//                    Log.v("grav_actual",""+grav_actual[0]+grav_actual[1]+grav_actual[2]);
                    gravActual.setText(""+grav_actual[0]+grav_actual[1]+grav_actual[2]);
                    // acc_x.addEntry(new Entry(acc_count, acc[0]));
                    // acc_y.addEntry(new Entry(acc_count, acc[1]));
                    // acc_z.addEntry(new Entry(acc_count++, acc[2]));
                    acc_x.addEntry(new Entry(acc_count, grav_actual[0]));
                    acc_y.addEntry(new Entry(acc_count, grav_actual[1]));
                    acc_z.addEntry(new Entry(acc_count++, grav_actual[2]));
                    linedata_acc.notifyDataChanged();
                    chart_acc.notifyDataSetChanged();
                    chart_acc.invalidate();
                    break;
                case GYR_DATA:
                    float[] gyr = msg.getData().getFloatArray("value");
                    if(gyr_x.getEntryCount() == DISPLAY_SIZE) {
                        gyr_x.removeFirst();
                        gyr_y.removeFirst();
                        gyr_z.removeFirst();
                    }
                    Log.v("gyr",""+gyr[0]+gyr[1]+gyr[2]);
                    gyr_x.addEntry(new Entry(gyr_count, gyr[0]));
                    gyr_y.addEntry(new Entry(gyr_count, gyr[1]));
                    gyr_z.addEntry(new Entry(gyr_count++, gyr[2]));
                    linedata_gyr.notifyDataChanged();
                    chart_gyr.notifyDataSetChanged();
                    chart_gyr.invalidate();
                    break;
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        myBinder = (ActionCollectService.Binder)iBinder;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName){
        myBinder = null;
    }

    private void unbind(){
        unbindService(this);
        this.finish();
    }

    private void handleStart() {
        if (!hasStarted){
            hasStarted = true;
            startButton.setClickable(false);

            Intent startIntent = new Intent(this, ActionCollectService.class);
//            startIntent.putExtra("action_duration", acts.getDuration());
//            startIntent.putExtra("group_id", acts.getGroup_id());
            startIntent.putExtra("action_duration", Integer.parseInt(remainTime.getText().toString()));
            startIntent.putExtra("group_id", title.getText());
            startService(startIntent);
            bindService(startIntent, this, Context.BIND_AUTO_CREATE);
            chartActivate();
            Toast.makeText(ActionCollectActivity.this, "Start Collecting...", Toast.LENGTH_SHORT).show();
        }
    }
    private void chartActivate() {
        if(linedata_acc.getDataSetCount() == 3) {
            linedata_acc.removeDataSet(0);
            linedata_acc.removeDataSet(0);
            linedata_acc.removeDataSet(0);
        }
        linedata_acc.addDataSet(acc_x);
        linedata_acc.addDataSet(acc_y);
        linedata_acc.addDataSet(acc_z);
        chart_acc.notifyDataSetChanged();

        if(linedata_gyr.getDataSetCount() == 3){
        linedata_gyr.removeDataSet(0);
        linedata_gyr.removeDataSet(0);
        linedata_gyr.removeDataSet(0);
        }
        linedata_gyr.addDataSet(gyr_x);
        linedata_gyr.addDataSet(gyr_y);
        linedata_gyr.addDataSet(gyr_z);
        chart_gyr.notifyDataSetChanged();
    }


//    private void handleStop() {
//        if (hasStarted && myBinder != null){
//            hasStarted = false;
//            myBinder.cancel();
//            Intent stopIntent = new Intent(this, ActionCollectService.class);
//            stopService(stopIntent);
//            Toast.makeText(ActionCollectActivity.this, "Collect Cancel", Toast.LENGTH_SHORT).show();
//            this.finish();
//
//        }
//        Toast.makeText(this,"Not start yet!",Toast.LENGTH_SHORT).show();
//    }
}
