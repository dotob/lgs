import net.sf.libgrowl.Application;
import net.sf.libgrowl.GrowlConnector;
import net.sf.libgrowl.Notification;
import net.sf.libgrowl.NotificationType;

class GrowlNetwork {
    private final GrowlConnector growl;
    private final Application downloadApp;
    private final NotificationType downloadStarted;

    public GrowlNetwork() {
        this.growl = new GrowlConnector("localhost");
        // give your application a name and icon (optionally)<br>
        this.downloadApp = new Application("lgs");
        // create reusable notification types, their names are used in the Growl settings<br>
        this.downloadStarted = new NotificationType("lgs info");
        NotificationType[] notificationTypes = new NotificationType[] {this.downloadStarted};
        // now register the application in growl<br>
        this.growl.register(this.downloadApp, notificationTypes);
        // create a notification with specific title and message<br>
        // finally send the notification<br>
    }

    public void notify(String title, String message) {
        Notification ubuntuDownload = new Notification(this.downloadApp, this.downloadStarted, title, message);
        this.growl.notify(ubuntuDownload);

    }

}
