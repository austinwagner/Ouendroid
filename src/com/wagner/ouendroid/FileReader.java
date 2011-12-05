package com.wagner.ouendroid;

/**
 * User: Alex
 * Date: 12/3/11
 * Time: 2:45 PM
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    private int color;
    private int number;
    private int screenWidth, screenHeight;

    public FileReader(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    public ArrayList<ButtonInfo> getButtonInfoList(String inFileName) {
        color = 0;
        number = 0;
        ArrayList<ButtonInfo> timesCoords = new ArrayList<ButtonInfo>();

        try
        {
            final File file = new File(inFileName);
            final Scanner scanner;

            scanner = new Scanner(file).useDelimiter("\r\n");

            while(scanner.hasNext())
            {
                ButtonInfo b = parseLine(scanner.next());
                if (b != null)
                    timesCoords.add(b);
            }
        }
        catch(final FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return timesCoords;
    }
    // set up our scanner to use , as delimiter and parse all our data
    private ButtonInfo parseLine(String line) {
        Scanner scanner = new Scanner(line).useDelimiter(",");
        double xCoord = Double.parseDouble(scanner.next());
        double yCoord = Double.parseDouble(scanner.next());
        int time   = Integer.parseInt(scanner.next());
        int comboChange = Integer.parseInt(scanner.next());
        int hitSound = Integer.parseInt(scanner.next());

        if (comboChange != 1 && comboChange != 5) {
            scanner.useDelimiter("\n");
            scanner.next();
            return null;
        }
        if (comboChange != 5 && number < 8)  // if there's no combo change, increment the number
            number++;
        else if (comboChange == 5) { // if we need to change combo, rotate the color and reset the number.
            if (color < 2)
                color++;
            else
                color = 0;
            number = 1;
        }
        xCoord *= (screenWidth / 512.0);
        yCoord *= (screenHeight / 384.0);
        return new ButtonInfo(time, (int)xCoord, (int)yCoord, color, number);
    }

}
