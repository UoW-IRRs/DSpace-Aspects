package nz.ac.lconz.irr.dspace.app.xmlui.aspect.irrstats;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for the LCoNZ Institutional Research Repositories
 */
public class Navigation extends AbstractDSpaceTransformer {

	private static final Message T_IRR_Stats = message("xmlui.aspect.IRRStats.Navigation.link");

	@Override
	public void addOptions(Options options) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		if (!AuthorizeManager.isAdmin(context)) {
			return;
		}
		options.addList("administrative").addItemXref(contextPath + "/admin/irr-stats", T_IRR_Stats);
	}
}
