package com.wagner.ouendroid;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static javax.microedition.khronos.opengles.GL10.*;

/**
 * Displays the text "Miss" on the screen which lasts until a specified time.
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

        gl.glEnable(GL_TEXTURE_2D);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer(2, GL_FLOAT, 0, textureBuffer);
        gl.glBindTexture(GL_TEXTURE_2D, textureId);


        gl.glDepthMask(false);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);

        gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glDisable(GL_BLEND);
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
        gl.glBindTexture(GL_TEXTURE_2D, textureId);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
                GL_LINEAR);

        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
                GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
    }

    /**
     * Removes the bitmap that all instances of {@link Miss Miss} use from memory.
     */
    public static void unload() {
        bitmap.recycle();
        bitmap = null;
    }

}
