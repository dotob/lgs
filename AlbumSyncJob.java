import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class AlbumSyncJob implements ActionListener, PropertyChangeListener {
    Timer checkTimer;
    int checkIntervalInSeconds = 60;
    private IMessageDisplay output;
    private BaseAlbumProvider albumProvider;
    private TargetDirectorySearchService targetDirectorySearchService;
    private SlaveDirectorySearchService slaveDirectorySearchService;
    private Vector<Album> lastSyncAlbums;
    private ConfigurationService configurationService;

    public AlbumSyncJob(IMessageDisplay output, ConfigurationService configurationService) {
        this.output = output;
        this.configurationService = configurationService;
    }

    public void StartChecking() {
        checkTimer = new Timer(checkIntervalInSeconds, this);
        checkTimer.setInitialDelay(checkIntervalInSeconds);
        checkTimer.start();
    }

    public void StopChecking() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
    }

    public void setTimerCheckingInterval(int newCheckIntervalInSeconds) {
        this.checkIntervalInSeconds = newCheckIntervalInSeconds;
        this.StopChecking();
        this.StartChecking();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        // this should be called when timer callback executes
        // check if albumfetching is in progress, then skip this timer, but tell someone
        if (albumProvider != null && !albumProvider.isDone()) {
            output.showMessage("letzter album check noch nicht fertig, überspringe dieses mal");
        } else {
            // ok albumprovider is null then create a new one and check
            this.albumProvider = new BaseAlbumProvider(this.output);
            this.albumProvider.addPropertyChangeListener(this);
            this.albumProvider.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        // who is throwing this event?
        if (propertyChangeEvent.getSource() == this.albumProvider) {
            albumProviderIsReady();
        } else if (propertyChangeEvent.getSource() == this.targetDirectorySearchService) {
            targetDirectorySearchServiceIsReady();
        } else if (propertyChangeEvent.getSource() == this.slaveDirectorySearchService) {
            slaveDirectorySearchServiceIsReady();
        }
    }

    private void albumProviderIsReady() {
        if (this.albumProvider != null && this.albumProvider.isDone()) {
            // check if this is first time
            if (this.lastSyncAlbums == null) {
                doFirstTimeCheck();
            } else {
                doCheck4NewAlbums();
            }
        }
    }

    private void targetDirectorySearchServiceIsReady() {
        // check if there is anything left to do or if it is already in sync
        if (this.targetDirectorySearchService != null && targetDirectorySearchService.isDone()) {
            try {
                File target = this.targetDirectorySearchService.get();
                Album handleAlbum = this.targetDirectorySearchService.getHandleAlbum();
                if (target != null) {
                    if (!TargetDirectorySearchService.IsAlbumAlreadyInSync(handleAlbum, target)) {
                        // ok we found an unsynced target dir, so go and search for slavedir
                        this.output.showMessage("search for slave directory for album:" + handleAlbum.getName());
                        this.targetDirectorySearchService = null; // do i need this?
                        this.slaveDirectorySearchService = new SlaveDirectorySearchService(handleAlbum, target, this.configurationService.getSlaveParentDirectories());
                        this.slaveDirectorySearchService.execute();
                    } else {
                        this.output.showMessage("album: " + handleAlbum.getName() + " schon fertig gesynced");
                    }
                } else {
                    this.output.showMessage("kein gültiges target gefunden für album: " + handleAlbum.getName());
                }
            } catch (InterruptedException e) {
                this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady:InterruptedException");
            } catch (ExecutionException e) {
                this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady:ExecutionException");
            }
        } else {
            this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady");
        }
    }

    private void slaveDirectorySearchServiceIsReady() {
        if (this.slaveDirectorySearchService != null && this.slaveDirectorySearchService.isDone()) {
            try {
                File slaveDir = this.slaveDirectorySearchService.get();
                Album handleAlbum = this.slaveDirectorySearchService.getHandleAlbum();
                if (slaveDir != null) {
                    this.output.showMessage("have slave directory gefunden: " + slaveDir.getAbsolutePath() + " für album: " + handleAlbum.getName());
                    // start sync
                    Vector<String> foddos = null;
                    try {
                        // this is SYNC, but usually we retrieved the images already
                        AlbumImageProvider aip = new AlbumImageProvider(this.output);
                        aip.setAlbum(handleAlbum);
                        foddos = aip.get(); // this is syncronous
                    } catch (Exception e) {
                        this.output.showMessage("slaveDirectorySearchServiceIsReady: beim laden der bilder für album: " + handleAlbum.getName() + " ist ein fehler aufgetreten: " + e.getMessage());
                    }
                    FileDBSyncer fdbs = new FileDBSyncer(this.output);
                    fdbs.syncItems(foddos, slaveDir.getAbsolutePath(), this.slaveDirectorySearchService.getTarget().getAbsolutePath());
                } else {
                    this.output.showMessage("kein gültiges target gefunden für album: " + handleAlbum.getName());

                }
            } catch (InterruptedException e) {
                this.output.showMessage("something went wrong here...slaveDirectorySearchServiceIsReady:InterruptedException");
            } catch (ExecutionException e) {
                this.output.showMessage("something went wrong here...slaveDirectorySearchServiceIsReady:InterruptedException");
            }
        }
    }

    private void doCheck4NewAlbums() {
        Vector<Album> albums = new Vector<Album>();
        try {
            albums = this.albumProvider.get();
        } catch (InterruptedException e) {
            this.output.showMessage("doCheck4NewAlbums: something bad happened... " + e.getMessage());
        } catch (ExecutionException e) {
            this.output.showMessage("doCheck4NewAlbums: something bad happened... " + e.getMessage());
        }
        Vector<Album> todoList = new Vector<Album>();
        // compare album vectors
        for (Album a : albums) {
            if (!this.lastSyncAlbums.contains(a)) {
                // add it to todolist
                todoList.add(a);
                this.output.showMessage("album " + a.getName() + " mit " + a.getCount() + " bildern zur todoliste hinzugefügt");
            }
        }
        if (todoList.size() > 0) {
            for (Album a : todoList) {
                // find target (this is async...)
                this.targetDirectorySearchService = new TargetDirectorySearchService(a);
                // start syncing
                this.targetDirectorySearchService.execute();
            }
        }
    }

    private void doFirstTimeCheck() {
        try {
            this.lastSyncAlbums = this.albumProvider.get();
        } catch (InterruptedException e) {
            this.output.showMessage("something bad happened... " + e.getMessage());
        } catch (ExecutionException e) {
            this.output.showMessage("something bad happened... " + e.getMessage());
        }
    }
}
