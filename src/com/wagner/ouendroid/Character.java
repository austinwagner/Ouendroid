package com.wagner.ouendroid;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:16 AM
 */

public class Character {

    private static Bitmap bitmap;
    private static int textureId;
    private static boolean loadTexture = true;
    private char ascii;
    private float x, y, r, g, b;
	private short[] indices = { 0, 1, 2, 0, 2, 3 };

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    /**
     * Sets the character map to be loaded. All instances of {@link Character Character} will use this texture.
     * @param b The bitmap containing the miss text.
     */
    public static void initialize(Bitmap b) {
        bitmap = b;
        loadTexture = true;
    }

    /**
     * This class displays characters on the screen from the set of printing ASCII characters.
     * @param ascii The ASCII character code of the character to print.
     * @param x The horizontal postion of the left.
     * @param y The vertical position of the top.
     * @param r The level of the red channel from 0 to 1.
     * @param g The level of the green channel from 0 to 1.
     * @param b The level of the blue channel from 0 to 1.
     */
    public Character(char ascii, float x, float y, float r, float g, float b, float scale) {
        float vertices[] = {
		               0.0f,          0.0f, 0.0f,  // 0, Top Left
		               0.0f, 22.0f * scale, 0.0f,  // 1, Bottom Left
		      14.0f * scale, 22.0f * scale, 0.0f,  // 2, Bottom Right
		      14.0f * scale,          0.0f, 0.0f,  // 3, Top Right
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

        if (ascii >= 32 && ascii <= 126)
            this.ascii = ascii;
        else
            this.ascii = 32;
        this.x = x;
        this.y = y;
        this.r = r;
        this.b = b;
        this.g = g;
    }

	/**
     * Draws the character to the screen.
     * @param gl The OpenGL instance to draw to.
     */
	public void draw(GL10 gl) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glPushMatrix();
        int row = (ascii - 32) / 18;
        int col = (ascii - 32) % 18;

        float textureCoordinates[] = {
              0.0546875f * col,       0.0859375f * row,       // Top Left
              0.0546875f * col,       0.0859375f * (row + 1), // Bottom Left
              0.0546875f * (col + 1), 0.0859375f * (row + 1), // Bottom Right
              0.0546875f * (col + 1), 0.0859375f * row        // Top Right
        };

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(textureCoordinates);
        textureBuffer.position(0);

        gl.glColor4f(r, g, b, 0.0f);
        gl.glTranslatef(x, y, 0.0f);

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
        gl.glPopMatrix();
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
			    GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    /**
     * Removes the bitmap that all instances of {@link Character Character} use from memory.
     */
    public static void unload() {
        bitmap.recycle();
        bitmap = null;
    }

}
