package dtp.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFilter extends FileFilter {

    private String[] extensions;

    public ExtensionFilter(String[] extensions) {
        this.extensions = extensions;
    }

    private static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public boolean accept(File file) {
        if (file.isDirectory())
            return true;

        String extension = getExtension(file);

        for (String ext : extensions) {
            if (ext.equals(extension))
                return true;
        }

        return false;
    }

    public String getDescription() {
        StringBuilder builder = new StringBuilder("Extensions: ");
        for (String ext : extensions) {
            builder.append(ext);
            builder.append(" ");
        }
        return builder.toString();
    }
}
