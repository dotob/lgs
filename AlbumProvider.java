import javax.swing.*;
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
            // add empty row to select nothing
            this.albumList.add(new Album());
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
