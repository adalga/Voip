package searchrescue.voip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.calling.Call;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends AppCompatActivity {


    private Timer timer;
    private UpdateCallDurationTask durationTask;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }
    private String callId;
    private long callStart = 0;
    private Call call = CurrentCall.currentCall;
    private TextView callDuration;
    private TextView callState;
    private TextView callerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        callDuration = (TextView) findViewById(R.id.callDuration);
        callerName = (TextView) findViewById(R.id.remoteUser);
        callState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);

        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        callStart = System.currentTimeMillis();
        callId = getIntent().getStringExtra("CALL_ID");
        callerName.setText(call.getRemoteUserId());
        callState.setText(call.getState().toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        durationTask.cancel();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        durationTask = new UpdateCallDurationTask();
        timer.schedule(durationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (callStart > 0) {
            callDuration.setText(formatTimespan(System.currentTimeMillis() - callStart));
        }
    }
}
