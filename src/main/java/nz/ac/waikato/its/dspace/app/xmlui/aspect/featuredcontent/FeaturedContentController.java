package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.commons.lang3.StringUtils;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jjung on 9/02/16.
 */
public class FeaturedContentController {

    static HashMap<String,String> getFeaturedItem(){
        HashMap<String,String> returnMap = null;

        // configuration file location -> ${DSPACE_HOME}/config/modules/lconz-aspect.cfg
        String featuredItemFile = ConfigurationManager.getProperty("lconz-aspect", "featured.content.file");
        if (StringUtils.isEmpty(featuredItemFile)) {
            return null;
        }

        Properties props = new Properties();
        InputStream is = null;

        try {
            // file location -> ${DSPACE_HOME}/config/modules/featured-content
            File propFile = new File(featuredItemFile);
            if (!propFile.exists()) {
                propFile.createNewFile();
            }
            props.load(new FileInputStream(propFile));

            String img_location = StringUtils.trimToNull(props.getProperty("img_location"));
            String link_target = StringUtils.trimToNull(props.getProperty("link_target"));
            String caption = StringUtils.trimToNull(props.getProperty("caption"));

            if((img_location != null) && (link_target != null) && (caption != null)){
                returnMap = new HashMap<>();

                returnMap.put("img_location", img_location);
                returnMap.put("link_target", link_target);
                returnMap.put("caption", caption);
            }

        } catch (IOException e) {
        } finally{
            try {
               if(is != null){ is.close();}
            } catch (IOException e) {
            }
        }
        return returnMap;
    }

    static void setFeaturedItem(String img_location, String link_target, String caption) throws IOException {
        String featuredItemFile = ConfigurationManager.getProperty("lconz-aspect", "featured.content.file");
        if (!StringUtils.isEmpty(featuredItemFile)) {
            Properties props = new Properties();

            props.setProperty("img_location", img_location);
            props.setProperty("link_target", link_target);
            props.setProperty("caption", caption);

            OutputStream os = null;

            try {
                props.setProperty("img_location", img_location);
                props.setProperty("link_target", link_target);
                props.setProperty("caption", caption);

                props.store(new FileOutputStream(featuredItemFile, false), null);
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                try {
                   if(os != null) { os.close();}
                } catch (IOException e) {
                }
            }
        }
    }
}
