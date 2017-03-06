package edu.wpi.rbe2001.field;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

class Loggers implements ViewTreeObserver.OnScrollChangedListener {

    private TextView statusText, debugText;
    private ScrollView infoScroll;
    private static final int RATE_SWIPE_UP = -15;
    private static final int RATE_SWIPE_DOWN = 35;
    private int lastInfoY = 0;
    private boolean autoScrollToBottom = true;

    Loggers(Activity a) {
        statusText = (TextView) a.findViewById(R.id.statusView);
        debugText = (TextView) a.findViewById(R.id.debugView);
        infoScroll = (ScrollView) a.findViewById(R.id.info_scroll);

        infoScroll.getViewTreeObserver().addOnScrollChangedListener(this);
    }

    @Override
    public void onScrollChanged() {
        int infoY = infoScroll.getScrollY();
        int statusDY = infoY - lastInfoY;

        if (statusDY < RATE_SWIPE_UP) {
            autoScrollToBottom = false;
        } else if (statusDY > RATE_SWIPE_DOWN) {
            autoScrollToBottom = true;
        }

        lastInfoY = infoY;
    }

    void appendStatus(String status) {
        statusText.append(status + "\n");
        if (autoScrollToBottom) {
            infoScroll.fullScroll(View.FOCUS_DOWN);
        }
    }

    void appendDebug(String debug) {
        debugText.append(debug + "\n");
        if (autoScrollToBottom) {
            infoScroll.fullScroll(View.FOCUS_DOWN);
        }
    }
}
