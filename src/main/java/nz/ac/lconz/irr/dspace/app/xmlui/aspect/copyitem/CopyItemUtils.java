package nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem;

import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;

import java.sql.SQLException;

/**
 * @author Andrea Schweer for LCoNZ
 * Utility methods for
 */
public class CopyItemUtils {
	private static final Logger log = Logger.getLogger(CopyItemUtils.class);

	// default group is Administrators
	public static final int DEFAULT_GROUP_ID = 1;

	/** Determines whether the current user is authorised to deposit a copy of a given item. A user is authorised when they are part of a defined group
	 * (config module lconz-aspect, config key copyitem.authorised.groupid, default if not set: administrators) and when they are authorised to submit
	 * to the owning collection of the item. When no user is logged in or the specified item cannot be found, this method will return false.
	 *
	 * @param context The current context.
	 * @param itemID The ID of the item to copy.
	 * @return Whether the current user is authorised to deposit a copy of the item identified by itemID.
	 * @throws SQLException
	 */
	public static boolean canDepositCopy(Context context, int itemID) throws SQLException {
		EPerson currentUser = context.getCurrentUser();
		if (currentUser == null) {
			return false; // no user is logged in
		}

		int groupid = ConfigurationManager.getIntProperty("lconz-aspect", "copyitem.authorised.groupid", DEFAULT_GROUP_ID);
		Group authorisedGroup = Group.find(context, groupid);
		if (authorisedGroup == null) {
			log.warn("No group set up as being authorised to deposit");
			return false;
		} else {
			if (!authorisedGroup.isMember(currentUser)) {
				log.warn("user is not member of group authorised to deposit");
				return false;
			}
		}

		Item original = Item.find(context, itemID);
		if (original == null) {
			log.warn("Cannot find original item for itemId " + itemID);
			return false;
		}

		Collection owningCollection = original.getOwningCollection();
		return AuthorizeManager.authorizeActionBoolean(context, owningCollection, Constants.ADD, true);
	}
}
