package nz.ac.waikato.its.dspace.app.xmlui.aspect.featuredcontent;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by jjung on 9/02/16.
 */
public class Navigation extends AbstractDSpaceTransformer {
    @Override
    public void addOptions(Options options) throws SAXException, WingException, SQLException, IOException, AuthorizeException {
        if (!AuthorizeManager.isAdmin(context)) {
            return;
        }
        options.addList("administrative").addItemXref(contextPath + "/admin/featured-content", "Featured Content");
    }
}
