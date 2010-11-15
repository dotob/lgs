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


public class Lgs extends TransferHandler implements ActionListener, IMessageDisplay {
    private static final String versionString = "buildnumber";

    //TODO: make this configurable
    String[] ext = {"jpg", "xmp"};
    private JRadioButton dbRadioButton;
    private AlbumProvider albumProvider;
    private AlbumImageProvider albumImageProvider;
    private FileDBSyncer fileDBSyncer;
    private FileDirectorySyncer fileDirectorySyncer;
    private JCheckBox verboseOutputCB;

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
        Container realContentPane = this.frame.getContentPane();
        GridBagLayout gbl = new GridBagLayout();
        JPanel contentPane = new JPanel(gbl);
        contentPane.setLayout(gbl);
        double lWeight = 0.1;
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(1, 3, 1, 3);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = lWeight;

        String masterMsg = "<html>das <b>master</b>verzeichnis ist die vorlage. dateien die in diesem verzeichnis existieren, werden im <b>slave</b>verzeichnis gesucht und von dort (slave) ins <b>ziel</b>verzeichnis kopiert</html>";
        JLabel masterLabel = new JLabel("master");
        masterLabel.setToolTipText(masterMsg);
        contentPane.add(masterLabel, gc);
        gc.gridx++;
        this.dirRadioButton = new JRadioButton("verzeichnis", true);
        this.dirRadioButton.setActionCommand("dir");
        this.dirRadioButton.addActionListener(this);
        this.dirRadioButton.setToolTipText("hier klicken um ein verzeichnis als master zu nutzen");
        contentPane.add(this.dirRadioButton, gc);
        gc.gridx++;
        gc.gridwidth = 4;
        gc.weightx = 1;
        this.masterDirectory = new JTextField();
        this.masterDirectory.setName("master");
        this.masterDirectory.setTransferHandler(this);
        this.masterDirectory.setToolTipText(masterMsg);
        contentPane.add(this.masterDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 4;
        JButton browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseMaster");
        contentPane.add(browsebutton, gc);

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
        gc.gridwidth = 4;
        this.masterAlbum = new JComboBox();

        this.masterAlbum.setActionCommand("dbalbums");
        this.masterAlbum.addActionListener(this);
        this.masterAlbum.setEnabled(false);
        this.masterAlbum.setMaximumRowCount(30);
        this.masterAlbum.setToolTipText("<html>ist ein album als <b>master</b> gewählt, so werden dateien aus dem album im <b>slave</b>verzeichnis gesucht und ins <b>ziel</b>verzeichnis kopiert.</html>");
        contentPane.add(this.masterAlbum, gc);
        gc.gridx += 4;
        gc.weightx = lWeight;
        gc.gridwidth = 1;
        browsebutton = new JButton("*");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("updateAlbums");
        contentPane.add(browsebutton, gc);

        // radiobutton group
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(this.dirRadioButton);
        bgroup.add(dbRadioButton);

        gc.gridx = 0;
        gc.gridy++;
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        String slaveMsg = "<html>im <b>slave</b>verzeichnis liegen die <b>originale</b>. diese werden dann ins zielverzeichnis kopiert.<br /> dabei dient der master als vorlage welche dateien kopiert werden müssen</html>";
        JLabel slaveLabel = new JLabel("slave");
        slaveLabel.setToolTipText(slaveMsg);
        contentPane.add(slaveLabel, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 5;
        this.slaveDirectory = new JTextField();
        this.slaveDirectory.setName("slave");
        this.slaveDirectory.setTransferHandler(this);
        this.slaveDirectory.setToolTipText(slaveMsg);
        contentPane.add(this.slaveDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 5;
        browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseSlave");
        contentPane.add(browsebutton, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        String targetMsg = "<html>hier werden die dateien aus dem <b>slave</b>verzeichnis hin kopiert</html>";
        JLabel targetLabel = new JLabel("ziel");
        targetLabel.setToolTipText(targetMsg);
        contentPane.add(targetLabel, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 5;
        this.targetDirectory = new JTextField();
        this.targetDirectory.setName("ziel");
        this.targetDirectory.setTransferHandler(this);
        this.targetDirectory.setToolTipText(targetMsg);
        contentPane.add(this.targetDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 5;
        browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseTarget");
        contentPane.add(browsebutton, gc);

        gc.gridy++;
        gc.gridx = 1;
        gc.gridwidth = 2;
        gc.weightx = lWeight;
        verboseOutputCB = new JCheckBox("mehr info");
        verboseOutputCB.setToolTipText("detailliertere ausgaben machen");
        //TODO: make visible when we have more detailled output
        //verboseOutputCB.setVisible(false);
        contentPane.add(this.verboseOutputCB, gc);

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

        gatherAlbumsInBackground();

        realContentPane.add(contentPane, BorderLayout.CENTER);
        JLabel statusLabel = new JLabel("du bekommst hilfe wenn du die maus über ein element bewegst");
        realContentPane.add(statusLabel, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void gatherAlbumsInBackground() {
        albumProvider = new AlbumProvider(this.masterAlbum, this, this.dbRadioButton);

        // gather albums in background
        try {
            this.albumProvider.execute();
        } catch (Exception e) {
            this.outputArea.append(e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
        } else if (e.getActionCommand().equals("browseMaster")) {
            this.masterDirectory.setText(getPathFromUser("master verzeichnis auswählen"));
        } else if (e.getActionCommand().equals("browseSlave")) {
            this.slaveDirectory.setText(getPathFromUser("slave verzeichnis auswählen"));
        } else if (e.getActionCommand().equals("browseTarget")) {
            this.targetDirectory.setText(getPathFromUser("ziel verzeichnis auswählen"));
        } else if (e.getActionCommand().equals("updateAlbums")) {
            gatherAlbumsInBackground();
        } else if (e.getActionCommand().equals("dir")) {
            this.masterAlbum.setEnabled(false);
            this.masterDirectory.setEnabled(true);
        } else if (e.getActionCommand().equals("dbalbums")) {
            // album is selected
            Album album = (Album) this.masterAlbum.getSelectedItem();
            this.albumImageProvider = new AlbumImageProvider(this);
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
            this.outputArea.append("start with directory" + "\n");
            // use supplied directory
            String masterDir = this.masterDirectory.getText();
            String slaveDir = this.slaveDirectory.getText();
            String targetDir = this.targetDirectory.getText();
            this.fileDirectorySyncer = new FileDirectorySyncer(this);
            this.fileDirectorySyncer.syncItems(masterDir, this.ext, slaveDir, targetDir);
        } else {
            this.outputArea.append("start with db" + "\n");
            // use album from db
            String slaveDir = this.slaveDirectory.getText();
            String targetDir = this.targetDirectory.getText();
            Vector<String> foddos = null;
            try {
                // this is SYNC, but usually we retrieved the images already
                foddos = this.albumImageProvider.get();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.fileDBSyncer = new FileDBSyncer(this);
            this.fileDBSyncer.syncItems(foddos, slaveDir, targetDir);
        }
    }

    /*
    FILE CHOOOSER
     */
    private String getPathFromUser(String header) {
        String absPath = "";
        // not so nice...
//        JFileChooser fc = new JFileChooser();
//        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        int erg = fc.showDialog(this.frame, header);
//        if (erg == JFileChooser.APPROVE_OPTION) {
//            File file = fc.getSelectedFile();
//            absPath = file.getAbsolutePath();
//        }

        // better
        try {
            JFrame frame = new JFrame();
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
            FileDialog d = new FileDialog(frame);
            d.setVisible(true);
            absPath = d.getDirectory();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return absPath;
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

    @Override
    public void showMessage(String msg) {
        showMessage(msg, NORMAL);
    }

    @Override
    public void showMessage(String msg, int level) {
        if (level < VERBOSE || this.verboseOutputCB.isSelected()) {
            this.outputArea.append(msg);
        }
    }
}
