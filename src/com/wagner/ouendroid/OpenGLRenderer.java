package com.wagner.ouendroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.*;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.text.TextPaint;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class OpenGLRenderer implements Renderer {
    private LinkedList<Button> buttons = new LinkedList<Button>();
    private LinkedList<Miss> misses = new LinkedList<Miss>();
    private int readerPos = 0;
    private MediaPlayer player = new MediaPlayer();
    FileReader reader = new FileReader();
    private float tapX = -1.0f;
    private float tapY = -1.0f;
    ArrayList<ButtonInfo> timesCoords = reader.getButtonInfoList("sdcard/airbrushed.txt");
    private Bitmap buttonTexture;
    private int score = 0;
    private int health = 100;
    private TextPaint textPaint = new TextPaint();

    public OpenGLRenderer(Context context) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        buttonTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.button, o);
        Text.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.numbers, o));
        Miss.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.miss, o));
        Uri songUri = Uri.parse("file:///sdcard/A_Airbrushed.mp3");
        try {
            player.setDataSource(context,songUri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textPaint.setColor(Color.argb(0, 255, 255, 255));
        textPaint.setTextSize(20.0f);


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

        int time = player.getCurrentPosition();

        while (readerPos < timesCoords.size()) {
            ButtonInfo info = timesCoords.get(readerPos);
            if (info.time - time < Config.RING_TIME) {
                readerPos++;
                buttons.add(new Button(buttonTexture, info));
            } else {
                break;
            }
        }

        // Handle Tap
        if (tapX >= 0.0f && buttons.size() > 0) {
            Button b = buttons.removeFirst();
            if (b.isHit(tapX, tapY) && b.scoreMultiplier(time) > 0) {
                score += Config.BUTTON_VALUE * b.scoreMultiplier(time);
                health += Config.HEALTH_PER_HIT;
                if (health > 100) health = 100;
            } else {
                misses.add(new Miss(b.getInfo().time + 2000, b.getInfo().x, b.getInfo().y));
                health -= Config.HEALTH_PER_MISS;
                if (health < 0) health = 0;
            }
        }

        // Handle Timeout
        if (buttons.size() > 0 && buttons.peek().getInfo().time - time < -Config.MAX_TIME_FOR_HIT) {
            Button b = buttons.removeFirst();
            misses.add(new Miss(b.getInfo().time + 2000, b.getInfo().x, b.getInfo().y));
            health -= Config.HEALTH_PER_MISS;
            if (health < 0) health = 0;
        }

        if (misses.size() > 0 && misses.peek().getTime() <= time) {
            misses.removeFirst();
        }

        for (Button b : buttons) {
            b.draw(gl, time);
        }

        for (Miss m : misses) {
            m.draw(gl);
        }

        float left = 10;
        for (char c : String.valueOf(health).toCharArray()) {
            int num = Integer.parseInt(String.valueOf(c));
            new Text(num == 0 ? 10 : num, left, 10.0f).draw(gl);
            left += 14.0f;
        }


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
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, width, height, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void stop() {
        player.stop();
    }
}
