import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Vector;

class ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(Lgs.class);
    private Configuration config;

    public ConfigurationService(){
        try {
            logger.debug("read properties");
            this.config = new PropertiesConfiguration("lgs.properties");
            logger.debug("read properties ok");
        } catch (ConfigurationException e) {
            logger.debug("error while reading properties", e);
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
