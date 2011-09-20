package nz.ac.lconz.irr.dspace.app.xmlui.aspect.ymediaplayer;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.matching.Matcher;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.cocoon.util.HashMap;
import org.dspace.app.xmlui.utils.ContextUtil;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Bitstream;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz
 *
 * Cocoon sitemap matcher that detects DSpace items with at least one public media file.
 */
public class MediaFilesMatcher extends AbstractLogEnabled implements Matcher {
	public Map match(String pattern, Map objectModel, Parameters parameters) throws PatternException {
		try {
			Context context = ContextUtil.obtainContext(objectModel);
			DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

			if (dso.getType() != Constants.ITEM) {
				getLogger().debug("MediaFilesMatcher: Not an item");
				return null;
			}

			List<String> mediaFormats = new ArrayList<String>();
			Collections.addAll(mediaFormats, pattern.split(",\\s*"));

			Item item = (Item) dso;
			Bitstream[] bitstreams = item.getNonInternalBitstreams();
			for (Bitstream bitstream : bitstreams) {
				if (AuthorizeManager.authorizeActionBoolean(context, bitstream, Constants.READ)) {
					String mimeType = bitstream.getFormat().getMIMEType();
					for (String mediaFormat : mediaFormats) {
						if (mimeType.startsWith(mediaFormat)) {
							// we have a media file
							return new HashMap();
						}
					}
				}
			}

			// no media files found
			return null;
		} catch (SQLException e) {
			getLogger().warn("MediaFilesMatcher: Cannot determine whether this is an item with media file", e);
			throw new PatternException("Unable to perform match", e);
		}
	}
}
