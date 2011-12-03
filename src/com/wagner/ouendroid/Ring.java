package com.wagner.ouendroid;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import org.apache.commons.logging.Log;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 3:49 PM
 */
public class Ring {
    private static final int SEGMENTS = 20;
    private float radius = 0.0f;
    private float x;
    private float y;

    public Ring(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    /**
     * This function draws our square on screen.
     * @param gl
     */
    public void draw(GL10 gl) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);
        gl.glColor4f(0.0f, 1.0f, 0.0f, 0.0f);
        ByteBuffer vbb = ByteBuffer.allocateDirect(SEGMENTS * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        for (float i = 0; i < 360.0f; i+=(360.0f/SEGMENTS))
        {
            vertexBuffer.put((float)Math.cos(degreesToRadian(i))*radius);
            vertexBuffer.put((float)Math.sin(degreesToRadian(i))*radius);
        }

        vertexBuffer.position(0);

        gl.glEnable(GL10.GL_LINE_SMOOTH);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glLineWidth(1.5f);
		gl.glVertexPointer (2, GL10.GL_FLOAT , 0, vertexBuffer);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, SEGMENTS);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glPopMatrix();
    }

    private float degreesToRadian(float angle) {
        return angle * (float)Math.PI / 180.0f;
    }
}
