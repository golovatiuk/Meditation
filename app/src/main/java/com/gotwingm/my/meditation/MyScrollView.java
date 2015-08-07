package com.gotwingm.my.meditation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Custom ScrollView class which provide correct work with ViewFlipper as a child view
 */
public class MyScrollView extends ScrollView {

    /** Actual distance between motion's X coordinates */
    private float xDistance;

    /** Actual distance between motion's X coordinates */
    private float yDistance;

    /** start motion's X coordinate */
    private float fromX;

    /** start motion's Y coordinate */
    private float fromY;


    /** Default constructor */
    public MyScrollView(Context context) {
        super(context);
    }

    /** Default constructor */
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Default constructor */
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Method JUST determines whether we want to intercept the motion.
     *
     * @param ev MotionEvent
     * @return true where we want to intercept the vertical scrolling motion,
     *         false - do not intercept touch event, let the child ViewFlipper handle it
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xDistance = yDistance = 0;
                    fromX = ev.getX();
                    fromY = ev.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    final float curX = ev.getX();
                    final float curY = ev.getY();
                    xDistance += Math.abs(curX - fromX);
                    yDistance += Math.abs(curY - fromY);
                    fromX = curX;
                    fromY = curY;
                    if(xDistance > yDistance)
                        return false;
            }
        return super.onInterceptTouchEvent(ev);
    }
}
