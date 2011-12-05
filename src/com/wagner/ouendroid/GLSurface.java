package com.wagner.ouendroid;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
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
        queueEvent(new KeyRunnable(keyCode, event));
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        queueEvent(new KeyRunnable(keyCode, event));
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
        private int keyCode;
        public KeyRunnable(int code, KeyEvent event) {
            keyEvent = event;
            keyCode = code;
        }

        public void run() {
            renderer.handleKey(keyCode, keyEvent);
        }
    }
}
