package com.wagner.ouendroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 3:55 PM
 */
public class Menu {
    private enum State { MAIN, FILES }

    private OpenGLRenderer parent;
    private State state = State.MAIN;
    private ArrayList<String> files;
    private ArrayList<Text> fileText;
    private int textureId;
    private boolean loadTexture = true;
    private Bitmap bitmap;
    private float vertices[];
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
    float textureCoordinates[] = {
                  0.0f,         0.0f,
                  0.0f, 0.833984375f,
              0.46875f, 0.833984375f,
              0.46875f,         0.0f
    };
    private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;
    private TwoStateButton songButton;
    private boolean songButtonPressed = false;
    private int selected = -1;

    public void unload() {
        bitmap.recycle();
        bitmap = null;
    }

    public Menu(OpenGLRenderer parent, Context context) {
        this.parent = parent;

        vertices = new float[] {
		                   0.0f,               0.0f, 0.0f, // 0, Top Left
		                   0.0f, parent.getHeight(), 0.0f, // 1, Bottom Left
		      parent.getWidth(), parent.getHeight(), 0.0f, // 2, Bottom Right
		      parent.getWidth(),               0.0f, 0.0f  // 3, Top Right
	    };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(vertices.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textureCoordinates);
		textureBuffer.position(0);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu, o);

        songButton = new TwoStateButton(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.select, o),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.select_p, o),
                parent.getWidth() / 2.0f, parent.getHeight() * 3.0f / 4.0f,
                285, 53, 0.556640625f, 0.103515625f);

    }

    public void draw(GL10 gl) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        drawBackground(gl);

        if (state == State.MAIN) {
            if (!parent.isTouchHandled() && songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())
                    && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP) {
                songButtonPressed = false;
                File rootDir = new File("/sdcard/");
                File [] filesArr = rootDir.listFiles(new FilenameFilter() {
                    public boolean accept(File file, String s) {
                        return s.endsWith(".oed");
                    }
                });
                files = new ArrayList<String>();
                fileText = new ArrayList<Text>();
                for (File f : filesArr) {
                    files.add(f.getName());
                }
                Collections.sort(files);
                float top = 2.0f;
                for (String f : files) {
                    fileText.add(new Text().setText(f.substring(0, f.length() - 4)).setX(2.0f).setY(top));
                    top += 24.0f;
                }

                state = State.FILES;
            } else if (!parent.isTouchHandled() && songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                songButtonPressed = true;
            } else if (!parent.isTouchHandled()&& !songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                songButtonPressed = false;
            }
            songButton.draw(gl, songButtonPressed);
        } else {
            if (!parent.isKeyHandled() && parent.getKeyEvent().getKeyCode() == KeyEvent.KEYCODE_BACK) {
                state = State.MAIN;
                parent.setKeyHandled();
            }

            if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN)
                selected = -1;

            for (int i = 0; i < files.size(); i++) {
                Text t = fileText.get(i);
                if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN &&
                        t.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY()))
                    selected = i;
                if (i == selected)
                    t.setBlue(0.0f).setRed(0.0f);
                else
                    t.setBlue(1.0f).setRed(1.0f);
                t.draw(gl);
            }

            if (selected != -1 && !parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP &&
                    fileText.get(selected).isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                String chosen = files.get(selected);
                parent.startGame("file:///sdcard/" + chosen.substring(0, chosen.length() - 3) + "mp3",
                        "/sdcard/" + chosen);
                state = State.MAIN;
            } else if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP) {
                selected = -1;
            }
        }

        parent.setTouchHandled();
    }

    private void drawBackground(GL10 gl) {
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    private void loadGLTexture(GL10 gl) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        textureId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
			    GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }

}
