import java.util.Vector;

public class Album {
    String name;
    String id;
    Vector<String> images = new Vector<String>();
    boolean initiated = false;

    public boolean isInitiated() {
        return initiated;
    }

    public Vector<String> getImages() {
        return images;
    }

    public void addImage(String image) {
        this.images.add(image);
        this.initiated = true;
    }

    /*
     what the list will show
     */

    public String toString() {
        if (id != null) {
            return "<html><b>" + name + "</b> (" + id + ")</html>";
        } else {
            return "";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return this.images.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfoString() {
        return id +":"+name+", "+initiated+", #="+this.getCount();
    }
}
