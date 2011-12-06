package com.wagner.ouendroid;

/**
 * A container for data parsed from Osu! files. Contains all of the necessary information
 * to display the button at the correct place and time.
 */
public class ButtonInfo {
    public int time;
    public int x;
    public int y;
    public int color;
    public int number;

    /**
     * @param inTime The time in millisecond of the song for the button to be displayed.
     * @param xCoord The horizontal location of the button.
     * @param yCoord The vertical location of the button.
     * @param inColor The color of the button between, 0 and 2.
     * @param inNum The number on the button between, 0 and 8.
     */
    public ButtonInfo(int inTime, int xCoord, int yCoord, int inColor, int inNum) {
        time = inTime;
        color = inColor;
        x = xCoord;
        y = yCoord;
        number = inNum;
    }


}
