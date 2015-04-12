package dtp.util;

import java.io.File;
import java.io.IOException;

public class DirectoriesResolver {

    public final static File getCurrentDir() {
        return new File(".");
    }

    public static String getNetworksDir() {
        File dir = getCurrentDir();
        try {
            return dir.getCanonicalPath() + "\\xml\\maps";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getResourcesDir() {
        File dir = getCurrentDir();
        try {
            return dir.getCanonicalPath() + "\\xml\\resources";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTxtCommisionsDir() {
        File dir = getCurrentDir();
        try {
            return dir.getCanonicalPath() + "\\benchmarks";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
