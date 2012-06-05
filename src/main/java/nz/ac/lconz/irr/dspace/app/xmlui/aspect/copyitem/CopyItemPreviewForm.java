package nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for LCoNZ
 */
public class CopyItemPreviewForm extends AbstractDSpaceTransformer {

	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_copyitem_trail = message("xmlui.aspect.CopyItem.CopyItemForm.trail");
	private static final Message T_copyitem_title = message("xmlui.aspect.CopyItem.CopyItemForm.title");
	private static final Message T_copyitem_help = message("xmlui.aspect.CopyItem.CopyItemForm.help");
	private static final Message T_head1 = message("xmlui.aspect.CopyItem.CopyItemForm.head1");
	private static final Message T_submit_confirm = message("xmlui.aspect.CopyItem.CopyItemForm.submit_confirm");
	private static final Message T_submit_cancel = message("xmlui.general.cancel");

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
		if (!(dso instanceof Item))
		{
			return;
		}

		Item item = (Item) dso;

		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		HandleUtil.buildHandleTrail(item, pageMeta, contextPath);
		pageMeta.addTrail().addContent(T_copyitem_trail);

		pageMeta.addMetadata("title").addContent(T_copyitem_title);
	}

	@Override
	public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		// Get our parameters and state
		int itemID = parameters.getParameterAsInteger("itemID",-1);
		Item item = Item.find(context, itemID);

		Division div = body.addInteractiveDivision("admin-copy-item", contextPath + "/admin/copy-item", Division.METHOD_POST, "primary administrative copyitem");
		div.setHead(T_head1);

		div.addPara(T_copyitem_help);

		if (item != null) {
			ReferenceSet set = div.addReferenceSet("copy-item-set", ReferenceSet.TYPE_SUMMARY_LIST, null, "copy-item");
			set.addReference(item);
		}

		List form = div.addList("copy-item-settings", List.TYPE_FORM);
		org.dspace.app.xmlui.wing.element.Item actions = form.addItem();
		Button confirmButton = actions.addButton("submit_confirm");
		confirmButton.setValue(T_submit_confirm);

		actions.addButton("submit_cancel").setValue(T_submit_cancel);

		div.addHidden("administrative-continue").setValue(knot.getId());
		div.addHidden("itemID").setValue(itemID);
	}
}
