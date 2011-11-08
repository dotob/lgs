import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import javax.swing.*;
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
                URL updateURL = new URL("http://dev.thalora.com/php/index.php?mode=desktop_get_orders&id=" + this.album.getId());
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(updateURL.openStream()));
                reader.beginArray();
                while (reader.hasNext()) {
                    Image image = gson.fromJson(reader, Image.class);
                    this.album.addImage(image.getFilenameOrig());
                }
                reader.endArray();
                reader.close();
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
