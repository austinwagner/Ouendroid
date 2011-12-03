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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.List;

public class FileReader {
    public FileReader() {

    }
    public HashMap getTimeCoordMap(String inFile) {
        final File file;
        String time;
        String xCoord;
        String yCoord;
        HashMap<String,Coordinate> timesCoords = new HashMap<String,Coordinate>();

                //file = new File(inFile);

//                try
//                {
                    final Scanner scanner;

                    scanner = new Scanner(inFile).useDelimiter(",");

                    while(scanner.hasNextLine())
                    {
                        time   = scanner.next();
                        xCoord = scanner.next();
                        yCoord = scanner.next();
                        Coordinate coord = new Coordinate(Integer.parseInt(xCoord), Integer.parseInt(yCoord));
                        timesCoords.put(time, coord);

                    }
//                }
//                catch(final FileNotFoundException ex)
//                {
//                    ex.printStackTrace();
//                }
        return timesCoords;
    }
}
