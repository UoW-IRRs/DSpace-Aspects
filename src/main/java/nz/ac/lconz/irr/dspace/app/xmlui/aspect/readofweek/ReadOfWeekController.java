package nz.ac.lconz.irr.dspace.app.xmlui.aspect.readofweek;

import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import java.io.*;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: schweer
 * Date: 12/04/12
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadOfWeekController {
	static Item getFeaturedItem(Context context) throws SQLException, IOException {
		String featuredItemFile = getFeaturedItemFile();
		if (featuredItemFile == null) {
			return null;
		}

		BufferedReader reader = new BufferedReader(new FileReader(featuredItemFile));
		String line = reader.readLine();
		if (line == null || "".equals(line)) {
			return null;
		}
		line = line.trim();
		int itemID = Integer.parseInt(line);
		return Item.find(context, itemID);
	}

	private static String getFeaturedItemFile() {
		String featuredItemFile = ConfigurationManager.getProperty("lconz-aspect", "featured.item.file");
		if (featuredItemFile == null || "".equals(featuredItemFile)) {
			return null;
		}
		return featuredItemFile;
	}

	static void setFeaturedItem(Context context, Item item) throws IOException {
		String featuredItemFile = getFeaturedItemFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(featuredItemFile));
		try {
			writer.append("" + item.getID());
		} finally {
			writer.close();
		}
	}
}
