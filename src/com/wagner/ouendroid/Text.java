package com.wagner.ouendroid;

import javax.microedition.khronos.opengles.GL10;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 2:48 PM
 */
public class Text {
    private String text = "";
    private float x = 0;
    private float y = 0;
    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private HorAlign hor = HorAlign.LEFT;
    private VertAlign vert = VertAlign.TOP;
    private float scale = 1.0f;

    public Text() {

    }

    public Text setText(String text) {
        this.text = text;
        return this;
    }

    public Text setX(float x) {
        this.x = x;
        return this;
    }

    public Text setY(float y) {
        this.y = y;
        return this;
    }

    public Text setRed(float r) {
        this.r = r;
        return this;
    }

    public Text setGreen(float g) {
        this.g = g;
        return this;
    }

    public Text setBlue(float b) {
        this.b = b;
        return this;
    }

    public Text setScale(float s) {
        scale = s;
        return this;
    }

    public Text setHorizontalAlignment(HorAlign align) {
        this.hor = align;
        return this;
    }

    public Text setVerticalAlignment(VertAlign align) {
        this.vert = align;
        return this;
    }

    public static enum HorAlign {
        LEFT, CENTER, RIGHT
    }

    public static enum VertAlign {
        TOP, MIDDLE, BOTTOM
    }

    public void draw(GL10 gl) {
        float top, left;

        if (vert == VertAlign.TOP)
            top = y;
        else if (vert == VertAlign.MIDDLE)
            top = y - 11.0f * scale;
        else
            top = y - 22.0f * scale;

        if (hor == HorAlign.LEFT)
            left = x;
        else if (hor == HorAlign.CENTER)
            left = x - 7.0f * text.length() * scale;
        else
            left = x - 14.0f * text.length() * scale;

        for (char c : text.toCharArray()) {
            new Character(c, left, top, r, g, b, scale).draw(gl);
            left += 14.0f * scale;
        }
    }

    public boolean isHit(float hitX, float hitY) {
        float top, left;

        if (vert == VertAlign.TOP)
            top = y;
        else if (vert == VertAlign.MIDDLE)
            top = y - 11.0f * scale;
        else
            top = y - 22.0f * scale;

        if (hor == HorAlign.LEFT)
            left = x;
        else if (hor == HorAlign.CENTER)
            left = x - 7.0f * text.length() * scale;
        else
            left = x - 14.0f * text.length() * scale;

        return (hitX > left && hitX < left + text.length() * 14.0f * scale && hitY > top && hitY < top + 22.0f * scale);
    }
}
