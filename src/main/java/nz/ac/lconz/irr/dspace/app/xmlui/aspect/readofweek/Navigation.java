package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: schweer
 * Date: 5/04/12
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Navigation extends AbstractDSpaceTransformer {
	@Override
	public void addOptions(Options options) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		if (!AuthorizeManager.isAdmin(context)) {
			return;
		}
		options.addList("administrative").addItemXref(contextPath + "/admin/read-of-the-week", "Read of the Week");
	}
}
