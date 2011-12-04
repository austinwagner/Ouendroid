package com.wagner.ouendroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.*;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.text.TextPaint;

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
    private Game game = new Game();
    private Menu menu = new Menu();
    private Context context;

    private float tapX = -1.0f;
    private float tapY = -1.0f;

    public OpenGLRenderer(Context context) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Button.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.buttons, o));
        Character.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.characters, o));
        Miss.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.miss, o));
        this.context = context;

        game.initialize(context, "file:///sdcard/A_Airbrushed.mp3", "sdcard/airbrushed.txt");
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

    public void setTap(float x, float y) {
        tapX = x;
        tapY = y;
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

        } else if (state == State.GAME) {
            game.draw(gl);
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
    }

    public void stop() {
        game.stop();
        Button.unload();
        Character.unload();
        Miss.unload();
    }
}
