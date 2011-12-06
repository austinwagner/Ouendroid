package com.wagner.ouendroid;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Draws a 480x854 texture to the screen. The texture will stretch horizontally and vertically to fit
 * the screen if necessary.
 */
public class FullScreenImage {
    private int textureId;
    private boolean loadTexture = true;
    private Bitmap bitmap;
    private float vertices[];
	private short[] indices = { 0, 1, 2, 0, 2, 3 };
    private boolean transparent;

    private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    /**
     * @param bitmap The texture to draw on the square.
     * @param width The width of the screen.
     * @param height The height of the screen.
     * @param transparent Set to false if the image does not have transparency to improve performance.
     */
    public FullScreenImage(Bitmap bitmap, int width, int height, boolean transparent) {
        vertices = new float[] {
		      0.0f,   0.0f, 0.0f, // 0, Top Left
		      0.0f, height, 0.0f, // 1, Bottom Left
		     width, height, 0.0f, // 2, Bottom Right
		     width,   0.0f, 0.0f  // 3, Top Right
	    };

        float textureCoordinates[] = {
                  0.0f,         0.0f,
                  0.0f, 0.833984375f,
              0.46875f, 0.833984375f,
              0.46875f,         0.0f
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

        this.bitmap = bitmap;
        this.transparent = transparent;
    }

    /**
     * Draws the texture to the screen.
     * @param gl The OpenGL instance to draw to.
     */
    public void draw(GL10 gl) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        if (transparent) {
            gl.glDepthMask(false);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        if (transparent) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDepthMask(true);
        }
    }

    /**
     * Loads the bitmap as an OpenGL texture.
     * @param gl The instance of OpenGL to load the texture to.
     */
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

    /**
     * Removes the bitmap loaded by this instance.
     */
    public void unload() {
        bitmap.recycle();
        bitmap = null;
    }
}
