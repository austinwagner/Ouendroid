package com.wagner.ouendroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * User: Austin Wagner
 * Date: 12/3/11
 * Time: 11:05 AM
 */
public class OpenGLRenderer implements Renderer {
    private enum State { MENU, GAME }
    private State state = State.MENU;
    private Game game;
    private Menu menu;
    private Context context;

    private int width, height;
    private boolean keyHandled = true;
    private boolean touchHandled = true;
    private int keyCode;
    private KeyEvent keyEvent;
    private MotionEvent touchEvent;
    private Activity parent;

    /**
     * Main renderer for the entire game. Controls all aspects of the game including tracking states and handling
     * button presses.
     * @param context The context to get the bitmap resources from.
     * @param parent The activity that holds this renderer.
     */
    public OpenGLRenderer(Context context, Activity parent) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Button.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.buttons, o));
        Character.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.characters, o));
        Miss.initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.miss, o));
        this.context = context;
        this.parent = parent;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    /**
     * Tell the renderer that a touch action is ready for processing.
     * If another touch event is still pending processing, it will be replaced
     * @param event The action that needs to be processed.
     */
    public void handleTouch(MotionEvent event) {
        touchEvent = event;
        touchHandled = false;
    }

    /**
     * Tell the renderer that a key action is ready for processing.
     * If another key event is still pending processing, it will be replaced
     * @param event The action that needs to be processed.
     */
    public void handleKey(KeyEvent event) {
        keyEvent = event;
        keyHandled = false;
    }

    /**
     * Gets the last touch event sent for processing.
     * @return The latest touch event.
     */
    public MotionEvent getTouchEvent() {
        return touchEvent;
    }

    /**
     * Checks if there is a pending touch event.
     * @return True if the last touch event has already been handled, otherwise false.
     */
    public boolean isTouchHandled() {
        return touchHandled;
    }

    /**
     * Tells the renderer that the touch event has been handled.
     */
    public void setTouchHandled() {
        touchHandled = true;
    }

    /**
     * Gets the last key event sent for processing.
     * @return The latest key event.
     */
    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    /**
     * Checks if there is a pending key event.
     * @return True if the last key event has already been handled, otherwise false.
     */
    public boolean isKeyHandled() {
        return keyHandled;
    }

    /**
     * Tells the renderer that the key event has been handled.
     */
    public void setKeyHandled() {
        keyHandled = true;
    }

    public void onDrawFrame(GL10 gl) {
        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        if (state == State.MENU) {
            menu.draw(gl);
            if (!keyHandled && keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                    keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                parent.finish();
                keyHandled = true;
            }
        } else if (state == State.GAME) {
            game.draw(gl);
            if (!keyHandled && keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                returnToMenu();
                keyHandled = true;
            }
        }


    }

    /**
     * Stops the game and redisplays the menu
     */
    public void returnToMenu() {
        game.stop();
        state = State.MENU;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, width, height, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        this.width = width;
        this.height = height;

        menu = new Menu(this, context);
        game = new Game(this, context);
    }

    /**
     * Reintializes and starts the game with a new song and note chart.
     * @param song The path to the song to play as a URI (e.g. file:///sdcard/song.mp3)
     * @param chart The file path to the note chart (e.g. /sdcard/song.oed)
     */
    public void startGame(String song, String chart) {
        game.initialize(song, chart);
        state = State.GAME;
    }

    /**
     * Unloads all bitmap resources associated with the game.
     * IMPORTANT: The entire renderer MUST be reinitialized if this function is called.
     */
    public void unload() {
        game.stop();
        game.unload();
        Button.unload();
        Character.unload();
        Miss.unload();
        menu.unload();
    }

    /**
     * Gets the height of the renderer as set by {@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(
     * javax.microedition.khronos.opengles.GL10, int, int) onSurfaceChanged}
     * @return The height of the renderer.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the width of the renderer as set by {@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(
     * javax.microedition.khronos.opengles.GL10, int, int) onSurfaceChanged}
     * @return The width of the renderer.
     */
    public int getWidth() {
        return width;
    }
}
