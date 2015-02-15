package nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.selection.Selector;
import org.apache.cocoon.servlet.RequestUtil;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Andrea Schweer for LCoNZ
 * Determines whether the current user is authorised to deposit a copy of the current item. The current item is identified either by the handle in the URL
 * or by a request parameter called itemID
 */
public class CopyItemSelector implements Selector {

	private static final Logger log = Logger.getLogger(CopyItemSelector.class);

	public boolean select(String expression, Map objectModel, Parameters parameters) {
		try {
			Context context = ContextUtil.obtainContext(objectModel);
			int itemID;

			DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
			if (dso != null && dso.getType() == Constants.ITEM) {
				itemID = dso.getID();
			} else {
				try {
					String itemIDString = ObjectModelHelper.getRequest(objectModel).getParameter("itemID");
					if (itemIDString == null || "".equals(itemIDString)) {
						return false;
					}
					itemID = Integer.valueOf(itemIDString);
				} catch (NumberFormatException e) {
					return false;
				}
			}
			return CopyItemUtils.canDepositCopy(context, itemID) && "can-deposit-copy".equals(expression);
		} catch (SQLException e) {
			log.error("cannot determine whether current user can deposit copy of an item: " + e.getMessage(), e);
			return false;
		}
	}
}
