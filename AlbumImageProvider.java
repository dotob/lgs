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
    private JTextArea outputArea;
    private Album album;

    public AlbumImageProvider(JTextArea output) {
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
                outputArea.append("Kein Zugang zum Internet gefunden.");
                // e.printStackTrace();
            } catch (UnknownHostException e) {
                outputArea.append("Kein Zugang zum Internet gefunden.");
                // e.printStackTrace();
            } catch (ConnectException e) {
                outputArea.append("Kein Zugang zum Internet gefunden.");
                // e.printStackTrace();
            } catch (MalformedURLException e) {
                outputArea.append("Kein Zugang zum Internet gefunden.");
                // MyLog.exceptionError(e);
                e.printStackTrace();
            } catch (IOException e) {
                outputArea.append("Kein Zugang zum Internet gefunden.");
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
            this.outputArea.append(+pics.size() + " bilder in album " + this.album.getName() + " gefunden\n");
        } catch (Exception ignore) {
        }
    }
}
