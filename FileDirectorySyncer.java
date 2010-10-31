import javax.swing.*;
import java.io.File;
import java.util.Vector;

public class FileDirectorySyncer extends SwingWorker<Boolean, Object> {
    private IMessageDisplay outputArea;
    private String slaveDir;
    private String targetDir;
    private Vector<FileInfo> masterFileInfos;

    public FileDirectorySyncer(IMessageDisplay outputArea) {
        this.outputArea = outputArea;
    }


    /*
   * return false if fail
    */
    public boolean syncItems(Vector<FileInfo> master, String slaveDir, String targetDir) {
        this.masterFileInfos = master;
        this.slaveDir = slaveDir;
        this.targetDir = targetDir;
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        if (!FileSyncerUtils.doChecking(masterFileInfos, slaveDirFile, targetDirFile, this.outputArea)) {
            return false;
        }
        // output image names
        this.outputArea.showMessage("im verzeichnis enthaltene bilder:\n", IMessageDisplay.VERBOSE);
        for (FileInfo s : master) {
            this.outputArea.showMessage("  " + s.toString()+"\n", IMessageDisplay.VERBOSE);
        }
        try {
            return doInBackground();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return false;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        Vector<File> toCopy = new Vector<File>();
        for (FileInfo fi : masterFileInfos) {
            for (File siblingInSlave : fi.GetSiblings(slaveDirFile)) {
                toCopy.add(siblingInSlave);
            }
        }
        FileSyncerUtils.doCopying(toCopy, targetDirFile, this.outputArea);
        return true;
    }
}
