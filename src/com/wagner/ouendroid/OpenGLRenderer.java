package com.wagner.ouendroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class OpenGLRenderer implements Renderer {
    private enum State { MENU, GAME }
    private State state = State.MENU;
    private Game game;
    private Menu menu;
    private Context context;

    private int width, height;
    private boolean keyHandled = true;
    private boolean touchHandled = true;
    private int keyCode;
    private KeyEvent keyEvent;
    private MotionEvent touchEvent;
    private Activity parent;

    public OpenGLRenderer(Context context, Activity parent) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Button.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.buttons, o));
        Character.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.characters, o));
        Miss.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.miss, o));
        this.context = context;
        this.parent = parent;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
         * microedition.khronos.opengles.GL10, javax.microedition.khronos.
         * egl.EGLConfig)
	 */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    public void handleTouch(MotionEvent event) {
        touchEvent = event;
        touchHandled = false;
    }

    public void handleKey(int code, KeyEvent event) {
        keyCode = code;
        keyEvent = event;
        keyHandled = false;
    }

    public MotionEvent getTouchEvent() {
        return touchEvent;
    }

    public boolean isTouchHandled() {
        return touchHandled;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    public boolean isKeyHandled() {
        return keyHandled;
    }

    public void setKeyHandled() {
        keyHandled = true;
    }

    public void setTouchHandled() {
        touchHandled = true;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
          * microedition.khronos.opengles.GL10)
      */
    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (state == State.MENU) {
            menu.draw(gl);
            if (!keyHandled && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                parent.finish();
                keyHandled = true;
            }
        } else if (state == State.GAME) {
            game.draw(gl);
            if (!keyHandled && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                game.stop();
                state = State.MENU;
                keyHandled = true;
            }
        }


    }

    /*
      * (non-Javadoc)
      *
      * @see
      * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
          * microedition.khronos.opengles.GL10, int, int)
      */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, width, height, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        this.width = width;
        this.height = height;

        menu = new Menu(this, context);
        game = new Game(this, context);
    }

    public void startGame(String song, String chart) {
        game.initialize(context, song, chart);
        state = State.GAME;
    }
    public void unload() {
        game.stop();
        game.unload();
        Button.unload();
        Character.unload();
        Miss.unload();
        menu.unload();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
