package com.wagner.ouendroid;

/**
 * Created by IntelliJ IDEA.
 * User: Alex
 * Date: 12/3/11
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    int time;
    double xCoord;
    double yCoord;
    int comboChange;
    int color;
    int number = 0;
    int hitSound;

    ArrayList<ButtonInfo> timesCoords = new ArrayList<ButtonInfo>();

    public FileReader() {



    }
    public ArrayList<ButtonInfo> getButtonInfoList(String inFileName) {

        final File file;

        file = new File(inFileName);

        try
        {
            final Scanner scanner;

            scanner = new Scanner(file).useDelimiter("\r\n");

            while(scanner.hasNext())
            {
                parseLine(scanner.next());
            }
        }
        catch(final FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return timesCoords;
    }
    // set up our scanner to use , as delimiter and parse all our data
    private void parseLine(String line) {
        Scanner scanner = new Scanner(line).useDelimiter(",");
        xCoord = Double.parseDouble(scanner.next());
        yCoord = Double.parseDouble(scanner.next());
        time   = Integer.parseInt(scanner.next());
        comboChange = Integer.parseInt(scanner.next());
        hitSound = Integer.parseInt(scanner.next());

        if (comboChange != 1 && comboChange != 5) {
            scanner.useDelimiter("\n");
            scanner.next();
            return;
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
        xCoord *= (280.0 / 512.0);
        yCoord *= (510.0 / 384.0);
        ButtonInfo button = new ButtonInfo(time, (int)xCoord, (int)yCoord, color, number);
        timesCoords.add(button);

    }

}
