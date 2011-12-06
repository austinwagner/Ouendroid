package com.wagner.ouendroid;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: Austin Wagner
 * Date: 12/5/11
 * Time: 7:58 PM
 */
public class DimScreen {
	private short[] indices = { 0, 1, 2, 0, 2, 3 };

    private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;

    /**
     * This class draws a black box at half transparency. Gives the effect of a dimmer screen.
     * @param width The width of the screen.
     * @param height The height of the screen.
     */
    public DimScreen(int width, int height) {
        float[] vertices = new float[] {
		      0.0f,   0.0f, 0.0f, // 0, Top Left
		      0.0f, height, 0.0f, // 1, Bottom Left
		     width, height, 0.0f, // 2, Bottom Right
		     width,   0.0f, 0.0f  // 3, Top Right
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
    }

    /**
     * Draws the translucent square to the screen.
     * @param gl The OpenGL instance to draw to.
     */
    public void draw(GL10 gl) {

        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glDepthMask(false);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_BLEND);
        gl.glDepthMask(true);
    }
}
