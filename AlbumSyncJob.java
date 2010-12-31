import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class AlbumSyncJob implements ActionListener, PropertyChangeListener {
    Timer checkTimer;
    int checkIntervalInSeconds = 60;
    private IMessageDisplay output;
    private BaseAlbumProvider albumProvider;
    private Vector<Album> lastSyncAlbums;

    public AlbumSyncJob(IMessageDisplay output) {
        this.output = output;
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
        // this is for the albumprovider
        if (this.albumProvider != null && this.albumProvider.isDone()) {
            // check if this is first time
            if (this.lastSyncAlbums == null) {
                doFirstTimeCheck();
            } else {
                doCheck4NewAlbums();
            }
        }
    }

    private void doCheck4NewAlbums() {
        Vector<Album> albums = new Vector<Album>();
        try {
            albums = this.albumProvider.get();
        } catch (InterruptedException e) {
            this.output.showMessage("something bad happened... " + e.getMessage());
        } catch (ExecutionException e) {
            this.output.showMessage("something bad happened... " + e.getMessage());
        }
        Vector<Album> todoList = new Vector<Album>();
        // compare album vectors
        for (Album a : albums) {
            if (!this.lastSyncAlbums.contains(a)) {
                // add it to todolist
                todoList.add(a);
                this.output.showMessage("album " + a.getName() + " mit " + a.getCount() + " bildern zur todoliste gefügt");
            }
        }
        if (todoList.size() > 0) {
            //TODO:  do the action
            
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
