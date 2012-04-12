package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: schweer
 * Date: 12/04/12
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOfWeekPreviewForm extends AbstractDSpaceTransformer {
	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_readofweek_trail = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.trail");
	private static final Message T_readofweek_title = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.title");
	private static final Message T_head1 = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.head1");
	private static final Message T_no_current = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.no_current");
	private static final Message T_submit_confirm = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekPreviewForm.submit_confirm");
	private static final Message T_submit_cancel = message("xmlui.general.cancel");

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_readofweek_trail);
		pageMeta.addMetadata("title").addContent(T_readofweek_title);
	}

	@Override
	public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {

		// Get our parameters and state
		int itemID = parameters.getParameterAsInteger("itemID",-1);
		Item item = Item.find(context, itemID);

		Division div = body.addInteractiveDivision("admin-readofweek", contextPath + "/admin/read-of-the-week", Division.METHOD_POST, "primary administrative readofweek");
		div.setHead(T_head1);

		if (item != null) {
			ReferenceSet set = div.addReferenceSet("read-of-week-set", ReferenceSet.TYPE_SUMMARY_LIST, null, "read-of-week");
			set.addReference(item);
		} else {
			div.addPara(T_no_current);
		}

		List form = div.addList("readofweek-settings", List.TYPE_FORM);
		org.dspace.app.xmlui.wing.element.Item actions = form.addItem();
		Button confirmButton = actions.addButton("submit_confirm");
		confirmButton.setValue(T_submit_confirm);

		actions.addButton("submit_cancel").setValue(T_submit_cancel);

		div.addHidden("administrative-continue").setValue(knot.getId());
		div.addHidden("itemID").setValue(itemID);
	}
}
