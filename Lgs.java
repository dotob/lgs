import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;


public class Lgs extends TransferHandler implements ActionListener {
    private static final String versionString = "buildnumber";

    //TODO: make this configurable
    String[] ext = {"jpg", "xmp"};
    private JRadioButton dbRadioButton;
    private AlbumProvider albumProvider;
    private AlbumImageProvider albumImageProvider;
    private FileDBSyncer fileDBSyncer;
    private FileDirectorySyncer fileDirectorySyncer;

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Lgs lgsGui = new Lgs();
                    lgsGui.createAndShowGUI();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JFrame frame;
    private JTextField masterDirectory;
    private JComboBox masterAlbum;
    private JRadioButton dirRadioButton;
    private JTextField targetDirectory;
    private JTextField slaveDirectory;
    private JTextArea outputArea;

    private void createAndShowGUI() {
        String setLafResult = ""; //this.setLAF();
        this.outputArea = new JTextArea();
        this.outputArea.append(setLafResult);
        dbRadioButton = new JRadioButton("db-album");

        this.frame = new JFrame("lgs v" + versionString);
        this.frame.setIconImage(new ImageIcon("lgs.gif").getImage());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setPreferredSize(new Dimension(600, 500));
        Container contentPane = this.frame.getContentPane();
        GridBagLayout gbl = new GridBagLayout();
        contentPane.setLayout(gbl);
        double lWeight = 0.1;
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(1, 3, 1, 3);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = lWeight;
        contentPane.add(new JLabel("master"), gc);
        gc.gridx++;
        this.dirRadioButton = new JRadioButton("verzeichnis", true);
        this.dirRadioButton.setActionCommand("dir");
        this.dirRadioButton.addActionListener(this);
        this.dirRadioButton.setToolTipText("hier klicken um ein verzeichnis als master zu nutzen");
        contentPane.add(this.dirRadioButton, gc);
        gc.gridx++;
        gc.gridwidth = 5;
        gc.weightx = 1;
        this.masterDirectory = new JTextField();
        this.masterDirectory.setName("master");
        this.masterDirectory.setTransferHandler(this);
        this.masterDirectory.setToolTipText("<html>das <b>master</b>verzeichnis ist die vorlage. dateien die in diesem verzeichnis existieren, werden im <b>slave</b>verzeichnis gesucht und von dort (slave) ins <b>ziel</b>verzeichnis kopiert</html>");
        contentPane.add(this.masterDirectory, gc);
        gc.gridwidth = 1;
        gc.gridy++;
        gc.gridx = 1;
        gc.weightx = lWeight;
        dbRadioButton.setActionCommand("db");
        dbRadioButton.addActionListener(this);
        dbRadioButton.setToolTipText("hier klicken um ein datenbank-album als master zu nutzen");
        contentPane.add(dbRadioButton, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 5;
        this.masterAlbum = new JComboBox();

        this.masterAlbum.setActionCommand("dbalbums");
        this.masterAlbum.addActionListener(this);
        this.masterAlbum.setEnabled(false);
        this.masterAlbum.setMaximumRowCount(30);
        this.masterAlbum.setToolTipText("<html>ist ein album als <b>master</b> gewählt, so werden dateien aus dem album im <b>slave</b>verzeichnis gesucht und ins <b>ziel</b>verzeichnis kopiert.</html>");
        contentPane.add(this.masterAlbum, gc);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(this.dirRadioButton);
        bgroup.add(dbRadioButton);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        contentPane.add(new JLabel("slave"), gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 6;
        this.slaveDirectory = new JTextField();
        this.slaveDirectory.setName("slave");
        this.slaveDirectory.setTransferHandler(this);
        this.slaveDirectory.setToolTipText("<html>im <b>slave</b>verzeichnis liegen die <b>originale</b>. diese werden dann ins zielverzeichnis kopiert.<br /> dabei dient der master als vorlage welche dateien kopiert werden müssen</html>");
        contentPane.add(this.slaveDirectory, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        contentPane.add(new JLabel("ziel"), gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 6;
        this.targetDirectory = new JTextField();
        this.targetDirectory.setName("ziel");
        this.targetDirectory.setTransferHandler(this);
        this.targetDirectory.setToolTipText("<html>hier werden die dateien aus dem <b>slave</b>verzeichnis hin kopiert</html>");
        contentPane.add(this.targetDirectory, gc);

        gc.gridy++;
        gc.gridx = 3;
        gc.gridwidth = 2;
        gc.weightx = lWeight;
        JButton startButton = new JButton("start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        startButton.setToolTipText("das kopieren starten");
        contentPane.add(startButton, gc);

        gc.gridx += 2;
        JButton endButton = new JButton("ende");
        endButton.setToolTipText("lgs beenden");
        endButton.setActionCommand("end");
        endButton.addActionListener(this);
        contentPane.add(endButton, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 7;
        gc.gridheight = 8;
        JScrollPane jsp = new JScrollPane(this.outputArea);
        contentPane.add(jsp, gc);

        albumProvider = new AlbumProvider(this.masterAlbum, this.outputArea, this.dbRadioButton);
        albumImageProvider = new AlbumImageProvider(this.outputArea);
        this.fileDirectorySyncer = new FileDirectorySyncer(this.outputArea);
        this.fileDBSyncer = new FileDBSyncer(this.outputArea);

        // gather albums in background
        try {
            this.albumProvider.execute();
        } catch (Exception e) {
            this.outputArea.append(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        this.frame.pack();
        this.frame.setVisible(true);
    }

    private String setLAF() {
        String result = "";
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        System.setProperty("sun.awt.noerasebackground", "true");
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
            result = "black theme geladen\n";
        } catch (ClassNotFoundException e) {
            result = "black theme nicht gefunden\n";
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
            this.outputArea.append("black theme nicht gefunden\n");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            this.outputArea.append("black theme nicht gefunden\n");
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            this.outputArea.append("black theme nicht gefunden\n");
        }
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("start")) {
            this.startLGS();
        } else if (e.getActionCommand().equals("end")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("db")) {
            this.masterAlbum.setEnabled(true);
            this.masterDirectory.setEnabled(false);
        } else if (e.getActionCommand().equals("dir")) {
            this.masterAlbum.setEnabled(false);
            this.masterDirectory.setEnabled(true);
        } else if (e.getActionCommand().equals("dbalbums")) {
            // album is selected
            Album album = (Album) this.masterAlbum.getSelectedItem();
            this.albumImageProvider.setAlbum(album);
            try {
                this.albumImageProvider.execute();
            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    private void startLGS() {
        if (this.dirRadioButton.isSelected()) {
            this.outputArea.append("start with directory");
            // use supplied directory
            String masterDir = this.masterDirectory.getText();
            String slaveDir = this.slaveDirectory.getText();
            String targetDir = this.targetDirectory.getText();
            this.fileDirectorySyncer.syncItems(FileSyncerUtils.GetFileInfoItems(masterDir, this.ext), slaveDir, targetDir);
        } else {
            this.outputArea.append("start with db");
            // use album from db
            String slaveDir = this.slaveDirectory.getText();
            String targetDir = this.targetDirectory.getText();
            Album album = (Album) this.masterAlbum.getSelectedItem();
            this.albumImageProvider.setAlbum(album);
            Vector<String> foddos = null;
            try {
                // this is SYNC, but usually we retrieved the images already
                foddos = this.albumImageProvider.get();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.fileDBSyncer.syncItems(foddos, slaveDir, targetDir);
        }
    }

    /*
    DRAG & DROP
     */
    @Override
    public boolean importData(JComponent comp, Transferable t) {
        // Make sure we have the right starting points
        if (!(comp instanceof JTextField)) {
            return false;
        }
        if (!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false;
        }

        // Grab the tree, its model and the root node
        JTextField textField = (JTextField) comp;
        try {
            List data = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (Object object : data) {
                File f = (File) object;
                textField.setText(f.getAbsolutePath());
                Vector<FileInfo> fis = FileSyncerUtils.GetFileInfoItems(f.getAbsolutePath(), this.ext);
                this.outputArea.append(fis.size() + " dateien in " + textField.getName() + " gefunden\n");
            }
            return true;
        } catch (UnsupportedFlavorException ufe) {
            System.err.println("Ack! we should not be here.\nBad Flavor.");
        } catch (IOException ioe) {
            System.out.println("Something failed during import:\n" + ioe);
        }
        return false;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (comp instanceof JTextField) {
            for (DataFlavor transferFlavor : transferFlavors) {
                if (transferFlavor.equals(DataFlavor.javaFileListFlavor)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
