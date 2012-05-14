import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(Lgs.class);

    //TODO: make this configurable
    private final String[] ext = {"jpg", "xmp"};
    private JRadioButton dbRadioButton;
    private AlbumImageProvider albumImageProvider;
    private JCheckBox verboseOutputCB;
    private AlbumSyncJob albumSyncJob;
    private final ConfigurationService confService;
    private final JTextArea outputAreaAutoSync = new JTextArea();
    private final JTextArea outputAreaManualSync = new JTextArea();
    private final GrowlNetwork growl = new GrowlNetwork();
    private final MyMessageDisplay manualSyncOutput = new MyMessageDisplay(this.outputAreaManualSync, this.growl);
    private final MyMessageDisplay autoSyncOutput = new MyMessageDisplay(this.outputAreaAutoSync, this.growl);
    private JFrame frame;
    private JTextField masterDirectory;
    private JComboBox masterAlbum;
    private JRadioButton dirRadioButton;
    private JTextField targetDirectory;
    private JTextField slaveDirectory;
    private JButton startAutoSyncButton;
    private JButton stopAutoSyncButton;
    private JRadioButton websearchRadioButton;
    private JTextField websearchURL;

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    //logger.debug("create lgs");
                    Lgs lgsGui = new Lgs();
                    lgsGui.createAndShowGUI();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Lgs() {
        this.confService = new ConfigurationService();
    }

    private void createAndShowGUI() {
        logger.debug("createAndShowGUI");
        String setLafResult = ""; //this.setLAF();
        this.outputAreaManualSync.append(setLafResult);
        this.dbRadioButton = new JRadioButton("db-album", true);
        this.websearchRadioButton = new JRadioButton("suchservice");

        this.frame = new JFrame("lgs v" + versionString);
        this.frame.setIconImage(new ImageIcon("lgs.gif").getImage());
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setPreferredSize(new Dimension(900, 500));
        Container realContentPane = this.frame.getContentPane();

        JPanel manualSyncPanel = createManualSyncPanel();
        JPanel automaticSyncPanel = createAutomaticSyncPanel();

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.add("handsync", manualSyncPanel);
        mainTabs.add("autosync", automaticSyncPanel);
        mainTabs.setSelectedIndex(0);

        realContentPane.add(mainTabs, BorderLayout.CENTER);
        JLabel statusLabel = new JLabel("du bekommst hilfe wenn du die maus über ein element bewegst");
        realContentPane.add(statusLabel, BorderLayout.SOUTH);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    private JPanel createAutomaticSyncPanel() {
        GridBagLayout gbl = new GridBagLayout();
        JPanel autoSyncPanel = new JPanel(gbl);

        double lWeight = 0.1;
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(1, 3, 1, 3);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 4;
        gc.weightx = lWeight;

        this.startAutoSyncButton = new JButton("start");
        this.startAutoSyncButton.setActionCommand("startAutoSync");
        this.startAutoSyncButton.addActionListener(this);
        autoSyncPanel.add(this.startAutoSyncButton, gc);

        gc.gridx = 4;
        gc.gridy = 0;
        gc.gridwidth = 4;
        gc.weightx = lWeight;

        this.stopAutoSyncButton = new JButton("stop");
        this.stopAutoSyncButton.setActionCommand("stopAutoSync");
        this.stopAutoSyncButton.addActionListener(this);
        this.stopAutoSyncButton.setEnabled(false);
        autoSyncPanel.add(this.stopAutoSyncButton, gc);


        // what do we need?
        // timeinterval chooser
        // start/stop-button
        // list of directories where to look for slavedir
        // output textpane
        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 8;
        gc.gridheight = 8;
        JScrollPane jsp = new JScrollPane(this.outputAreaAutoSync);
        autoSyncPanel.add(jsp, gc);

        this.albumSyncJob = new AlbumSyncJob(this.autoSyncOutput, this.confService);

        return autoSyncPanel;
    }

    private JPanel createManualSyncPanel() {
        GridBagLayout gbl = new GridBagLayout();
        JPanel manualSyncPanel = new JPanel(gbl);
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
        manualSyncPanel.add(masterLabel, gc);
        gc.gridx++;
        this.dirRadioButton = new JRadioButton("verzeichnis");
        this.dirRadioButton.setActionCommand("dir");
        this.dirRadioButton.addActionListener(this);
        this.dirRadioButton.setToolTipText("hier klicken um ein verzeichnis als master zu nutzen");
        manualSyncPanel.add(this.dirRadioButton, gc);
        gc.gridx++;
        gc.gridwidth = 4;
        gc.weightx = 1;
        this.masterDirectory = new JTextField();
        this.masterDirectory.setName("master");
        this.masterDirectory.setTransferHandler(this);
        this.masterDirectory.setToolTipText(masterMsg);
        this.masterDirectory.setEnabled(false);
        manualSyncPanel.add(this.masterDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 4;
        JButton browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseMaster");
        manualSyncPanel.add(browsebutton, gc);

        gc.gridwidth = 1;
        gc.gridy++;
        gc.gridx = 1;
        gc.weightx = lWeight;
        this.dbRadioButton.setActionCommand("db");
        this.dbRadioButton.addActionListener(this);
        this.dbRadioButton.setToolTipText("hier klicken um ein datenbank-album als master zu nutzen");
        manualSyncPanel.add(this.dbRadioButton, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 4;
        this.masterAlbum = new JComboBox();
        this.masterAlbum.setActionCommand("dbalbums");
        this.masterAlbum.addActionListener(this);
        this.masterAlbum.setMaximumRowCount(30);
        this.masterAlbum.setToolTipText("<html>ist ein album als <b>master</b> gewählt, so werden dateien aus dem album im <b>slave</b>verzeichnis gesucht und ins <b>ziel</b>verzeichnis kopiert.</html>");
        manualSyncPanel.add(this.masterAlbum, gc);
        gc.gridx += 4;
        gc.weightx = lWeight;
        gc.gridwidth = 1;
        browsebutton = new JButton("*");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("updateAlbums");
        manualSyncPanel.add(browsebutton, gc);

        // radiobutton group
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(this.dirRadioButton);
        bgroup.add(this.dbRadioButton);


        gc.gridy++;
        gc.gridx = 0;
        String slaveMsg = "<html>im <b>slave</b>verzeichnis liegen die <b>originale</b>. diese werden dann ins zielverzeichnis kopiert.<br /> dabei dient der master als vorlage welche dateien kopiert werden müssen</html>";
        JLabel slaveLabel = new JLabel("slave");
        slaveLabel.setToolTipText(slaveMsg);
        manualSyncPanel.add(slaveLabel, gc);
        gc.gridx++;
        JRadioButton slaveDirRadioButton = new JRadioButton("verzeichnis", true);
        slaveDirRadioButton.setActionCommand("slavedir");
        slaveDirRadioButton.addActionListener(this);
        slaveDirRadioButton.setToolTipText("hier klicken um ein verzeichnis als master zu nutzen");
        manualSyncPanel.add(slaveDirRadioButton, gc);
        gc.gridx++;
        gc.gridwidth = 4;
        gc.weightx = 1;
        this.slaveDirectory = new JTextField();
        this.slaveDirectory.setName("slave");
        this.slaveDirectory.setTransferHandler(this);
        this.slaveDirectory.setToolTipText(slaveMsg);
        this.slaveDirectory.setEnabled(false);
        manualSyncPanel.add(this.slaveDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 4;
        browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseSlave");
        manualSyncPanel.add(browsebutton, gc);

        gc.gridwidth = 1;
        gc.gridy++;
        gc.gridx = 1;
        gc.weightx = lWeight;
        String websearchMsg = "<html>ist der webservice als <b>slave</b> gewählt, so werden die master-dateien vom webservice gesucht</html>";
        this.websearchRadioButton.setActionCommand("websearch");
        this.websearchRadioButton.addActionListener(this);
        this.websearchRadioButton.setToolTipText(websearchMsg);
        manualSyncPanel.add(this.websearchRadioButton, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 4;
        this.websearchURL = new JTextField(this.confService.GetLssmSearchUrl());
        this.websearchURL.setName("websearch");
        this.websearchURL.setTransferHandler(this);
        this.websearchURL.setToolTipText(websearchMsg);
        manualSyncPanel.add(this.websearchURL, gc);

        // radiobutton group
        ButtonGroup bgroupSlave = new ButtonGroup();
        bgroupSlave.add(slaveDirRadioButton);
        bgroupSlave.add(this.websearchRadioButton);


        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        String targetMsg = "<html>hier werden die dateien aus dem <b>slave</b>verzeichnis hin kopiert</html>";
        JLabel targetLabel = new JLabel("ziel");
        targetLabel.setToolTipText(targetMsg);
        manualSyncPanel.add(targetLabel, gc);
        gc.gridx++;
        gc.weightx = 1;
        gc.gridwidth = 5;
        this.targetDirectory = new JTextField();
        this.targetDirectory.setName("ziel");
        this.targetDirectory.setTransferHandler(this);
        this.targetDirectory.setToolTipText(targetMsg);
        manualSyncPanel.add(this.targetDirectory, gc);
        gc.gridwidth = 1;
        gc.weightx = lWeight;
        gc.gridx += 5;
        browsebutton = new JButton("...");
        browsebutton.addActionListener(this);
        browsebutton.setActionCommand("browseTarget");
        manualSyncPanel.add(browsebutton, gc);

        gc.gridy++;
        gc.gridx = 1;
        gc.gridwidth = 2;
        gc.weightx = lWeight;
        this.verboseOutputCB = new JCheckBox("mehr info");
        this.verboseOutputCB.setToolTipText("detailliertere ausgaben machen");
        this.verboseOutputCB.setActionCommand("setVerboseOutput");
        this.verboseOutputCB.addActionListener(this);
        //TODO: make visible when we have more detailled output
        //verboseOutputCB.setVisible(false);
        manualSyncPanel.add(this.verboseOutputCB, gc);

        gc.gridx = 3;
        gc.gridwidth = 2;
        gc.weightx = lWeight;
        JButton startButton = new JButton("start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);
        startButton.setToolTipText("das kopieren starten");
        manualSyncPanel.add(startButton, gc);

        gc.gridx += 2;
        JButton endButton = new JButton("ende");
        endButton.setToolTipText("lgs beenden");
        endButton.setActionCommand("end");
        endButton.addActionListener(this);
        manualSyncPanel.add(endButton, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = 7;
        gc.gridheight = 8;
        JScrollPane jsp = new JScrollPane(this.outputAreaManualSync);
        manualSyncPanel.add(jsp, gc);

        gatherAlbumsInBackground();
        return manualSyncPanel;
    }

    private void gatherAlbumsInBackground() {
        AlbumProvider albumProvider = new AlbumProvider(this.masterAlbum, this.manualSyncOutput, this.dbRadioButton, this.confService);
        // gather albums in background
        try {
            albumProvider.execute();
        } catch (Exception e) {
            logger.debug("error while executing albumprovider", e);
            this.outputAreaManualSync.append(e.getMessage());
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
            this.manualSyncOutput.showMessage("black theme nicht gefunden\n");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            this.manualSyncOutput.showMessage("black theme nicht gefunden\n");
        } catch (UnsupportedLookAndFeelException e) {
            logger.debug("error while setting look and feel", e);
            this.manualSyncOutput.showMessage("black theme nicht gefunden\n");
        }
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.debug("actionperformed:" + e.getActionCommand());
        if (e.getActionCommand().equals("start")) {
            this.startLGS();
        } else if (e.getActionCommand().equals("end")) {
            this.frame.dispose();
        } else if (e.getActionCommand().equals("startAutoSync")) {
            this.albumSyncJob.setUseWebsearch(true);
            this.albumSyncJob.setWebSearchServiceURL(this.websearchURL.getText());
            this.albumSyncJob.StartChecking();
            this.startAutoSyncButton.setEnabled(false);
            this.stopAutoSyncButton.setEnabled(true);
        } else if (e.getActionCommand().equals("stopAutoSync")) {
            this.albumSyncJob.StopChecking();
            this.startAutoSyncButton.setEnabled(true);
            this.stopAutoSyncButton.setEnabled(false);
        } else if (e.getActionCommand().equals("db")) {
            this.masterAlbum.setEnabled(true);
            this.masterDirectory.setEnabled(false);
        } else if (e.getActionCommand().equals("websearch")) {
            this.websearchURL.setEnabled(true);
            this.slaveDirectory.setEnabled(false);
        } else if (e.getActionCommand().equals("slavedir")) {
            this.websearchURL.setEnabled(false);
            this.slaveDirectory.setEnabled(true);
        } else if (e.getActionCommand().equals("browseMaster")) {
            this.masterDirectory.setText(getPathFromUser());
        } else if (e.getActionCommand().equals("browseSlave")) {
            this.slaveDirectory.setText(getPathFromUser());
        } else if (e.getActionCommand().equals("browseTarget")) {
            this.targetDirectory.setText(getPathFromUser());
        } else if (e.getActionCommand().equals("updateAlbums")) {
            gatherAlbumsInBackground();
        } else if (e.getActionCommand().equals("setVerboseOutput")) {
            this.manualSyncOutput.SetVerboseOutput(this.verboseOutputCB.isSelected());
        } else if (e.getActionCommand().equals("dir")) {
            this.masterAlbum.setEnabled(false);
            this.masterDirectory.setEnabled(true);
        } else if (e.getActionCommand().equals("dbalbums")) {
            selectDbAlbum();
        }
    }

    private void selectDbAlbum() {
        // album is selected
        Album album = (Album) this.masterAlbum.getSelectedItem();
        String orderTargetPath = this.confService.GetOrderTargetPath() + album.getId() + "_" + album.getLogin();
        this.targetDirectory.setText(orderTargetPath);
        this.albumImageProvider = new AlbumImageProvider(this.manualSyncOutput, this.confService);
        this.albumImageProvider.setAlbum(album);
        try {
            this.albumImageProvider.execute();
        } catch (Exception e) {
            logger.debug("error while executing albumprovider", e);
        }
    }


    private void startLGS() {
        if (this.dirRadioButton.isSelected()) {
            logger.debug("start lgs with dir");
            this.manualSyncOutput.showMessage("start with directory" + "\n");
            // use supplied directory
            String masterDir = this.masterDirectory.getText();
            String slaveDir = this.slaveDirectory.getText();
            String targetDir = this.targetDirectory.getText();
            FileDirectorySyncer fileDirectorySyncer = new FileDirectorySyncer(this.manualSyncOutput);
            fileDirectorySyncer.syncItems(masterDir, this.ext, slaveDir, targetDir);
        } else {
            logger.debug("start lgs with db");
            this.manualSyncOutput.showMessage("start with db" + "\n");
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
            FileDBSyncer fileDBSyncer = new FileDBSyncer(this.manualSyncOutput);
            fileDBSyncer.syncItemsDirBased(foddos, slaveDir, targetDir);
        }
    }

    /*
    FILE CHOOOSER
     */
    private String getPathFromUser() {
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
            absPath += d.getFile();
        } catch (Exception e) {
            logger.debug("error while getting path from user", e);
        }
        logger.debug("got path from user:", absPath);
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
                this.manualSyncOutput.showMessage(fis.size() + " dateien in " + textField.getName() + " gefunden\n");
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

class MyMessageDisplay implements IMessageDisplay {

    private final JTextArea output;
    private boolean verbose;
    private final GrowlNetwork growl;

    public MyMessageDisplay(JTextArea output, GrowlNetwork growl) {
        this.output = output;
        this.growl = growl;
    }

    public void SetVerboseOutput(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void showMessage(String msg, int level) {
        if (level < VERBOSE || this.verbose) {
            this.output.append(msg);
            // never growl verbose messages
            if (level < VERBOSE) {
                this.growl.notify("lgs info", msg);
            }
        }
    }

    @Override
    public void showMessage(String msg) {
        showMessage(msg, NORMAL);
    }
}
