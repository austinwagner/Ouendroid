package com.wagner.ouendroid;

/**
 * Created by IntelliJ IDEA.
 * User: Alex
 * Date: 12/3/11
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

    int time;
    String xCoord;
    String yCoord;
    int color;
    ArrayList<ButtonInfo> timesCoords = new ArrayList<ButtonInfo>();

    public FileReader() {

    }
    public ArrayList<ButtonInfo> getButtonInfoList(String inFile) {
        final File file;



                //file = new File(inFile);

//                try
//                {
                    final Scanner scanner;

                    scanner = new Scanner(inFile).useDelimiter("\n");

                    while(scanner.hasNext())
                    {
                        parseLine(scanner.next());
                    }
//                }
//                catch(final FileNotFoundException ex)
//                {
//                    ex.printStackTrace();
//                }
        return timesCoords;
    }
    private void parseLine(String line) {
        Scanner scanner = new Scanner(line).useDelimiter(",");
        time   = Integer.parseInt(scanner.next());
        xCoord = scanner.next();
        yCoord = scanner.next();
        color = Integer.parseInt(scanner.next());
        ButtonInfo button = new ButtonInfo(time, Integer.parseInt(xCoord), Integer.parseInt(yCoord), color);
        timesCoords.add(button);

    }

}
