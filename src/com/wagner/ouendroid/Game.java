package com.wagner.ouendroid;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.wagner.ouendroid.Config.*;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 3:57 PM
 */
public class Game {
    private LinkedList<Button> buttons = new LinkedList<Button>();
    private LinkedList<Miss> misses = new LinkedList<Miss>();
    private int readerPos;
    private MediaPlayer player;
    private FileReader reader;
    private ArrayList<ButtonInfo> timesCoords;
    private int score;
    private float health;
    private int lastTime;
    private OpenGLRenderer parent;
    private DimScreen dimScreen;
    private Square pauseScreen;
    private FullScreenImage background;
    private Text scoreText;
    private Text healthText;
    private Square successText;
    private Square failureText;
    private boolean gameOver;
    private Context context;

    /**
     * This class renders the game screen and tracks all states related to running the game.
     * @param parent The OpenGLRenderer that created this game instance.
     * @param context The context to get the texture bitmaps from.
     */
    public Game(OpenGLRenderer parent, Context context) {
        this.parent = parent;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        dimScreen = new DimScreen(parent.getWidth(), parent.getHeight());
        pauseScreen = new Square(BitmapFactory.decodeResource(context.getResources(), R.drawable.pause, o),
                parent.getWidth(), parent.getWidth() / 2, parent.getHeight() / 2);
        background = new FullScreenImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.menu, o),
                parent.getWidth(), parent.getHeight(), false);
        reader = new FileReader(parent.getWidth(), parent.getHeight());
        scoreText = new Text().setHorizontalAlignment(Text.HorAlign.RIGHT).setVerticalAlignment(Text.VertAlign.BOTTOM)
                .setX(parent.getWidth() - 2.0f).setY(parent.getHeight() - 2.0f);
        healthText = new Text().setHorizontalAlignment(Text.HorAlign.LEFT).setVerticalAlignment(Text.VertAlign.BOTTOM)
                .setX(2.0f).setY(parent.getHeight() - 2.0f);
        successText = new Square(BitmapFactory.decodeResource(context.getResources(), R.drawable.clear, o),
                parent.getWidth(), parent.getWidth() / 2, parent.getHeight() / 2);
        failureText = new Square(BitmapFactory.decodeResource(context.getResources(), R.drawable.fail, o),
                parent.getWidth(), parent.getWidth() / 2, parent.getHeight() / 2);

        this.context = context;
    }

     /**
     * Intializes and starts the game with a new song and note chart.
     * @param songPath The path to the song to play as a URI (e.g. file:///sdcard/song.mp3)
     * @param chartPath The file path to the note chart (e.g. /sdcard/song.oed)
     */
    public void initialize(String songPath, String chartPath) {
        readerPos = 0;
        score = 0;
        health = 100.0f;
        lastTime = 0;
        gameOver = false;
        timesCoords = reader.getButtonInfoList(chartPath);
        player = new MediaPlayer();
        Uri songUri = Uri.parse(songPath);
        try {
            player.setDataSource(context,songUri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws the game screen and processes events.
     * @param gl The OpenGL instance to draw to.
     */
    public void draw(GL10 gl) {
        // Handle menu button press (pause)
        if (!parent.isKeyHandled() && parent.getKeyEvent().getAction() == KeyEvent.ACTION_DOWN &&
                parent.getKeyEvent().getKeyCode() == KeyEvent.KEYCODE_MENU) {
            player.pause();
            parent.setKeyHandled();
        }

        int time = player.getCurrentPosition();

        // If game is paused, do not process events
        if (player.isPlaying()) {

            // Load in buttons that will need to be displayed
            while (readerPos < timesCoords.size()) {
                ButtonInfo info = timesCoords.get(readerPos);
                if (info.time - time < RING_TIME) {
                    readerPos++;
                    buttons.addFirst(new Button(info));
                } else {
                    break;
                }
            }

            // Perform health decrement
            health -= (time - lastTime) / 1000.0f * HEALTH_PER_SECOND;
            if (health < 0.0f) health = 0.0f;

            // Handle tap
            if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN && buttons.size() > 0) {
                Button b = buttons.getLast();
                if (b.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY()) && b.scoreMultiplier(time) > 0) {
                    buttons.removeLast();
                    score += BUTTON_VALUE * b.scoreMultiplier(time);
                    health += HEALTH_PER_HIT;
                    if (health > 100.0f) health = 100.0f;
                }
            }

            // Handle button timeout
            if (buttons.size() > 0 && buttons.getLast().getInfo().time - time < -MAX_TIME_FOR_HIT) {
                Button b = buttons.removeLast();
                misses.add(new Miss(b.getInfo().time + MISS_TEXT_DURATION, b.getInfo().x, b.getInfo().y));
            }

            // Remove old misses
            if (misses.size() > 0 && misses.peek().getTime() <= time) {
                misses.removeFirst();
            }
        }

        // Draw the background image
        background.draw(gl);

        // Draw buttons
        for (Button b : buttons) {
            b.draw(gl, time);
        }

        // Draw misses
        for (Miss m : misses) {
            m.draw(gl);
        }

        // If the game is paused, display the pause screen
        if (!player.isPlaying() && !gameOver && time < player.getDuration() - 100) {
            healthText.setText("Health: " + (int)health).draw(gl);
            scoreText.setText("Score: " + score).draw(gl);
            dimScreen.draw(gl);
            pauseScreen.draw(gl);
            if (!parent.isTouchHandled())
                player.start();
        // If the song is over, display the victory screen
        } else if (!player.isPlaying() && health > 0.0f) {
            gameOver = true;
            dimScreen.draw(gl);
            successText.draw(gl);
            healthText.setText("Health: " + (int)health).draw(gl);
            scoreText.setText("Score: " + score).draw(gl);
        // If the player loses, display the failure screen
        } else if (health <= 0.0f) {
            gameOver = true;
            player.stop();
            dimScreen.draw(gl);
            failureText.draw(gl);
            healthText.setText("Health: " + (int)health).draw(gl);
            scoreText.setText("Score: " + score).draw(gl);
        // Otherwise, just draw the health and score
        } else {
            healthText.setText("Health: " + (int)health).draw(gl);
            scoreText.setText("Score: " + score).draw(gl);
        }

        lastTime = time;
        parent.setTouchHandled();
    }

    /**
     * Stop the song.
     */
    public void stop() {
        player.stop();
    }

    /**
     * Release all bitmaps loaded by {@link Game Game}.
     */
    public void unload() {
        pauseScreen.unload();
        failureText.unload();
        successText.unload();
        background.unload();
    }


}
