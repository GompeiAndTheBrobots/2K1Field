package com.example.ikeandmike.field;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by peter on 11/8/15.
 */
public class Loggers implements ViewTreeObserver.OnScrollChangedListener {

    private TextView statusText, debugText;
    private ScrollView statusView, debugView;
    private static final int RATE_SWIPE_UP = -15;
    private static final int RATE_SWIPE_DOWN = 35;
    private int lastStatusY = 0, lastDebugY = 0;
    private boolean autoScrollDebugToBottom = true;
    private boolean autoScrollStatusToBottom = true;

    public Loggers(Activity a) {
        debugText = (TextView) a.findViewById(R.id.debug);
        statusText = (TextView) a.findViewById(R.id.status);
        statusView = (ScrollView) a.findViewById(R.id.statusView);
        debugView = (ScrollView) a.findViewById(R.id.debugView);

        statusView.getViewTreeObserver().addOnScrollChangedListener(this);
        debugView.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    @Override
    public void onScrollChanged() {
        int statusY = statusView.getScrollY();
        int debugY = debugView.getScrollY();
        int debugDY = debugY - lastDebugY;
        int statusDY = statusY - lastStatusY;

        if (statusDY < RATE_SWIPE_UP) {
            autoScrollStatusToBottom = false;
        } else if (statusDY > RATE_SWIPE_DOWN) {
            autoScrollStatusToBottom = true;
        }

        if (debugDY < RATE_SWIPE_UP) {
            autoScrollDebugToBottom = false;
        } else if (debugDY > RATE_SWIPE_DOWN) {
            autoScrollDebugToBottom = true;
        }

        lastStatusY = statusY;
        lastDebugY = debugY;
    }

    public void appendStatus(String status) {
        statusText.append(status + "\n");
        if (autoScrollStatusToBottom) {
            statusView.fullScroll(View.FOCUS_DOWN);
        }
    }

    public void appendDebug(String debug) {
        debugText.append(debug + "\n");
        if (autoScrollDebugToBottom) {
            debugView.fullScroll(View.FOCUS_DOWN);
        }
    }
}
