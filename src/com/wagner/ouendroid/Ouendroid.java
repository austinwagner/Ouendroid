package com.wagner.ouendroid;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 10:55 AM
 */
public class Ouendroid extends Activity{
    private GLSurface view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new GLSurface(this);
        setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.start(this);
    }

    @Override
    protected void onPause() {
        view.stop();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        view.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            return false;
        return view.onKeyDown(keyCode, event);
    }
}

