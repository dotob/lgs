import javax.swing.*;
import java.io.File;
import java.util.Vector;

class FileDBSyncer extends SwingWorker<Boolean, Object> {
    private final IMessageDisplay outputArea;
    private String slaveDir;
    private Vector<String> masterDBInfos;
    private String websearchURL;
    private Boolean useWebsearch;
    private String targetDir;
    private final WebSearchService webSearch;

    public FileDBSyncer(IMessageDisplay outputArea) {
        this.outputArea = outputArea;
        this.webSearch = new WebSearchService();
    }

    public void syncItemsDirBased(Vector<String> master, String slaveDir, String targetDir) {
        this.slaveDir = slaveDir;
        this.masterDBInfos = master;
        this.targetDir = targetDir;
        this.useWebsearch = false;

        File slaveDirFile = new File(slaveDir);
        File targetDirFile = new File(targetDir);
        if (!FileSyncerUtils.doChecking(this.masterDBInfos, slaveDirFile, targetDirFile, this.outputArea)) {
            return;
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

    public void syncItemsWebSearchBased(Vector<String> master, String targetDir, String websearchURL) {
        this.masterDBInfos = master;
        this.websearchURL = websearchURL;
        this.targetDir = targetDir;

        this.useWebsearch = true;

        // output image names
        this.outputArea.showMessage("im album enthaltene bilder:\n", IMessageDisplay.VERBOSE);
        for (String s : master) {
            this.outputArea.showMessage("  " + s + "\n", IMessageDisplay.VERBOSE);
        }
        try {
            this.doInBackground();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        File targetDirFile = new File(this.targetDir);
        Vector<File> toCopy = new Vector<File>();
        if (this.useWebsearch) {
            getFilesFromWebSearch(toCopy);
        } else {
            getFilesFromSlaveDir(toCopy);
        }
        FileSyncerUtils.doCopying(toCopy, targetDirFile, this.outputArea);
        return true;
    }

    private void getFilesFromWebSearch(Vector<File> toCopy) {
        for (String masterRequired : this.masterDBInfos) {
            String masterFileMatchName = FileInfo.GetMatchName(masterRequired);
            Vector<FileInformation> fileInformations = this.webSearch.Search4Files(this.websearchURL, masterFileMatchName);
            if (fileInformations.size() == 1) {
                toCopy.add(new File(fileInformations.get(0).getFileName()));
            } else {
                //TODO what now???
            }
        }
    }

    private void getFilesFromSlaveDir(Vector<File> toCopy) {
        // get all files, do not consider extensions
        for (FileInfo fi : FileSyncerUtils.GetFileInfoItems(this.slaveDir)) {
            for (String masterRequired : this.masterDBInfos) {
                if (fi.getMatchName().equals(FileInfo.GetMatchName(masterRequired))) {
                    toCopy.add(fi.getFile());
                }
            }
        }
    }
}
