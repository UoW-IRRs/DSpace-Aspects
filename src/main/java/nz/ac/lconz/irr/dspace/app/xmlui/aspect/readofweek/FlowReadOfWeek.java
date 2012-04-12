package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.dspace.app.xmlui.aspect.administrative.FlowResult;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.apache.cocoon.environment.Request;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: schweer
 * Date: 12/04/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlowReadOfWeek {
	private static final Message T_success = new Message("default", "xmlui.aspect.ReadOfTheWeek.ReadOfWeekForm.success");

	public static FlowResult processPickReadOfTheWeek(Context context, int itemID) throws SQLException, IOException {
		FlowResult result = new FlowResult();
		result.setContinue(false);

		Item item = Item.find(context, itemID);
		ReadOfWeekController.setFeaturedItem(context, item);

		result.setOutcome(true);
		result.setContinue(true);
		result.setMessage(T_success);

		return result;
	}
}
