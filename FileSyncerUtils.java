import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Vector;

class FileSyncerUtils {
    public static boolean doChecking(AbstractCollection master, File slaveDirFile, File targetDirFile, IMessageDisplay outputArea) {
        if (master == null) {
            outputArea.showMessage("master information ist leer (null)\n");
            return false;
        }
        if (master.size() <= 0) {
            outputArea.showMessage("master information ist leer\n");
            return false;
        }
        if (!slaveDirFile.isDirectory()) {
            outputArea.showMessage("slave-verzeichnis existiert nicht oder ist kein verzeichnis\n");
            return false;
        }
        if (!targetDirFile.isDirectory()) {
            targetDirFile.mkdirs();
            if (!targetDirFile.isDirectory()) {
                outputArea.showMessage("ziel-verzeichnis existiert nicht oder ist kein verzeichnis\n");
            }
            return false;
        }
        return true;
    }

    public static void doCopying(Vector<File> toCopy, File targetDirFile, IMessageDisplay outputArea) {
        outputArea.showMessage("starte kopieren von " + toCopy.size() + " dateien\n");
        int current = 0;
        for (File f : toCopy) {
            try {
                current++;
                outputArea.showMessage("  cp " + current + "/" + toCopy.size() + " :" + f.getName() + " \n");
                FileUtils.copyFileToDirectory(f, targetDirFile);
            } catch (IOException e) {
                outputArea.showMessage("fehler beim kopieren von:" + f.getName() + "\n");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static Vector<FileInfo> GetFileInfoItems(String absPath, String[] extensions) {
        Vector<FileInfo> ret = new Vector<FileInfo>();
        for (FileInfo aFile : GetFileInfoItems(absPath)) {
            boolean extMatch = false;
            for (String ext : extensions) {
                extMatch |= aFile.getFile().getName().toLowerCase().endsWith(ext.toLowerCase());
            }
            if (extMatch) {
                ret.add(aFile);
            }
        }
        return ret;
    }

    public static Vector<FileInfo> GetFileInfoItems(String absPath) {
        Vector<FileInfo> ret = new Vector<FileInfo>();
        File f = new File(absPath);
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            for (File aFile : files) {
                ret.add(new FileInfo(aFile));
            }
        }
        return ret;
    }
}
