package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.content.Item;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: schweer
 * Date: 12/04/12
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOfWeekForm extends AbstractDSpaceTransformer {
	/** Language strings */
	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_readofweek_trail = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.trail");
	private static final Message T_readofweek_title = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.title");
	private static final Message T_head1 = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.head1");
	private static final Message T_head_current = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.head_current");
	private static final Message T_head_find = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.head_find");
	private static final Message T_no_current = message("xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.no_current");
	// TODO
	private static final Message T_identifier_label = message("xmlui.administrative.item.FindItemForm.identifier_label");
	private static final Message T_identifier_error = message("xmlui.administrative.item.FindItemForm.identifier_error");
	private static final Message T_find = message("xmlui.administrative.item.FindItemForm.find");

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_readofweek_trail);
		pageMeta.addMetadata("title").addContent(T_readofweek_title);
	}

	@Override
	public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {

		// Get our parameters and state;
		String identifier = parameters.getParameter("identifier",null);

		String errorString = parameters.getParameter("errors",null);
		ArrayList<String> errors = new ArrayList<String>();
		if (errorString != null)
		{
			for (String error : errorString.split(","))
			{
				errors.add(error);
			}
		}

		Division div = body.addDivision("admin-readofweek", "primary administrative readofweek");
		div.setHead(T_head1);

		// DIVISION: current
		Division current = div.addDivision("current-read", "secondary");
		current.setHead(T_head_current);

		Item currentRead = ReadOfWeekController.getFeaturedItem(context);
		if (currentRead != null) {
			ReferenceSet set = current.addReferenceSet("read-of-week-set", ReferenceSet.TYPE_SUMMARY_LIST, null, "read-of-week");
			set.addReference(currentRead);
		} else {
			current.addPara(T_no_current);
		}

		// DIVISION: find-item
		Division findItem = div.addInteractiveDivision("find-item",contextPath + "/admin/read-of-the-week", Division.METHOD_GET, "secondary");
		findItem.setHead(T_head_find);

		List form = findItem.addList("find-item-form", List.TYPE_FORM);

		Text id = form.addItem().addText("identifier");
		id.setLabel(T_identifier_label);
		if (identifier != null)
		{
			id.setValue(identifier);
		}
		if (errors.contains("identifier"))
		{
			id.addError(T_identifier_error);
		}

		form.addItem().addButton("submit_find").setValue(T_find);

		findItem.addHidden("administrative-continue").setValue(knot.getId());
	}
}
