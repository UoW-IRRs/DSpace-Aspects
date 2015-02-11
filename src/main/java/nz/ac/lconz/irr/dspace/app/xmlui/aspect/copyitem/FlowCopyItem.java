package nz.ac.lconz.irr.dspace.app.xmlui.aspect.copyitem;

import org.apache.log4j.Logger;
import org.dspace.app.xmlui.aspect.administrative.FlowResult;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.content.Metadatum;
import org.dspace.content.WorkspaceItem;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for LCoNZ
 */
public class FlowCopyItem {

	private static final Message T_success = new Message("default", "xmlui.aspect.CopyItem.success");
	private static final Message T_error = new Message("default", "xmlui.aspect.CopyItem.error");

	private static final Logger log = Logger.getLogger(FlowCopyItem.class);

	public static FlowResult processCopyItem(Context context, int itemID) throws SQLException, IOException {
		FlowResult result = new FlowResult();
		result.setContinue(false);

		Item original = Item.find(context, itemID);
		WorkspaceItem workspaceItem = null;

		try {
			workspaceItem = WorkspaceItem.create(context, original.getOwningCollection(), true);
			Item copy = workspaceItem.getItem();

			for (String field : getFields()) {
				String[] components = field.split("\\.");
				String schema = components[0];
				String element = components[1];
				String qualifier = components.length > 2 ? components[2] : null;

				copy.clearMetadata(schema, element, qualifier, Item.ANY);
				for (Metadatum originalMD : original.getMetadata(schema, element, qualifier, Item.ANY)) {
					copy.addMetadata(schema, element, qualifier, originalMD.language, originalMD.value, originalMD.authority, originalMD.confidence);
				}
			}
			workspaceItem.update();
			workspaceItem.setPageReached(1);
		} catch (AuthorizeException e) {
			log.error("Problem copying item id=" + original.getID(), e);
		}

		if (workspaceItem != null) {
			result.setParameter("workspaceID", workspaceItem.getID());

			result.setOutcome(true);
			result.setContinue(true);
			result.setMessage(T_success);
		} else {
			result.setOutcome(false);
			result.setContinue(false);
			result.setMessage(T_error);
		}

		return result;
	}

	private static List<String> getFields() {
		List<String> fields = new ArrayList<String>();
		String fieldsProperty = ConfigurationManager.getProperty("lconz-aspect", "copyitem.fields");
		if (fieldsProperty != null && !"".equals(fieldsProperty)) {
			fields.addAll(Arrays.asList(fieldsProperty.split(",\\s*")));
		}
		return fields;
	}
}
