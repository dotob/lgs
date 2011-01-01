import javax.swing.*;
import java.io.File;
import java.util.Vector;

// remember:
// master: where the info comes from which images to use
// slave: where the originals reside, we copy from here
// target: where the originals from slave should be copied to
public class SlaveDirectorySearchService extends SwingWorker<File, Object> {

    private Album handleAlbum;
    private Vector<File> configuredParentPathsToSearchIn;
    private File target; // this is just for giving it to the next service


    // walk through the configured directories and search for the directory where the originals reside (slave directory)
    public SlaveDirectorySearchService(Album a, File target, Vector<File> configuredParentPathsToSearchIn) {
        this.handleAlbum = a;
        this.target = target;
        this.configuredParentPathsToSearchIn = configuredParentPathsToSearchIn;
    }

    @Override
    protected File doInBackground() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Album getHandleAlbum() {
        return handleAlbum;
    }

    public File getTarget() {
        return target;
    }
}
