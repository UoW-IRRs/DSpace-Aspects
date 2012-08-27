package nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem;

import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.DSpaceObject;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * add link to context menu of item page (for admins) to trigger copy-of-item deposit
 */
public class Navigation extends AbstractDSpaceTransformer {

	private static final Message T_copyitem_navlink = message("xmlui.aspect.CopyItem.Navigation.link");
	private static final Message T_contextmenu_head = message("xmlui.administrative.Navigation.context_head");
	private static final Logger log = Logger.getLogger(Navigation.class);

	@Override
	public void addOptions(Options options) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		Context context = ContextUtil.obtainContext(objectModel);
		DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

		if (dso == null || dso.getType() != Constants.ITEM) {
			return; // nothing to do
		}

		int itemID = dso.getID();

		if (!CopyItemUtils.canDepositCopy(context, itemID)) {
			return; // nothing to do
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("itemID", String.valueOf(itemID));

		String url = generateURL(contextPath + "/admin/copy-item", params);

		List contextList = options.addList("context");
		contextList.setHead(T_contextmenu_head);
		contextList.addItemXref(url, T_copyitem_navlink);
		log.debug("Added link " + url + " to context list in options");
	}
}
