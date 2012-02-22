import net.sf.libgrowl.Application;
import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;

public class GrowlNetwork {
    private GrowlConnector growl;
    private Application downloadApp;
    private NotificationType downloadStarted;

    public GrowlNetwork() {
        this.growl = new GrowlConnector("localhost");
        // give your application a name and icon (optionally)<br>
        this.downloadApp = new Application("lgs");
        // create reusable notification types, their names are used in the Growl settings<br>
        this.downloadStarted = new NotificationType("lgs info");
        NotificationType[] notificationTypes = new NotificationType[] { downloadStarted };
        // now register the application in growl<br>
        growl.register(downloadApp, notificationTypes);
        // create a notification with specific title and message<br>
        // finally send the notification<br>
    }

    public void notify(String title, String message) {
        Notification ubuntuDownload = new Notification(downloadApp, downloadStarted, title, message);
        growl.notify(ubuntuDownload);

    }

}
