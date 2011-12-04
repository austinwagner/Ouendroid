package com.wagner.ouendroid;

import javax.microedition.khronos.opengles.GL10;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * User: Austin Wagner
 * Date: 12/4/11
 * Time: 3:55 PM
 */
public class Menu {
    private enum State { MAIN, FILES }

    OpenGLRenderer parent;
    private State state = State.MAIN;
    private File[] files;

    public Menu(OpenGLRenderer parent) {
        this.parent = parent;
    }

    public void draw(GL10 gl, float tapX, float tapY) {
        if (state == State.MAIN) {
            File rootDir = new File("/sdcard/");
            files = rootDir.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.endsWith(".oed");
                }
            });
            state = State.FILES;
        } else {
            float top = 2.0f;
            for (File f : files) {
                new Text().setText(f.getName().substring(0, f.getName().length() - 4)).setX(2.0f).setY(top).draw(gl);
                top += 24.0f;
            }

            if (tapX > 0.0f) {
                parent.startGame("file:///sdcard/" + files[1].getName().substring(0, files[1].getName().length() - 3) + "mp3",
                        "/sdcard/" + files[1].getName());
                state = State.MAIN;
            }
        }
    }
}
