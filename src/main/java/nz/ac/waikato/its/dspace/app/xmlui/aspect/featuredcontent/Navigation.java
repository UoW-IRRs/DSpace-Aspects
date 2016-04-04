package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;

/**
 * Created by jjung on 9/02/16.
 */
public class Navigation extends AbstractDSpaceTransformer implements CacheableProcessingComponent {
    @Override
    public void addOptions(Options options) throws SAXException, WingException, SQLException, IOException, AuthorizeException {
        if (!AuthorizeManager.isAdmin(context)) {
            return;
        }
        options.addList("administrative").addItemXref(contextPath + "/admin/featured-content", "Featured Content");
    }

    @Override
    public Serializable getKey() {
        try {
            if (AuthorizeManager.isAdmin(context)) {
                return "ADMIN";
            }
        } catch (SQLException e) {
            // ignore exception, just fall through to default option
        }
        return "NOT ADMIN";
    }

    @Override
    public SourceValidity getValidity() {
        return NOPValidity.SHARED_INSTANCE;
    }
}
