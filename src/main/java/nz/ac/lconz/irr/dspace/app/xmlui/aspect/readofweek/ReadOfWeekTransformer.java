package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Body;
import org.dspace.app.xmlui.wing.element.Division;
import org.dspace.app.xmlui.wing.element.ReferenceSet;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: schweer
 * Date: 5/04/12
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOfWeekTransformer extends AbstractDSpaceTransformer {

	private static final Logger log = Logger.getLogger(ReadOfWeekTransformer.class);

	@Override
	public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {

		Item item = null;
		try {
			item = getFeaturedItem();
		} catch (SQLException e) {
			log.error("Cannot find featured item", e);
		} catch (IOException e) {
			log.error("Cannot find featured item", e);
		}

		if (item != null) {
			Division readOfWeekHome = body.addDivision("read-of-week-home", "primary repository");
			Division readOfWeek = readOfWeekHome.addDivision("read-of-week", "secondary read-of-week");
			readOfWeek.setHead("Read of the Week");
			ReferenceSet refset = readOfWeek.addReferenceSet("read-of-week-set", ReferenceSet.TYPE_SUMMARY_LIST, null, "read-of-week");
			refset.addReference(item);
		}
	}

	private Item getFeaturedItem() throws SQLException, IOException {
		String featuredItemFile = ConfigurationManager.getProperty("lconz-aspect", "featured.item.file");
		if (featuredItemFile == null || "".equals(featuredItemFile)) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new FileReader(featuredItemFile));
		String line = reader.readLine();
		if (line == null || "".equals(line)) {
			return null;
		}
		line.trim();
		int itemID = Integer.parseInt(line);
		return Item.find(context, itemID);
	}
}
