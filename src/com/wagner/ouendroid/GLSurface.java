package com.wagner.ouendroid;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * The surface that holds the {@link OpenGLRenderer OpenGLRenderer}. Passes touch and keypress actions down to the
 * renderer.
 */

public class GLSurface extends GLSurfaceView {
    private OpenGLRenderer renderer;
    private Context context;


    public GLSurface(Context context) {
        super(context);
        this.context = context;
    }

    public void start(Activity parent) {
        renderer = new OpenGLRenderer(context, parent);
        setRenderer(renderer);
    }

    public void stop() {
        queueEvent(new Runnable() {
            public void run() {
                renderer.unload();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        queueEvent(new TouchRunnable(event));
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        queueEvent(new KeyRunnable(event));
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        queueEvent(new KeyRunnable(event));
        return true;
    }

    private class TouchRunnable implements Runnable {
        private MotionEvent motionEvent;
        public TouchRunnable(MotionEvent event) {
            motionEvent = event;
        }

        public void run() {
            renderer.handleTouch(motionEvent);
        }
    }

    private class KeyRunnable implements Runnable {
        private KeyEvent keyEvent;
        public KeyRunnable(KeyEvent event) {
            keyEvent = event;
        }

        public void run() {
            renderer.handleKey(keyEvent);
        }
    }
}
