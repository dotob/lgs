import javax.swing.*;
import java.io.File;

// remember:
// master: where the info comes from which images to use
// slave: where the originals reside, we copy from here
// target: where the originals from slave should be copied to
public class TargetDirectorySearchService extends SwingWorker<File, Object> {
    private Album handleAlbum;

    // find the directory where this album images should be copied to from slave directory
    public TargetDirectorySearchService(Album a) {
        handleAlbum = a;
    }


    @Override
    protected File doInBackground() throws Exception {
        return new File("targetteeeest");  //To change body of implemented methods use File | Settings | File Templates.
    }

    // is there already a targetdirectory and are all images already there?
    public static boolean IsAlbumAlreadyInSync(Album a, File targetDir) {
        return false;
    }

    public Album getHandleAlbum() {
        return handleAlbum;
    }
}
