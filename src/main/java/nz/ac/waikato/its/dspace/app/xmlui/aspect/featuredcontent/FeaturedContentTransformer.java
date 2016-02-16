package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by jjung on 9/02/16.
 *
 */
public class FeaturedContentTransformer extends AbstractDSpaceTransformer implements CacheableProcessingComponent {

    @Override
    public void addBody(Body body) throws SAXException, WingException, SQLException, IOException, AuthorizeException {
        HashMap<String, String> featuredContentMap = FeaturedContentController.getFeaturedItem();

        if(featuredContentMap != null) {
            String img_location = featuredContentMap.get("img_location");
            String link_target = featuredContentMap.get("link_target");
            String caption = featuredContentMap.get("caption");

            // DRI structure for featured-content-view
            //  => Division ("featured-content-home")
            //        - Para
            //              - Figure (target="...", source="...", title="...")
            Division featuredContentHome = body.addDivision("featured-content-home", "primary repository");
            Para para = featuredContentHome.addPara();

            para.addFigure(img_location, link_target, caption, List.TYPE_SIMPLE);
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
