package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.core.ConfigurationManager;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by jjung on 9/02/16.
 */
public class FeaturedContentController {
    private static final Logger log = Logger.getLogger(FeaturedContentController.class);


    static HashMap<String,String> getFeaturedItem(){
        HashMap<String,String> returnMap = null;

        // configuration file location -> ${dspace.dir}/var/featured-content/current.properties
        String featuredItemFile = FeaturedContentController.getFeaturedItemFile();
        if( featuredItemFile == null){
            return null;
        }

        Properties props = new Properties();
        InputStream is = null;

        try {
            // file location ->  ${dspace.src}/dspace/config/modules/uow-aspects.cfg
            File propFile = new File(featuredItemFile);
            is = new FileInputStream(propFile);
            props.load(is);

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
            log.warn("Problem reading featured content information from file: " + e.getMessage(), e);
        } finally{
            try {
               if(is != null){ is.close();}
            } catch (IOException e) {
                log.warn("Problem closing InputStream: " + e.getMessage(), e);
            }
        }
        return returnMap;
    }

    static void setFeaturedItem(String img_location, String link_target, String caption) throws IOException {
        String featuredItemFile = FeaturedContentController.getFeaturedItemFile();
        if( featuredItemFile != null){
            Properties props = new Properties();

            props.setProperty("img_location", img_location);
            props.setProperty("link_target", link_target);
            props.setProperty("caption", caption);

            OutputStream os = null;

            try {
                props.setProperty("img_location", img_location);
                props.setProperty("link_target", link_target);
                props.setProperty("caption", caption);


                File propFile = new File(featuredItemFile);
                File parentDir = new File(propFile.getParent());
                // in case of missing parent directory (do not remove)
                if(!parentDir.exists()){
                    parentDir.mkdirs();
                }

                os = new FileOutputStream(featuredItemFile, false);
                props.store(os, null);
            } catch (IOException e) {
                log.warn("Problem writing featured content information from file: " + e.getMessage(), e);
            } finally{
                try {
                    if(os != null) { os.close();}
                } catch (IOException e) {
                    log.warn("Problem closing OutputStream: " + e.getMessage(), e);
                }
            }

        }   else {
            log.warn("Problem reading featured-content configuration", new Exception("Problem reading featured-content configuration"));
        }
    }

    static String getFeaturedItemFile(){
        String featuredItemFile = StringUtils.trimToNull(ConfigurationManager.getProperty("uow-aspects", "featured-content.datafile"));
        if (featuredItemFile == null) {
            log.warn("Problem reading featured-content configuration.", new Exception("Problem reading featured-content configuration."));
            return null;
        }
        return featuredItemFile;
    }


}
