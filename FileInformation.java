/**
 * Created by IntelliJ IDEA.
 * User: basti
 * Date: 02.03.12
 * Time: 19:03
 * this is a simple pojo for json data retrieval
 */
class FileInformation {
    public String getFileName() {
        return this.FileName;
    }

    public void setFileName(String fileName) {
        this.FileName = fileName;
    }


    public String getHost() {
        return this.Host;
    }

    public void setHost(String host) {
        this.Host = host;
    }

    public String getId() {
        return this.Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    private String FileName;
    private String Host;
    private String Id;

}
