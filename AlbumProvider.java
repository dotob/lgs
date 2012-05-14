import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Vector;

class AlbumProvider extends SwingWorker<Vector<Album>, Object> {
    private static final Logger logger = LoggerFactory.getLogger(AlbumProvider.class);

    private final IMessageDisplay outputArea;
    private final JRadioButton dbRadioButton;
    private final ConfigurationService confService;
    private final JComboBox masterAlbum;
    private final Vector<Album> albumList;
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
                URL updateURL = new URL(this.confService.GetAlbumUrl());
                logger.debug("use url for albums: {}",updateURL);
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
                logger.error("konnte album information nicht laden (FileNotFoundException)\n");
                this.outputArea.showMessage("konnte album information nicht laden (FileNotFoundException)\n");
            } catch (UnknownHostException e) {
                logger.error("konnte album information nicht laden (UnknownHostException)\n");
                this.outputArea.showMessage("konnte album information nicht laden (UnknownHostException)\n");
            } catch (ConnectException e) {
                logger.error("konnte album information nicht laden (ConnectException)\n");
                this.outputArea.showMessage("konnte album information nicht laden (ConnectException)\n");
            } catch (MalformedURLException e) {
                logger.error("konnte album information nicht laden (MalformedURLException)\n");
                this.outputArea.showMessage("konnte album information nicht laden (MalformedURLException)\n");
            } catch (IOException e) {
                logger.error("konnte album information nicht laden (IOException)\n");
                this.outputArea.showMessage("konnte album information nicht laden (IOException)\n");
            }
        } catch (Exception e) {
            logger.error("error while retrieving data", e);
            this.dbRadioButton.setEnabled(false);
        }
    }

    @Override
    protected void done() {
        String msg = this.albumList.size() + " alben gefunden\n";
        logger.info(msg);
        this.outputArea.showMessage(msg);
        this.dbRadioButton.setEnabled(true);
        this.masterAlbum.setModel(new DefaultComboBoxModel(this.albumList));
    }
}
