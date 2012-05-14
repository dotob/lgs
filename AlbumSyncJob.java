import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

class AlbumSyncJob implements ActionListener, PropertyChangeListener {
    private int checkCounter = 0;
    private Timer checkTimer;
    private int checkIntervalInSeconds = 3;
    private final IMessageDisplay output;
    private BaseAlbumProvider albumProvider;
    private TargetDirectorySearchService targetDirectorySearchService;
    private SlaveDirectorySearchService slaveDirectorySearchService;
    private Vector<Album> lastSyncAlbums;
    private final ConfigurationService configurationService;
    private int execCount = 1;

    public String getWebSearchServiceURL() {
        return this.webSearchServiceURL;
    }

    public void setWebSearchServiceURL(String webSearchServiceURL) {
        this.webSearchServiceURL = webSearchServiceURL;
    }

    public Boolean getUseWebsearch() {
        return this.useWebsearch;
    }

    public void setUseWebsearch(Boolean useWebsearch) {
        this.useWebsearch = useWebsearch;
    }

    private String webSearchServiceURL;
    private Boolean useWebsearch;

    public AlbumSyncJob(IMessageDisplay output, ConfigurationService configurationService) {
        this.output = output;
        this.configurationService = configurationService;
    }

    public void StartChecking() {
        this.checkCounter = 0;
        this.lastSyncAlbums = null;
        this.checkTimer = new Timer(this.checkIntervalInSeconds * 1000, this);
        this.checkTimer.setInitialDelay(this.checkIntervalInSeconds * 1000);
        this.checkTimer.start();
        this.output.showMessage("starte autosync\n");
    }

    public void StopChecking() {
        if (this.checkTimer != null) {
            this.checkTimer.stop();
            this.output.showMessage("stoppe autosync\n");
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
        if (this.albumProvider != null && !this.albumProvider.isDone()) {
            this.output.showMessage("letzter album check noch nicht fertig, überspringe dieses mal\n");
        } else {
            // ok albumprovider is null then create a new one and check
            this.albumProvider = new BaseAlbumProvider(this.output, this.configurationService);
            this.albumProvider.setExecuteCount(this.execCount++);
            this.albumProvider.addPropertyChangeListener(this);
            this.albumProvider.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        // who is throwing this event? we are only interested in done...
        if (propertyChangeEvent.getNewValue() == SwingWorker.StateValue.DONE) {
            if (propertyChangeEvent.getSource() == this.albumProvider) {
                albumProviderIsReady();
            } else if (propertyChangeEvent.getSource() == this.targetDirectorySearchService) {
                targetDirectorySearchServiceIsReady();
            }
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
        if (this.targetDirectorySearchService != null && this.targetDirectorySearchService.isDone()) {
            try {
                File target = this.targetDirectorySearchService.get();
                Album handleAlbum = this.targetDirectorySearchService.getHandleAlbum();
                if (target != null) {
                    if (!TargetDirectorySearchService.IsAlbumAlreadyInSync(handleAlbum, target)) {
                        this.doSyncing(handleAlbum, target);
                    } else {
                        this.output.showMessage("album: " + handleAlbum.getName() + " schon fertig gesynced\n");
                    }
                } else {
                    this.output.showMessage("kein gültiges target gefunden für album: " + handleAlbum.getName() + "\n");
                }
            } catch (InterruptedException e) {
                this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady:InterruptedException\n");
            } catch (ExecutionException e) {
                this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady:ExecutionException\n");
            }
        } else {
            this.output.showMessage("something went wrong here...targetDirectorySearchServiceIsReady\n");
        }
    }

    private void doSyncing(Album handleAlbum, File targetDir) {
        // start sync
        Vector<String> foddos = null;
        try {
            // this is SYNC, but usually we retrieved the images already
            AlbumImageProvider aip = new AlbumImageProvider(this.output, this.configurationService);
            aip.setAlbum(handleAlbum);
            aip.execute();
            foddos = aip.get(); // this is syncronous
        } catch (Exception e) {
            this.output.showMessage("doSyncing: beim laden der bilder für album: " + handleAlbum.getName() + " ist ein fehler aufgetreten: " + e.getMessage() + "\n");
        }
        FileDBSyncer fdbs = new FileDBSyncer(this.output);
        fdbs.syncItemsWebSearchBased(foddos, targetDir.getAbsolutePath(), this.webSearchServiceURL);
    }


    private void doCheck4NewAlbums() {
        this.checkCounter++;
        Vector<Album> albums = new Vector<Album>();
        try {
            albums = this.albumProvider.get();
            this.output.showMessage(albums.size() + " alben beim " + this.checkCounter + ". check gefunden\n");
        } catch (InterruptedException e) {
            this.output.showMessage("doCheck4NewAlbums: something bad happened... " + e.getMessage() + "\n");
        } catch (ExecutionException e) {
            this.output.showMessage("doCheck4NewAlbums: something bad happened... " + e.getMessage() + "\n");
        }
        Vector<Album> todoList = new Vector<Album>();
        // compare album vectors
        for (Album a : albums) {
            // uuuu this is ugly
            boolean known = false;
            for (Album aa : this.lastSyncAlbums) {
                if (a.getId().equals(aa.getId())) {
                    known = true;
                    break;
                }
            }
            if (!known) {
                // add it to todolist
                todoList.add(a);
                // TODO perhaps we dont know the image count yet
                this.output.showMessage("album " + a.getName() + " mit " + a.getCount() + " bildern zur todoliste hinzugefügt\n");
            }
        }
        this.lastSyncAlbums = albums;
        if (todoList.size() > 0) {
            for (Album a : todoList) {
                // find target (this is async...)
                this.targetDirectorySearchService = new TargetDirectorySearchService(a);
                this.targetDirectorySearchService.addPropertyChangeListener(this);
                // start syncing
                this.targetDirectorySearchService.execute();
            }
        }
    }

    private void doFirstTimeCheck() {
        try {
            this.lastSyncAlbums = this.albumProvider.get();
            this.output.showMessage(this.lastSyncAlbums.size() + " alben beim 1. check gefunden\n");
            this.checkCounter++;
        } catch (InterruptedException e) {
            this.output.showMessage("something bad happened... " + e.getMessage() + "\n");
        } catch (ExecutionException e) {
            this.output.showMessage("something bad happened... " + e.getMessage() + "\n");
        }
    }
}
