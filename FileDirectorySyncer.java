import javax.swing.*;
import java.io.File;
import java.util.Vector;

class FileDirectorySyncer extends SwingWorker<Boolean, Object> {
    private final IMessageDisplay outputArea;
    private String slaveDir;
    private String targetDir;
    private Vector<FileInfo> masterFileInfos;

    public FileDirectorySyncer(IMessageDisplay outputArea) {
        this.outputArea = outputArea;
    }


    /*
   * return false if fail
    */
    public void syncItems(String master, String[] ext, String slaveDir, String targetDir) {
        this.slaveDir = slaveDir;
        this.targetDir = targetDir;
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        this.masterFileInfos = FileSyncerUtils.GetFileInfoItems(master, ext);
        if (!FileSyncerUtils.doChecking(this.masterFileInfos, slaveDirFile, targetDirFile, this.outputArea)) {
            return;
        }
        // output image names
        this.outputArea.showMessage("im verzeichnis enthaltene bilder:\n", IMessageDisplay.VERBOSE);
        for (FileInfo s : this.masterFileInfos) {
            this.outputArea.showMessage("  " + s.GetString() + "\n", IMessageDisplay.VERBOSE);
        }
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        File slaveDirFile = new File(this.slaveDir);
        File targetDirFile = new File(this.targetDir);
        Vector<File> toCopy = new Vector<File>();
        for (FileInfo fi : this.masterFileInfos) {
            for (File siblingInSlave : fi.GetSiblings(slaveDirFile)) {
                toCopy.add(siblingInSlave);
            }
        }
        FileSyncerUtils.doCopying(toCopy, targetDirFile, this.outputArea);
        return true;
    }
}
