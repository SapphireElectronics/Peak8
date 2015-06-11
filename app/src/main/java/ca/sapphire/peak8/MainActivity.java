package ca.sapphire.peak8;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    TextView segment_text, elapsed_text, total_text;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    MediaPlayer mpStart = new MediaPlayer();
    MediaPlayer mpStop = new MediaPlayer();
    MediaPlayer mpEnd = new MediaPlayer();
    MediaPlayer mpClick = new MediaPlayer();

    NumberPicker reps, peak, rest;

    ProgressBar mainProg, segmentProg;

    Button modeButton;

    peak8 pk8;

    Runnable timerRunnable = new Runnable() {


        @Override
        public void run() {
            pk8.update();

            if( pk8.modeChanged ) {
                pk8.modeChanged = false;
                switch( pk8.mode ) {
                    case START:
                        modeButton.setText("Waiting to start ...");
                        break;
                    case WARMUP:
                        pk8.setValues(reps.getValue(), peak.getValue(), rest.getValue());
                        mpStart.start();
                        modeButton.setText("Warm up");
                        modeButton.setBackgroundColor( 0xff0000ff );
                        break;
                    case RUN:
                        mpStart.start();
                        modeButton.setText("Peak");
                        modeButton.setBackgroundColor(0xffff0000);
                        break;
                    case REST:
                        mpStop.start();
                        modeButton.setText("Rest");
                        modeButton.setBackgroundColor(0xff00ff00);
                        break;
                    case COOLDOWN:
                        mpStop.start();
                        modeButton.setText("Cool down");
                        modeButton.setBackgroundColor(0xff0000ff);
                        break;
                    case END:
                        mpEnd.start();
                        modeButton.setText("Finished");
                        pk8.stop();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        break;
                }
            }

            segment_text.setText(String.format("Segment: %d", pk8.getSegmentTime()));
            elapsed_text.setText(String.format("Elapsed: %d:%02d", pk8.getElapsedMins(), pk8.getElapsedSecs()));
            total_text.setText(String.format("Total: %d:%02d", pk8.getTotalMins(), pk8.getTotalSecs()));

            mainProg.setMax(pk8.getTotalTime());
            mainProg.setProgress(pk8.getElapsedTime());

            segmentProg.setMax((int) pk8.segmentLength / 1000);
            segmentProg.setProgress( pk8.getSegmentTime() );

            if( pk8.isRunning )
                timerHandler.postDelayed(this, 500);
            else
                timerHandler.removeCallbacks(timerRunnable);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView segment_textView, elapsed_textView, total_textView;

        segment_text = (TextView) findViewById(R.id.segment_textView);
        elapsed_text = (TextView) findViewById(R.id.elapsed_textView);
        total_text = (TextView) findViewById(R.id.total_textView);

        Button b = (Button) findViewById(R.id.button);
        b.setText("Start Workout");

        mpStart = MediaPlayer.create(this, R.raw.time_on);
        mpStop = MediaPlayer.create(this, R.raw.time_off);
        mpEnd = MediaPlayer.create(this, R.raw.done);
        mpClick = MediaPlayer.create(this, R.raw.click);

        reps = (NumberPicker) findViewById(R.id.reps_picker);
        reps.setMinValue(1);
        reps.setMaxValue(15);
        reps.setValue(8);
        reps.setWrapSelectorWheel(false);

        peak = (NumberPicker) findViewById(R.id.peak_picker);
        peak.setMinValue(3);
        peak.setMaxValue(45);
        peak.setValue(30);
        peak.setWrapSelectorWheel(false);

        rest = (NumberPicker) findViewById(R.id.rest_picker);
        rest.setMinValue(5);
        rest.setMaxValue(120);
        rest.setValue(90);
        rest.setWrapSelectorWheel(false);

        modeButton = (Button) findViewById(R.id.mode_button);

        mainProg = (ProgressBar) findViewById(R.id.main_progress);
        segmentProg = (ProgressBar) findViewById(R.id.segment_progress);

        pk8 = new peak8(reps.getValue(), peak.getValue(), rest.getValue());
        pk8.update();

        timerHandler.postDelayed(timerRunnable, 0);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mpClick.start();
                Button b = (Button) v;
                if( pk8.isRunning) {
                    pk8.stop();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("Resume workout");
                }
                else {
                    pk8.start();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        b.setText("Resume");
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
