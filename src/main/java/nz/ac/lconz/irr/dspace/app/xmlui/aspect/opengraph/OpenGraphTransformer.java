package nz.ac.lconz.irr.dspace.app.xmlui.aspect.opengraph;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.util.HashUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.excalibur.source.SourceValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.DSpaceValidity;
import org.dspace.app.xmlui.utils.HandleUtil;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.*;
import org.dspace.core.ConfigurationManager;
import org.dspace.handle.HandleManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: schweer
 * Date: 30/08/12
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenGraphTransformer extends AbstractDSpaceTransformer implements CacheableProcessingComponent {
	private DSpaceValidity validity = null;

	/**
	 * Generates the unique caching key - this references the item.
	 * @return The unique caching key or 0 in case of errors
	 */
	public Serializable getKey() {
		try {
			DSpaceObject dso = HandleUtil.obtainHandle(objectModel);

			if (dso == null)
			{
				return "0"; // no item, something is wrong.
			}

			return HashUtil.hash(dso.getHandle());
		}
		catch (SQLException sqle)
		{
			// Ignore all errors and just return that the component is not cachable.
			return "0";
		}
	}

	/**
	 * Generates the cache validity object - linked to the item being viewed.
	 * @return The cache validity object or null in case of errors
	 */
	public SourceValidity getValidity() {
		DSpaceObject dso = null;

		if (this.validity == null)
		{
			try {
				dso = HandleUtil.obtainHandle(objectModel);

				DSpaceValidity validity = new DSpaceValidity();
				validity.add(dso);
				this.validity =  validity.complete();
			}
			catch (Exception e)
			{
				// Ignore all errors and just invalidate the cache.
			}

		}
		return this.validity;
	}

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		DSpaceObject dso = HandleUtil.obtainHandle(objectModel);
		if (!(dso instanceof Item))
		{
			return;
		}

		Item item = (Item) dso;

		StringBuilder meta = new StringBuilder();
		// item type - always article
		meta.append(makePropertyStatement("og:type", "article"));

		// link to the item
		String handle = item.getHandle();
		meta.append(makePropertyStatement("og:url", HandleManager.resolveToURL(context, handle)));

		// name of the site
		String siteName = ConfigurationManager.getProperty("dspace.name");
		meta.append(makePropertyStatement("og:site_name", siteName));

		// TODO images

		// title
		String itemName = item.getName();
		meta.append(makePropertyStatement("og:title", itemName));

		// description
		String descriptionText = " ";
		DCValue[] descriptions = item.getMetadata("dc", "description", "abstract", Item.ANY);
		if (descriptions.length > 0) {
			try {
				descriptionText = StringUtils.abbreviate(descriptions[0].value, 500);
			} catch (IllegalArgumentException e) {
				// string is too short - just leave as is
			}
		}
		meta.append(makePropertyStatement("og:description", descriptionText));

		// published time
		DCValue[] availableDates = item.getMetadata("dc", "date", "available", Item.ANY);
		if (availableDates.length > 0 && availableDates[0] != null && availableDates[0].value != null) {
			DCDate available = new DCDate(availableDates[0].value);
			String displayDate = available.toString();
			meta.append(makePropertyStatement("article:published_time", displayDate));
		}

		// modified time
		DCDate lastModified = new DCDate(item.getLastModified());
		meta.append(makePropertyStatement("article:modified_time", lastModified.toString()));

		// section - use name of top-level community in which this item lives
		Community parent = (Community) item.getOwningCollection().getParentObject();
		while (parent.getParentCommunity() != null) {
			parent = parent.getParentCommunity();
		}
		String parentName = parent.getName();
		meta.append(makePropertyStatement("article:section", parentName));

		// tags
		DCValue[] keywords = item.getMetadata("dc", "subject", null, Item.ANY);
		for (DCValue keyword : keywords) {
			meta.append(makePropertyStatement("article:tag", keyword.value));
		}

		pageMeta.addMetadata("xhtml_head_item", "opengraph").addContent(meta.toString());

		// add prefix declarations - the theme should add these to the <head> element like so:
		// <head prefix="og: http://ogp.me/ns# article: http://ogp.me/ns/article#">
		pageMeta.addMetadata("xhtml_head_prefix", "og").addContent("http://ogp.me/ns#");
		pageMeta.addMetadata("xhtml_head_prefix", "article").addContent("http://ogp.me/ns/article#");
	}

	private StringBuilder makePropertyStatement(Object propertyName, String value) {
		StringBuilder result = new StringBuilder();
		if (value != null && !"".equals(value)) {
			result.append("<meta property=\"");
			result.append(propertyName);
			result.append("\" content=\"");
			result.append(value);
			result.append("\">\n");
		}
		return result;
	}
}
