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

public class AlbumProvider extends SwingWorker<Vector<Album>, Object> {
    private JTextArea outputArea;
    private JRadioButton dbRadioButton;
    private JComboBox masterAlbum;
    private Vector<Album> albumList;

    public AlbumProvider(JComboBox masterAlbum, JTextArea output, JRadioButton dbRadioButton) {
        this.masterAlbum = masterAlbum;
        this.outputArea = output;
        this.dbRadioButton = dbRadioButton;
        this.albumList = new Vector<Album>();
    }

    @Override
    protected Vector<Album> doInBackground() throws Exception {
        this.dbRadioButton.setEnabled(false);
        this.albumList.add(new Album());
        try {
            // get last version info from internet
            URL updateURL = new URL("http://www.lichtographie.de/lgsFuncs.php");
            BufferedReader in = new BufferedReader(new InputStreamReader(updateURL.openStream()));
            String album;
            while ((album = in.readLine()) != null) {
                String albumName = album.substring(album.indexOf(";") + 1).trim();
                String albumID = album.substring(0, album.indexOf(";")).trim();
                Album a = new Album();
                a.setId(albumID);
                a.setName(albumName);
                this.albumList.add(a);
            }
            // close stream
            in.close();
        } catch (FileNotFoundException e) {
            this.outputArea.append("konnte album information nicht laden (FileNotFoundException)\n");
            dbRadioButton.setEnabled(false);
        } catch (UnknownHostException e) {
            this.outputArea.append("konnte album information nicht laden (UnknownHostException)\n");
            dbRadioButton.setEnabled(false);
        } catch (ConnectException e) {
            this.outputArea.append("konnte album information nicht laden (ConnectException)\n");
            dbRadioButton.setEnabled(false);
        } catch (MalformedURLException e) {
            this.outputArea.append("konnte album information nicht laden (MalformedURLException)\n");
            dbRadioButton.setEnabled(false);
        } catch (IOException e) {
            this.outputArea.append("konnte album information nicht laden (IOException)\n");
            dbRadioButton.setEnabled(false);
        }
        return this.albumList;
    }

    @Override
    protected void done() {
        this.dbRadioButton.setEnabled(true);
        this.masterAlbum.setModel(new DefaultComboBoxModel(this.albumList));
        this.outputArea.append(this.albumList.size() + " alben gefunden\n");
    }
}
