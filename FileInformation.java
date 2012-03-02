/**
 * Created by IntelliJ IDEA.
 * User: basti
 * Date: 02.03.12
 * Time: 19:03
 * this is a simple pojo for json data retrieval
 */
public class FileInformation {
    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }


    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    private String FileName;
    private String Host;
    private String Id;

}
