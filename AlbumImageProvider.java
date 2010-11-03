import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Vector;

public class AlbumImageProvider extends SwingWorker<Vector<String>, Object> {
    private IMessageDisplay outputArea;
    private Album album;

    public AlbumImageProvider(IMessageDisplay output) {
        this.outputArea = output;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    protected Vector<String> doInBackground() throws Exception {
        // if not already loaded load images
        if (this.album != null && !this.album.isInitiated()) {
            try {
                // get last version info from internet
                URL updateURL = new URL("http://www.lichtographie.de/lgsFuncs.php?album=" + this.album.getId());
                BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    this.album.addImage(inputLine);
                }
                // close stream
                in.close();
            } catch (FileNotFoundException e) {
                outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
                // e.printStackTrace();
            } catch (UnknownHostException e) {
                outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
                // e.printStackTrace();
            } catch (ConnectException e) {
                outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
                // e.printStackTrace();
            } catch (MalformedURLException e) {
                outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
                // MyLog.exceptionError(e);
                e.printStackTrace();
            } catch (IOException e) {
                outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
                // MyLog.exceptionError(e);
                e.printStackTrace();
            }
        }
        return this.album.getImages();
    }

    @Override
    protected void done() {
        try {
            Vector<String> pics = get();
            int i = 0;
            for (String pic : pics) {
                this.outputArea.showMessage("  " + i++ + ": " + pic + "\n", IMessageDisplay.VERBOSE);
            }
            this.outputArea.showMessage(+pics.size() + " bilder in album " + this.album.getName() + " gefunden\n");
        } catch (Exception ignore) {
        }
    }
}
