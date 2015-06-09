package ca.sapphire.peak8;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView timerTextView;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    MediaPlayer mpStart = new MediaPlayer();
    MediaPlayer mpStop = new MediaPlayer();
    MediaPlayer mpEnd = new MediaPlayer();

    NumberPicker reps, peak, rest;



    Runnable timerRunnable = new Runnable() {


        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int tenths = (int) (millis / 100);
            tenths = tenths % 10;

            int seconds = (int) (millis / 1000);
            int total_seconds = seconds;
            int minutes = seconds / 60;
            seconds = seconds % 60;

            int workout_time = reps.getValue() * ( peak.getValue() + rest.getValue() ) + rest.getValue();

            int work_min = (int) (workout_time / 60);
            int work_sec = workout_time - (work_min * 60);

//            timerTextView.setText(String.format("%d:%02d.%1d", minutes, seconds, tenths));
            timerTextView.setText(String.format("%d:%02d", work_min, work_sec));

            
            if( total_seconds == 10 )
                mpStart.start();

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = (TextView) findViewById(R.id.timerTextView);

        Button b = (Button) findViewById(R.id.button);
        b.setText("Start Workout");

        mpStart = MediaPlayer.create(this, R.raw.time_on);
        mpStop = MediaPlayer.create(this, R.raw.time_off);
        mpEnd = MediaPlayer.create(this, R.raw.time_off);

        reps = (NumberPicker) findViewById(R.id.reps_picker);
        reps.setMinValue(1);
        reps.setMaxValue(15);
        reps.setValue(8);
        reps.setWrapSelectorWheel(false);

        peak = (NumberPicker) findViewById(R.id.peak_picker);
        peak.setMinValue(15);
        peak.setMaxValue(45);
        peak.setValue(30);
        peak.setWrapSelectorWheel(false);

        rest = (NumberPicker) findViewById(R.id.rest_picker);
        rest.setMinValue(60);
        rest.setMaxValue(120);
        rest.setValue(90);
        rest.setWrapSelectorWheel(false);

        timerHandler.postDelayed(timerRunnable, 0);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Button b = (Button) v;
                if (b.getText().equals("Pause")) {
                    mpStart.start();
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("Resume");
                } else {
                    mpStop.start();
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    b.setText("Pause");
                }
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.button);
        b.setText("start");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
