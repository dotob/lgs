import java.util.Vector;

public class Album {
    private String name;
    private String login;
    private String total;
    private String id;
    private final Vector<String> images = new Vector<String>();
    private boolean initiated = false;

    public Album(){}

    public Album(String name, String total, String id) {
        this.name = name;
        this.total = total;
        this.id = id;
    }

    public boolean isInitiated() {
        return this.initiated;
    }

    public Vector<String> getImages() {
        return this.images;
    }

    public void addImage(String image) {
        this.images.add(image);
        this.initiated = true;
    }

    /*
     what the list will show
     */

    public String toString() {
        if (this.id != null) {
            return "<html><b>" + this.name + "</b> (" + this.id + ") von <b>"+ this.login +"</b> mit <b>"+ this.total +"</b> bildern</html>";
        } else {
            return "";
        }
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getTotal() {
        return this.total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getCount() {
        return this.images.size();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoString() {
        return this.id + ":" + this.name + ", " + this.initiated + ", #=" + this.getCount();
    }
}
