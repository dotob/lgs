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

public class AlbumProvider extends BaseAlbumProvider {
    private JRadioButton dbRadioButton;
    private JComboBox masterAlbum;
    private Vector<Album> albumList;

    public AlbumProvider(JComboBox masterAlbum, IMessageDisplay output, JRadioButton dbRadioButton) {
        super(output);
        this.masterAlbum = masterAlbum;
        this.dbRadioButton = dbRadioButton;
        this.albumList = new Vector<Album>();
    }

    @Override
    protected Vector<Album> doInBackground() throws Exception {
        this.dbRadioButton.setEnabled(false);
        try {
            super.execute();
        } catch (Exception e) {
            dbRadioButton.setEnabled(false);
        }
        return this.albumList;
    }

    @Override
    protected void done() {
        this.dbRadioButton.setEnabled(true);
        this.masterAlbum.setModel(new DefaultComboBoxModel(this.albumList));
    }
}
