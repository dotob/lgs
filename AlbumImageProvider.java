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

class AlbumImageProvider extends SwingWorker<Vector<String>, Object> {
    private final IMessageDisplay outputArea;
    private final ConfigurationService configurationService;
    private Album album;

    public AlbumImageProvider(IMessageDisplay output, ConfigurationService configurationService) {
        this.outputArea = output;
        this.configurationService = configurationService;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    protected Vector<String> doInBackground() throws Exception {
        // if not already loaded load images
        if (this.album != null && !this.album.isInitiated()) {
            //retrieveFakeData();
            retrieveRealData();
        }
        if (this.album != null) {
            return this.album.getImages();
        }
        return new Vector<String>();
    }

    private void retrieveFakeData() {
        this.album.addImage("IMG_6762.jpg");
        this.album.addImage("IMG_6763.jpg");
        this.album.addImage("IMG_6764.jpg");
    }

    private void retrieveRealData() {
        try {
            // get last version info from internet
            URL updateURL = new URL(this.configurationService.GetImageUrl() + this.album.getId());
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
            this.outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
        } catch (UnknownHostException e) {
            this.outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
        } catch (ConnectException e) {
            this.outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
        } catch (MalformedURLException e) {
            this.outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
        } catch (IOException e) {
            this.outputArea.showMessage("Kein Zugang zum Internet gefunden." + "\n");
        }
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
