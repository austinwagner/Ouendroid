package com.wagner.ouendroid;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 10:55 AM
 */
public class Ouendroid extends Activity{
    private OpenGLRenderer renderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GLSurfaceView view = new GLSurfaceView(this);
        renderer = new OpenGLRenderer(this);
        view.setRenderer(renderer);
        setContentView(view);

        // load music
        // load notefile
        // play music
        // read notefile line by line


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            renderer.setTap(event.getX(), event.getY());
        return true;
    }
}

