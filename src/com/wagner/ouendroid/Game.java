package com.wagner.ouendroid;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 3:57 PM
 */
public class Game {
    private LinkedList<Button> buttons = new LinkedList<Button>();
    private LinkedList<Miss> misses = new LinkedList<Miss>();
    private int readerPos = 0;
    private MediaPlayer player = new MediaPlayer();
    FileReader reader = new FileReader();
    ArrayList<ButtonInfo> timesCoords;
    private int score = 0;
    private float health = 100.0f;
    private int lastTime;
    private OpenGLRenderer parent;

    public Game(OpenGLRenderer parent) {
        this.parent = parent;
    }

    public void initialize(Context context, String songPath, String chartPath) {
        timesCoords = reader.getButtonInfoList(chartPath);
        Uri songUri = Uri.parse(songPath);
        try {
            player.setDataSource(context,songUri);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(GL10 gl) {
        int time = player.getCurrentPosition();

        // Load in buttons that will need to be displayed
        while (readerPos < timesCoords.size()) {
            ButtonInfo info = timesCoords.get(readerPos);
            if (info.time - time < Config.RING_TIME) {
                readerPos++;
                buttons.add(new Button(info));
            } else {
                break;
            }
        }

        // Perform health decrement
        health -= (time - lastTime) / 1000.0f * Config.HEALTH_PER_SECOND;
        if (health < 0.0f) health = 0;

        // Handle Tap
        if (!parent.isTouchHandled() && parent.getTouchEvent().getAction() == MotionEvent.ACTION_DOWN && buttons.size() > 0) {
            Button b = buttons.removeFirst();
            if (b.isHit(parent.getTouchEvent().getX(), parent.getTouchEvent().getY()) && b.scoreMultiplier(time) > 0) {
                score += Config.BUTTON_VALUE * b.scoreMultiplier(time);
                health += Config.HEALTH_PER_HIT;
                if (health > 100.0f) health = 100.0f;
            } else {
                misses.add(new Miss(b.getInfo().time + Config.MISS_TEXT_DURATION, b.getInfo().x, b.getInfo().y));
            }
        }

        // Handle Timeout
        if (buttons.size() > 0 && buttons.peek().getInfo().time - time < -Config.MAX_TIME_FOR_HIT) {
            Button b = buttons.removeFirst();
            misses.add(new Miss(b.getInfo().time + Config.MISS_TEXT_DURATION, b.getInfo().x, b.getInfo().y));
        }

        // Remove old misses
        if (misses.size() > 0 && misses.peek().getTime() <= time) {
            misses.removeFirst();
        }

        // Draw buttons
        for (Button b : buttons) {
            b.draw(gl, time);
        }

        // Draw misses
        for (Miss m : misses) {
            m.draw(gl);
        }

        //new Text().setText("Health: " + (int)health).setX(10).setY(10).draw(gl);
        new Text().setText(String.valueOf(time)).setX(10).setY(10).draw(gl);
        new Text().setText("Score: " + score).setX(300).setY(400).setHorizontalAlignment(Text.HorAlign.RIGHT).
                setVerticalAlignment(Text.VertAlign.BOTTOM).draw(gl);

        lastTime = time;

        parent.setTouchHandled();
    }

    public void stop() {
        player.stop();
    }


}
