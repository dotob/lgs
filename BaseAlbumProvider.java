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

public class BaseAlbumProvider extends SwingWorker<Vector<Album>, Object> {
    private IMessageDisplay outputArea;
    private Vector<Album> albumList;

    public BaseAlbumProvider(IMessageDisplay output) {
        this.outputArea = output;
        this.albumList = new Vector<Album>();
    }

    @Override
    protected Vector<Album> doInBackground() throws Exception {
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

    @Override
    protected void done() {
        this.outputArea.showMessage(this.albumList.size() + " alben gefunden\n");
    }
}
