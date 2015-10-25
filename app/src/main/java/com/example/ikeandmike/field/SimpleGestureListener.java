package com.example.ikeandmike.field;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by peter on 10/25/15.
 */
public class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {

    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    private Context context;
    private Class goTo;
    private int direction;

    SimpleGestureListener(Context context, Class goTo, int direction) {
        this.context = context;
        this.goTo = goTo;
        this.direction = direction;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        if (direction == LEFT){
            if (velocityX > 500) {
                Intent intent = new Intent(context, goTo);
                context.startActivity(intent);
            }
        }
        else if (direction == RIGHT){
            if (velocityX < -500) {
                Intent intent = new Intent(context, goTo);
                context.startActivity(intent);
            }
        }
        return true;
    }
}

