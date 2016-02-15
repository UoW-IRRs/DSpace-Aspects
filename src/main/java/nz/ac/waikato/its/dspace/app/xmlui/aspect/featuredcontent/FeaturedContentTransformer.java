package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by jjung on 9/02/16.
 */
public class FeaturedContentTransformer extends AbstractDSpaceTransformer implements CacheableProcessingComponent {
    //private static final Logger log = Logger.getLogger(FeaturedContentTransformer.class);

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException {

        HashMap<String, String> featuredContentMap = new HashMap<>();
        featuredContentMap = FeaturedContentController.getFeaturedItem();

        if(featuredContentMap != null) {
            String img_location = featuredContentMap.get("img_location");
            String link_target = featuredContentMap.get("link_target");
            String caption = featuredContentMap.get("caption");

            Division featuredContentHome = body.addDivision("featured-content-home", "primary repository");
            Para para = featuredContentHome.addPara();

            Figure figureItem = para.addFigure(img_location, link_target, caption, List.TYPE_SIMPLE);
            figureItem.addContent(caption);

        }
    }

    public Serializable getKey() {
        String featuredItemFile = ConfigurationManager.getProperty("aut-aspect", "featured.content.file");
        if (StringUtils.isEmpty(featuredItemFile)) {
            return "1";
        }
        File file = new File(featuredItemFile);
        if (!file.exists()) {
            return "0";
        }
        return file.lastModified();
    }

    public SourceValidity getValidity() {
        return NOPValidity.SHARED_INSTANCE;
    }
}