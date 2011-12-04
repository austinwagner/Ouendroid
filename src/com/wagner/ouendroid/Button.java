package com.wagner.ouendroid;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:16 AM
 */

public class Button {
    private static final int SEGMENTS = 40;
    private static final float BUTTON_SIZE = Config.BUTTON_SIZE; // Just to make the vertices definition cleaner

    private static Bitmap bitmap;
    private static int textureId;
    private static boolean loadTexture = true;
    private ButtonInfo info;

	private float vertices[] = {
		      -BUTTON_SIZE / 2, -BUTTON_SIZE / 2, 0.0f,  // 0, Top Left
		      -BUTTON_SIZE / 2,  BUTTON_SIZE / 2, 0.0f,  // 1, Bottom Left
		       BUTTON_SIZE / 2,  BUTTON_SIZE / 2, 0.0f,  // 2, Bottom Right
		       BUTTON_SIZE / 2, -BUTTON_SIZE / 2, 0.0f,  // 3, Top Right
	};

	private short[] indices = { 0, 1, 2, 0, 2, 3 };

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    public static void initialize(Bitmap b) {
        bitmap = b;
        loadTexture = true;
    }

	public Button(ButtonInfo info) {
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

        this.info = info;
    }

    public boolean isHit(float tapX, float tapY) {
        if((tapX - info.x)*(tapX - info.x) + (tapY - info.y)*(tapY - info.y) < 32 * 32)
            return true;
        else
            return false;
    }

    public ButtonInfo getInfo() {
       return info;
    }

	/**
	 * This function draws our square on screen.
	 * @param gl
	 */
	public void draw(GL10 gl, int time) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glPushMatrix();
        gl.glTranslatef(info.x, info.y, 0);
        drawButton(gl);
        drawRing(gl, time);
        gl.glPopMatrix();
	}

    private void drawButton(GL10 gl) {
        float textureCoordinates[] = {
              0.125f * (info.number - 1), 0.125f * (info.color),
              0.125f * (info.number - 1), 0.125f * (info.color + 1),
              0.125f * (info.number), 0.125f * (info.color + 1),
              0.125f * (info.number), 0.125f * (info.color)
        };
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(textureCoordinates);
        textureBuffer.position(0);

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDepthMask(false);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_BLEND);
        gl.glDepthMask(true);
    }

    private void drawRing(GL10 gl, int time) {
        float delta = info.time - time;
        float radius;

        if (delta > 0) {
            radius = (delta / Config.RING_TIME * Config.RING_RADIUS + BUTTON_SIZE / 2);
        } else {
            radius = (1-(-delta / Config.MAX_TIME_FOR_HIT)) * (BUTTON_SIZE / 4) + (BUTTON_SIZE / 4);
        }

        gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
        ByteBuffer vbb = ByteBuffer.allocateDirect(SEGMENTS * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        for (float i = 0; i < 360.0f; i+=(360.0f/SEGMENTS))
        {
            vertexBuffer.put((float)Math.cos(degreesToRadian(i))*radius);
            vertexBuffer.put((float)Math.sin(degreesToRadian(i))*radius);
        }

        vertexBuffer.position(0);

        gl.glEnable (GL10.GL_BLEND);
        gl.glBlendFunc (GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable (GL10.GL_LINE_SMOOTH);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glLineWidth(1.5f);
		gl.glVertexPointer (2, GL10.GL_FLOAT , 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, SEGMENTS);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_BLEND);
    }

    public float scoreMultiplier(int time) {
        float delta = Math.abs(time - info.time);
        if (delta > Config.MAX_TIME_FOR_HIT)
            return 0.0f;
        else
            return 1.0f - (delta / Config.MAX_TIME_FOR_HIT);
    }

    private float degreesToRadian(float angle) {
        return angle * (float)Math.PI / 180.0f;
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

    public static void unload() {
        bitmap.recycle();
        bitmap = null;
        loadTexture = true;
    }
}
