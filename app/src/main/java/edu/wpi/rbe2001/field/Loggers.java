package edu.wpi.rbe2001.field;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

class Loggers implements ViewTreeObserver.OnScrollChangedListener, View.OnLongClickListener {

    private TextView logText;
    private ScrollView infoScroll;
    private static final int RATE_SWIPE_UP = -15;
    private static final int RATE_SWIPE_DOWN = 35;
    private int lastInfoY = 0;
    private boolean autoScrollToBottom = true;

    Loggers(Activity a) {
        logText = (TextView) a.findViewById(R.id.logView);
        infoScroll = (ScrollView) a.findViewById(R.id.info_scroll);
        logText.setOnLongClickListener(this);

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

    void append(String msg) {
        logText.append(msg + "\n");
        if (autoScrollToBottom) {
            infoScroll.fullScroll(View.FOCUS_DOWN);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        logText.setText("");
        return true;
    }
}
