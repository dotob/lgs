import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.util.Vector;

class ConfigurationService {

    private Configuration config;

    public ConfigurationService(){
        try {
            this.config = new PropertiesConfiguration("lgs.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public String GetAlbumUrl() {
        if(this.config !=null){
            return this.config.getString("thalora.albumUrl");
        }
        return "";
    }

    public String GetImageUrl() {
        if(this.config !=null){
            return this.config.getString("thalora.imageUrl");
        }
        return "";
    }

    public String GetLssmSearchUrl() {
        if(this.config !=null){
            return this.config.getString("lssm.searchUrl");
        }
        return "";
    }

    public String GetOrderTargetPath() {
        if(this.config !=null){
            return this.config.getString("orderTargetPath");
        }
        return "";
    }
}
