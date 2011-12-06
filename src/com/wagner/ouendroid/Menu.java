package com.wagner.ouendroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 3:55 PM
 */
public class Menu {
    private enum State { MAIN, FILES }

    private OpenGLRenderer parent;
    private State state = State.MAIN;
    private ArrayList<String> files;
    private ArrayList<Text> fileText;
    private FullScreenImage background;
    private TwoStateButton songButton;
    private boolean songButtonPressed = false;
    private int selected = -1;

    /**
     * Removes the bitmaps loaded by this instance.
     */
    public void unload() {
        background.unload();
        songButton.unload();
    }

    /**
     * This class renders the game menu and tracks all states related to the menu.
     * @param parent The OpenGLRenderer that created this game instance.
     * @param context The context to get the texture bitmaps from.
     */
    public Menu(OpenGLRenderer parent, Context context) {
        this.parent = parent;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        background = new FullScreenImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.menu, o),
                parent.getWidth(), parent.getHeight(), false);

        songButton = new TwoStateButton(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.select, o),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.select_p, o),
                parent.getWidth() / 2.0f, parent.getHeight() * 3.0f / 4.0f,
                285, 53, 0.556640625f, 0.103515625f);

    }

    /**
     * Draws the menu screen and processes events.
     * @param gl The OpenGL instance to draw to.
     */
    public void draw(GL10 gl) {

        background.draw(gl);

        // If the main screen is showing
        if (state == State.MAIN) {
            // When the select song button is pressed, load the list of files from the SD card and go to the song
            // list screen
            if (!parent.isTouchHandled() && songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())
                    && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP) {
                songButtonPressed = false;
                File rootDir = new File("/sdcard/");
                File [] filesArr = rootDir.listFiles(new FilenameFilter() {
                    public boolean accept(File file, String s) {
                        return s.endsWith(".oed");
                    }
                });
                files = new ArrayList<String>();
                fileText = new ArrayList<Text>();
                for (File f : filesArr) {
                    files.add(f.getName());
                }
                Collections.sort(files);
                float top = 3.0f;
                for (String f : files) {
                    fileText.add(new Text().setText(f.substring(0, f.length() - 4)).setX(2.0f).setY(top).setScale(1.5f));
                    top += 36f;
                }

                state = State.FILES;
            // Handle pressing the button down
            } else if (!parent.isTouchHandled() && songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                songButtonPressed = true;
            // Handle moving off of the button without releasing
            } else if (!parent.isTouchHandled()&& !songButton.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                songButtonPressed = false;
            }
            songButton.draw(gl, songButtonPressed);
        // If the song selection is showing
        } else {
            // If the back key was pressed, go back to the main menu
            if (!parent.isKeyHandled() && parent.getKeyEvent().getKeyCode() == KeyEvent.KEYCODE_BACK) {
                state = State.MAIN;
                parent.setKeyHandled();
            }

            // Clear the last selected item if the screen was tapped
            if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN)
                selected = -1;

            // Print the file list to the screen while checking if any of the songs were chosen
            for (int i = 0; i < files.size(); i++) {
                Text t = fileText.get(i);
                if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN &&
                        t.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY()))
                    selected = i;
                if (i == selected)
                    t.setBlue(0.0f).setRed(0.0f);
                else
                    t.setBlue(1.0f).setRed(1.0f);
                t.draw(gl);
            }

            // If the tap was released while hovering over a song, load that song and go to the game screen
            if (selected != -1 && !parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP &&
                    fileText.get(selected).isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY())) {
                String chosen = files.get(selected);
                parent.startGame("file:///sdcard/" + chosen.substring(0, chosen.length() - 3) + "mp3",
                        "/sdcard/" + chosen);
                state = State.MAIN;
                selected = -1;
            // If the tap was released over nothing, deselect the item
            } else if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_UP) {
                selected = -1;
            }
        }

        parent.setTouchHandled();
    }

}
