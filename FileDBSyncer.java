import javax.swing.*;
import java.io.File;
import java.util.Vector;

public class FileDBSyncer extends SwingWorker<Boolean, Object> {
    private IMessageDisplay outputArea;
    private String slaveDir;
    private Vector<String> masterDBInfos;
    private String targetDir;

    public FileDBSyncer(IMessageDisplay outputArea) {
        this.outputArea = outputArea;
    }

    public boolean syncItems(Vector<String> master, String slaveDir, String targetDir) {
        this.slaveDir = slaveDir;
        this.masterDBInfos = master;
        this.targetDir = targetDir;
        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        if (!FileSyncerUtils.doChecking(masterDBInfos, slaveDirFile, targetDirFile, this.outputArea)) {
            return false;
        }
        // output image names
        this.outputArea.showMessage("im album enthaltene bilder:\n", IMessageDisplay.VERBOSE);
        for (String s : master) {
            this.outputArea.showMessage("  " + s+"\n", IMessageDisplay.VERBOSE);
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
        File targetDirFile = new File(targetDir);
        Vector<File> toCopy = new Vector<File>();
        // get all files, do not consider extensions
        for (FileInfo fi : FileSyncerUtils.GetFileInfoItems(slaveDir)) {
            for (String masterRequired : masterDBInfos) {
                if (fi.getMatchName().equals(masterRequired)) {
                    toCopy.add(fi.getFile());
                }
            }
        }
        FileSyncerUtils.doCopying(toCopy, targetDirFile, this.outputArea);
        return true;
    }
}
