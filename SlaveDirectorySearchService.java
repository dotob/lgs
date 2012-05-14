import javax.swing.*;
import java.io.File;
import java.util.Vector;

// remember:
// master: where the info comes from which images to use
// slave: where the originals reside, we copy from here
// target: where the originals from slave should be copied to
class SlaveDirectorySearchService extends SwingWorker<File, Object> {

    private final Album handleAlbum;
    private final File target; // this is just for giving it to the next service


    // walk through the configured directories and search for the directory where the originals reside (slave directory)
    public SlaveDirectorySearchService(Album a, File target) {
        this.handleAlbum = a;
        this.target = target;
    }

    @Override
    protected File doInBackground() throws Exception {
        return new File("slaveteeeeest");  //TODO
    }

    public Album getHandleAlbum() {
        return this.handleAlbum;
    }

    public File getTarget() {
        return this.target;
    }
}
