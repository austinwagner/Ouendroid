package com.wagner.ouendroid;

import javax.microedition.khronos.opengles.GL10;

/**
 * Automatically aligns {@link Character Characters} to form a single string. Also provides
 * a function for hit detection. All functions return an instance of this class to allow for chaining.
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

    /**
     * Sets the string of characters to render. All non-printing characters will display as blank space.
     * @param text The string of text to print.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Sets the horizontal position of the text.
     * @param x The horizontal position of the text.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the vertical position of the text.
     * @param y The vertical position of the text.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the value of the red channel.
     * @param r The value of the red channel from 0 to 1.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setRed(float r) {
        this.r = r;
        return this;
    }

    /**
     * Sets the value of the green channel.
     * @param g The value of the green channel from 0 to 1.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setGreen(float g) {
        this.g = g;
        return this;
    }

    /**
     * Sets the value of the blue channel.
     * @param b The value of the blue channel from 0 to 1.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setBlue(float b) {
        this.b = b;
        return this;
    }

    /**
     * Sets the scale to print the text at.
     * @param s The scale of the text where 1 is normal size.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setScale(float s) {
        scale = s;
        return this;
    }

    /**
     * Sets the horizontal alignment anchor for the text.
     * @param align The horizontal alignment anchor.
     * @return This object to allow for chaining of calls to set methods.
     */
    public Text setHorizontalAlignment(HorAlign align) {
        this.hor = align;
        return this;
    }

    /**
     * Sets the vertical alignment anchor for the text.
     * @param align The vertical alignment anchor.
     * @return This object to allow for chaining of calls to set methods.
     */
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

    /**
     * Draws the text to the screen.
     * @param gl The OpenGL instance to draw to.
     */
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

    /**
     * Checks if the text has been hit.
     * @param hitX The x position to hit test.
     * @param hitY The y position to hit test.
     * @return True if the hit is within the bounds of the text, otherwise false.
     */
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
