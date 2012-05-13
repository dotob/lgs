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

public class AlbumProvider extends SwingWorker<Vector<Album>, Object> {
    protected IMessageDisplay outputArea;
    private JRadioButton dbRadioButton;
    private ConfigurationService confService;
    private JComboBox masterAlbum;
    private Vector<Album> albumList;
    private int executeCount = 0;

    public AlbumProvider(JComboBox masterAlbum, IMessageDisplay output, JRadioButton dbRadioButton, ConfigurationService confService) {
        //super(output);
        this.outputArea = output;
        this.masterAlbum = masterAlbum;
        this.dbRadioButton = dbRadioButton;
        this.confService = confService;
        this.albumList = new Vector<Album>();
    }

    @Override
    protected Vector<Album> doInBackground() throws Exception {
        this.dbRadioButton.setEnabled(false);
        // add empty row to select nothing
        this.albumList.add(new Album());

        retrieveRealData();
        //retrieveFakeData();

        this.executeCount++;

        return this.albumList;
    }

    private void retrieveFakeData() {
        this.albumList.add(new Album("test1", "2", "1"));
        if (this.executeCount == 4) {
            this.albumList.add(new Album("test2", "2", "2"));
        }
    }

    private void retrieveRealData() {
        try {
            try {
                // get last version info from internet
                //URL updateURL = new URL("http://dev.thalora.com/php/index.php?mode=desktop_get_orders");
                URL updateURL = new URL(confService.GetAlbumUrl());

                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(updateURL.openStream()));
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
        } catch (Exception e) {
            dbRadioButton.setEnabled(false);
        }
    }

    @Override
    protected void done() {
        this.outputArea.showMessage(this.albumList.size() + " alben gefunden\n");
        this.dbRadioButton.setEnabled(true);
        this.masterAlbum.setModel(new DefaultComboBoxModel(this.albumList));
    }
}
