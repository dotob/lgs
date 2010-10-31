import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: basti
 * Date: 31.10.2010
 * Time: 16:09:16
 * To change this template use File | Settings | File Templates.
 */
public class Album {
    String name;
    int count;
    String id;
    Vector<String> images;
    Boolean initiated;

    public Boolean isInitiated() {
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
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
