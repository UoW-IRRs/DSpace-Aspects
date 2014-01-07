package nz.ac.lconz.irr.dspace.app.xmlui.aspect.general;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.selection.Selector;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz
 *
 * Cocoon sitemap selector that indicates what type of item where looking at
 *
 */
public class HandleTypeSelector extends AbstractLogEnabled implements
		Selector {

	@Override
	public boolean select(String expression, Map objectModel, Parameters parameters) {

		try {
			Context context = ContextUtil.obtainContext(objectModel);
			DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
			if (dso == null) {
				return expression.equals("other");
			}
			int type = dso.getType();
			if (type == Constants.COMMUNITY && expression.equals("community")) {
				return true;
			} else if (type == Constants.COLLECTION && expression.equals("collection")) {
				return true;
			} else if (type == Constants.ITEM && expression.equals("item")) {
				return true;
			}
			return expression.equals("other");
		} catch (SQLException e) {
			getLogger().warn("Cannot obtain context", e);
		}
		return false; // fallback in case of errors
	}
}
