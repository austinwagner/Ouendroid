package com.wagner.ouendroid;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLUtils;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:16 AM
 */

public class Button {
    private Bitmap bitmap;
    private int textureId;
    private boolean loadTexture = true;
    private float color = 1.0f;
    private float x;
    private float y;

	private float vertices[] = {
		      -32.0f,  -32.0f, 0.0f,  // 0, Top Left
		      -32.0f, 32.0f, 0.0f,  // 1, Bottom Left
		       32.0f, 32.0f, 0.0f,  // 2, Bottom Right
		       32.0f,  -32.0f, 0.0f,  // 3, Top Right
		};

    private float textureCoordinates[] = {0.0f, 0.0f,
                              0.0f, 1.0f,
                              1.0f, 1.0f,
                              1.0f, 0.0f };

	private short[] indices = { 0, 1, 2, 0, 2, 3 };

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

	public Button(Bitmap b, float x, float y) {
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


        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoordinates.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(textureCoordinates);
        textureBuffer.position(0);

        bitmap = b;
        this.x = x;
        this.y = y;
    }

    public void toggle() {
        if (color == 1.0f)
            color = 0.0f;
        else
            color = 1.0f;
    }

    public boolean isHit(float tapX, float tapY) {
        if((tapX - x)*(tapX - x) + (tapY - y)*(tapY - y) < 32 * 32)
            return true;
        else
            return false;
    }

	/**
	 * This function draws our square on screen.
	 * @param gl
	 */
	public void draw(GL10 gl) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);

        gl.glColor4f(1.0f, color, 1.0f, 0.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
                                 vertexBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
				  GL10.GL_UNSIGNED_SHORT, indexBuffer);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glPopMatrix();
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
