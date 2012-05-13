import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.util.Vector;

public class ConfigurationService {

    Configuration config;

    public ConfigurationService(){
        try {
            config  = new PropertiesConfiguration("lgs.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public String GetAlbumUrl() {
        if(config!=null){
            return config.getString("thalora.albumUrl");
        }
        return "";
    }

    public String GetImageUrl() {
        if(config!=null){
            return config.getString("thalora.imageUrl");
        }
        return "";
    }

    public String GetLssmSearchUrl() {
        if(config!=null){
            return config.getString("thalora.imageUrl");
        }
        return "";
    }

    public String GetOrderTargetPath() {
        if(config!=null){
            return config.getString("orderTargetPath");
        }
        return "";
    }
}
