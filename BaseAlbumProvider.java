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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BaseAlbumProvider extends SwingWorker<Vector<Album>, Object> {
    protected IMessageDisplay outputArea;
    private Vector<Album> albumList;

    public BaseAlbumProvider(IMessageDisplay output) {
        this.outputArea = output;
        this.albumList = new Vector<Album>();
    }

    @Override
    protected Vector<Album> doInBackground() throws Exception {
        try {
            // get last version info from internet
            URL updateURL = new URL("http://dev.thalora.com/php/index.php?mode=desktop_get_orders");

            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(updateURL.openStream()));
            List<Album> albums = new ArrayList<Album>();
            reader.beginArray();
            while (reader.hasNext()) {
                Album album = gson.fromJson(reader, Album.class);
                this.albumList.add(album);
            }
            reader.endArray();
            reader.close();

        } catch (FileNotFoundException e) {
            this.outputArea.showMessage("konnte album information nicht laden (FileNotFoundException)\n");
        } catch (UnknownHostException e) {
            this.outputArea.showMessage("konnte album information nicht laden (UnknownHostException)\n");
        } catch (ConnectException e) {
            this.outputArea.showMessage("konnte album information nicht laden (ConnectException)\n");
        } catch (MalformedURLException e) {
            this.outputArea.showMessage("konnte album information nicht laden (MalformedURLException)\n");
        } catch (IOException e) {
            this.outputArea.showMessage("konnte album information nicht laden (IOException)\n");
        }
        return this.albumList;
    }
}
