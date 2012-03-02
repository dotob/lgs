import javax.swing.*;
import java.io.File;
import java.util.Vector;

public class FileDBSyncer extends SwingWorker<Boolean, Object> {
    private IMessageDisplay outputArea;
    private String slaveDir;
    private Vector<String> masterDBInfos;
    private String websearchURL;
    private Boolean useWebsearch;
    private String targetDir;

    public FileDBSyncer(IMessageDisplay outputArea) {
        this.outputArea = outputArea;
    }

    public void syncItems(Vector<String> master, String slaveDir, String websearchURL, Boolean useWebsearch, String targetDir) {
        this.slaveDir = slaveDir;
        this.masterDBInfos = master;
        this.websearchURL = websearchURL;
        this.useWebsearch = useWebsearch;
        this.targetDir = targetDir;
        if (!useWebsearch) {
            File slaveDirFile = new File(slaveDir);
            File targetDirFile = new File(targetDir);
            if (!FileSyncerUtils.doChecking(masterDBInfos, slaveDirFile, targetDirFile, this.outputArea)) {
                return;
            }
        }
        // output image names
        this.outputArea.showMessage("im album enthaltene bilder:\n", IMessageDisplay.VERBOSE);
        for (String s : master) {
            this.outputArea.showMessage("  " + s + "\n", IMessageDisplay.VERBOSE);
        }
        try {
            execute();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        File targetDirFile = new File(targetDir);
        Vector<File> toCopy = new Vector<File>();
        if (useWebsearch) {
            getFileFromWebSearch(toCopy);
        } else {
            getFilesFromSlaveDir(toCopy);

        }
        FileSyncerUtils.doCopying(toCopy, targetDirFile, this.outputArea);
        return true;
    }

    private void getFileFromWebSearch(Vector<File> toCopy) {
        for (String masterRequired : masterDBInfos) {
            String masterFileMatchName = FileInfo.GetMatchName(masterRequired);

        }
    }

    private void getFilesFromSlaveDir(Vector<File> toCopy) {
        // get all files, do not consider extensions
        for (FileInfo fi : FileSyncerUtils.GetFileInfoItems(slaveDir)) {
            for (String masterRequired : masterDBInfos) {
                if (fi.getMatchName().equals(FileInfo.GetMatchName(masterRequired))) {
                    toCopy.add(fi.getFile());
                }
            }
        }
    }
}
