import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: basti
 * Date: 04.03.12
 * Time: 19:47
 * this retrieves file information from the web file indexer
 */
public class WebSearchService {
    public Vector<FileInformation> Search4Files(String webServiceURL, String fileNamePart) {
        Vector<FileInformation> ret = new Vector<FileInformation>();
        try {
            URL updateURL = new URL(webServiceURL + fileNamePart);
            if (webServiceURL == null || webServiceURL.length() == 0) {
                new URL("http://localhost:82/json/syncreply/FileInformations?SearchPattern=" + fileNamePart);
            }
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new InputStreamReader(updateURL.openStream()));
            reader.beginObject(); // {
            reader.nextName();    // Responsestatus
            reader.beginObject(); // {
            String s = reader.nextName();    // Errors
            reader.skipValue();   // usually [
            reader.endObject();   // {
            s = reader.nextName();    // Files

            reader.beginArray();
            while (reader.hasNext()) {
                FileInformation fi = gson.fromJson(reader, FileInformation.class);
                ret.add(fi);
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }
}
