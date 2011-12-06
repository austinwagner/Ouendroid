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
 * Date: 12/4/11
 * Time: 10:32 AM
 */
public class Miss {
    private static Bitmap bitmap;
    private static int textureId;
    private static boolean loadTexture = true;
    private float x, y;
    private int time;

    private float vertices[] = {
             -64.0f, -64.0f, 0.0f,  // 0, Top Left
             -64.0f,  64.0f, 0.0f,  // 1, Bottom Left
              64.0f,  64.0f, 0.0f,  // 2, Bottom Right
              64.0f, -64.0f, 0.0f,  // 3, Top Right
    };

    private short[] indices = { 0, 1, 2, 0, 2, 3 };

    private float textureCoordinates[] = {
              0.0f, 0.0f,
              0.0f, 1.0f,
              1.0f, 1.0f,
              1.0f, 0.0f
    };

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer textureBuffer;

    /**
     * Sets the miss texture to be loaded. All instances of {@link Miss Miss} will use this texture.
     * @param b The bitmap containing the miss text.
     */
    public static void initialize(Bitmap b) {
        bitmap = b;
        loadTexture = true;
    }

    /**
     * This class displays the text "Miss" on the screen which lasts until a specified time.
     * @param time The time in milliseconds of time in the song to remove this image.
     * @param x The horizontal postion of the center.
     * @param y The vertical position of the center.
     */
    public Miss(int time, float x, float y) {
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

        this.x = x;
        this.y = y;
        this.time = time;
    }

    /**
     * Draws the miss texture to the screen.
     * @param gl The OpenGL instance to draw to.
     */
    public void draw(GL10 gl) {
        if (loadTexture) {
            loadGLTexture(gl);
            loadTexture = false;
        }

        gl.glPushMatrix();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glTranslatef(x, y, 0.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);


        gl.glDepthMask(false);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_BLEND);
        gl.glDepthMask(true);
        gl.glPopMatrix();
    }

    /**
     * Get the time that this image is set to expire.
     * @return The time in milliseconds of the song time for the image to disappear.
     */
    public int getTime() {
        return time;
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
     * Removes the bitmap that all instances of {@link Miss Miss} use from memory.
     */
    public static void unload() {
        bitmap.recycle();
        bitmap = null;
    }

}
