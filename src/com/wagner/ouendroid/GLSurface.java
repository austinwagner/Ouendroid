package com.wagner.ouendroid;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 11:03 AM
 */

public class GLSurface extends GLSurfaceView {
    private OpenGLRenderer renderer;
    private Context context;


    public GLSurface(Context context) {
        super(context);
        this.context = context;
    }

    public void start() {
        renderer = new OpenGLRenderer(context);
        setRenderer(renderer);
    }

    public void stop() {
        queueEvent(new Runnable() {
            public void run() {
                renderer.stop();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            queueEvent(new TapRunnable(event.getX(), event.getY()));
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class TapRunnable implements Runnable {
        private float x, y;
        public TapRunnable(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void run() {
            renderer.setTap(x, y);
        }
    }
}
