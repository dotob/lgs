import javax.swing.*;
import java.io.File;

// remember:
// master: where the info comes from which images to use
// slave: where the originals reside, we copy from here
// target: where the originals from slave should be copied to
class TargetDirectorySearchService extends SwingWorker<File, Object> {
    private final Album handleAlbum;

    // find the directory where this album images should be copied to from slave directory
    public TargetDirectorySearchService(Album a) {
        this.handleAlbum = a;
    }


    @Override
    protected File doInBackground() throws Exception {
        return new File(this.handleAlbum.getLogin()+"_"+ this.handleAlbum.getId());
    }

    // is there already a targetdirectory and are all images already there?
    public static boolean IsAlbumAlreadyInSync(Album a, File targetDir) {
        return false;
    }

    public Album getHandleAlbum() {
        return this.handleAlbum;
    }
}
