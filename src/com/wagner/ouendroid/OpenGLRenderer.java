package com.wagner.ouendroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class OpenGLRenderer implements Renderer {

    private LinkedList<Button> buttons = new LinkedList<Button>();
    private int readerPos = 0;
    private int groupPos = 1;
    private MediaPlayer player = new MediaPlayer();
    FileReader reader = new FileReader();
    private float tapX = -1.0f;
    private float tapY = -1.0f;
    ArrayList<ButtonInfo> timesCoords = reader.getButtonInfoList("sdcard/airbrushed.txt");
    private Bitmap buttonTexture;

    public OpenGLRenderer(Context context) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        buttonTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.button, o);

        Uri songUri = Uri.parse("file:///sdcard/A_Airbrushed.mp3");
        try {
            player.setDataSource(context,songUri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // Set the background color to black ( rgba ).
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);  // OpenGL docs.
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);// OpenGL docs.
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);// OpenGL docs.
        // Enables depth testing.
        gl.glEnable(GL10.GL_DEPTH_TEST);// OpenGL docs.
        // The type of depth testing to do.
        gl.glDepthFunc(GL10.GL_LEQUAL);// OpenGL docs.
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, // OpenGL docs.
                GL10.GL_NICEST);
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

    int time = player.getCurrentPosition();
    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | // OpenGL docs.
                GL10.GL_DEPTH_BUFFER_BIT);

        //int time = player.getCurrentPosition();

        while (readerPos < timesCoords.size()) {
            ButtonInfo info = timesCoords.get(readerPos);
            readerPos++;
            if (info.time - time < 2000) {
                buttons.add(new Button(buttonTexture, info));
            } else {
                break;
            }
        }

        if (tapX >= 0.0f && buttons.size() > 0) {
            if (buttons.getFirst().isHit(tapX, tapY))
                buttons.getFirst().toggle();
            else
                buttons.removeFirst();
        }

        for (Button b : buttons) {
            b.draw(gl, time);
        }


//        if (r.getRadius() < 32.0f)
//            r.setRadius(45.0f);
//        else
//            r.setRadius(r.getRadius() - 1.0f);
//
//        r.draw(gl);

        tapX = -1.0f;
        tapY = -1.0f;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
          * microedition.khronos.opengles.GL10, int, int)
      */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);// OpenGL docs.
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
        // Reset the projection matrix
        gl.glLoadIdentity();// OpenGL docs.
        // Calculate the aspect ratio of the window
        GLU.gluOrtho2D(gl, 0, width, height, 0);

//        GLU.gluPerspective(gl, 45.0f,
//                (float) width / (float) height,
//                0.1f, 100.0f);
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
        // Reset the modelview matrix
        gl.glLoadIdentity();// OpenGL docs.
    }
}
