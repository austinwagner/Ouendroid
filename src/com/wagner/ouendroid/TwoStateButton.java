package com.wagner.ouendroid;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Loads two equal size images and allows you to switch between them by setting the pressed state
 * when rendering. Also provides a convenience method for determining if a click was inside this object's
 * hitbox.
 */
public class TwoStateButton {

    private int normalId;
    private int pressedId;
    private boolean loadTexture = true;
    private Bitmap normal;
    private Bitmap pressed;
    private float vertices[];
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
    private float x;
    private float y;
    private int w;
    private int h;

    private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    /**
     * @param normal The image to display normally (i.e. the unpressed state)
     * @param pressed The image to display when the button is pressed
     * @param x The horizontal position of the center.
     * @param y The vertical position of the center.
     * @param width The width to draw the button.
     * @param height The height to draw the button.
     * @param texFactW The location of the edge of the texture. Calcualted as (width of button image / width of texture)
     * @param texFactH The location of the edge of the texture. Calcualted as (height of button image / height of texture)
     */
    public TwoStateButton(Bitmap normal, Bitmap pressed, float x, float y, int width, int height, float texFactW, float texFactH) {
        vertices = new float[] {
		     -width / 2, -height / 2, 0.0f, // 0, Top Left
		     -width / 2,  height / 2, 0.0f, // 1, Bottom Left
		      width / 2,  height / 2, 0.0f, // 2, Bottom Right
		      width / 2, -height / 2, 0.0f  // 3, Top Right
	    };

        float textureCoordinates[] = {
                  0.0f,     0.0f,
                  0.0f, texFactH,
              texFactW, texFactH,
              texFactW,     0.0f
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

        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textureCoordinates);
		textureBuffer.position(0);

        this.normal = normal;
        this.pressed = pressed;
        this.x = x;
        this.y = y;
        w = width;
        h = height;
    }

    /**
     * Draws the button to the screen.
     * @param gl The OpenGL instance to draw to.
     * @param pressed The state to be drawn
     */
    public void draw(GL10 gl, boolean pressed) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, pressed ? pressedId : normalId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }

    /**
     * Loads the bitmaps as an OpenGL texture.
     * @param gl The instance of OpenGL to load the texture to.
     */
    private void loadGLTexture(GL10 gl) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        normalId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, normalId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
			    GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, normal, 0);

        textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        pressedId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, pressedId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
			    GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, pressed, 0);
    }

    public boolean isHit(float hitX, float hitY) {
        return (hitX <= x + w / 2 && hitX >= x - w / 2 && hitY <= y + h / 2 && hitY >= y - h / 2);
    }

    /**
     * Removes the bitmaps loaded by this instance.
     */
    public void unload() {
        normal.recycle();
        normal = null;
        pressed.recycle();
        pressed = null;
    }
}
