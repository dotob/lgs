import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Vector;

public class FileSyncer {
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
        File[] files = f.listFiles();
        for (File aFile : files) {
            ret.add(new FileInfo(aFile));
        }
        return ret;
    }

    /*
    * return false if fail
     */
    public static boolean SyncItemsFS(Vector<FileInfo> master, String slaveDir, String targetDir, JTextArea output) {
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        if (!doChecking(master, slaveDirFile, targetDirFile, output)) {
            return false;
        }
        Vector<File> toCopy = new Vector<File>();
        for (FileInfo fi : master) {
            for (File siblingInSlave : fi.GetSiblings(slaveDirFile)) {
                toCopy.add(siblingInSlave);
            }
        }
        doCopying(toCopy, targetDirFile, output);
        return true;
    }

    private static boolean doChecking(AbstractCollection master, File slaveDirFile, File targetDirFile, JTextArea output) {
        boolean result = true;
        if (master.size() <= 0) {
            output.append("master information ist leer\n");
            result = false;
        }
        if (!slaveDirFile.isDirectory()) {
            output.append("slave-verzeichnis existiert nicht oder ist kein verzeichnis\n");
            result = false;
        }
        if (!targetDirFile.isDirectory()) {
            output.append("ziel-verzeichnis existiert nicht oder ist kein verzeichnis\n");
            result = false;
        }
        return result;
    }

    private static void doCopying(Vector<File> toCopy, File targetDirFile, JTextArea output) {
        output.append("starte kopieren von " + toCopy.size() + " dateien, master ist verzeichnis\n");
        int current = 0;
        for (File f : toCopy) {
            try {
                current++;
                output.append("cp " + current + "/" + toCopy.size() + " :" + f.getName() + " \n");
                FileUtils.copyFileToDirectory(f, targetDirFile);
            } catch (IOException e) {
                output.append("fehler beim kopieren von:" + f.getName() + "\n");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static boolean SyncItemsDB(Vector<String> master, String slaveDir, String targetDir, JTextArea output) {
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        if (!doChecking(master, slaveDirFile, targetDirFile, output)) {
            return false;
        }
        Vector<File> toCopy = new Vector<File>();
        // get all files, do not consider extensions
        for (FileInfo fi : GetFileInfoItems(slaveDir)) {
            for (String masterRequired : master) {
                if (fi.getMatchName().equals(masterRequired)) {
                    toCopy.add(fi.getFile());
                }
            }
        }
        doCopying(toCopy, targetDirFile, output);
        return true;

    }
}
