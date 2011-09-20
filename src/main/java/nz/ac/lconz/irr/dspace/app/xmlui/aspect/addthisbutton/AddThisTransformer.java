package nz.ac.lconz.irr.dspace.app.xmlui.aspect.addthisbutton;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Item;
import org.dspace.app.xmlui.wing.element.List;
import org.dspace.app.xmlui.wing.element.Options;
import org.dspace.app.xmlui.wing.element.PageMeta;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.ConfigurationManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz
 *
 * Cocoon sitemap transformer that adds an AddThis button to the page.
 *
 */
public class AddThisTransformer extends AbstractDSpaceTransformer {
	private String addThisPublisher;

	@Override
	public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws ProcessingException, SAXException, IOException {
		super.setup(resolver, objectModel, src, parameters);
		String publisherIdValue = ConfigurationManager.getProperty("lconz-aspect", "addthis.publisher.id");
		if (publisherIdValue != null && !"".equals(publisherIdValue)) {
			addThisPublisher = publisherIdValue;
			getLogger().info("AddThisTransformer: using AddThis publisher id " + addThisPublisher);
		}
	}

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		if (addThisPublisher == null) {
			getLogger().debug("AddThisTransformer: no AddThis publisher id set, not adding javascript link");
			return;
		}
		pageMeta.addMetadata("javascript", "addthis").addContent("http://s7.addthis.com/js/250/addthis_widget.js#pubid=" + addThisPublisher);
	}

	@Override
	public void addOptions(Options options) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		if (addThisPublisher == null) {
			getLogger().debug("AddThisTransformer: no AddThis publisher id set, not adding button");
			return;
		}
		List shareOption = options.addList("share");
		shareOption.setHead("Share");
		Item item = shareOption.addItem();
		item.addFigure("http://s7.addthis.com/static/btn/v2/lg-share-en.gif",
				              "http://www.addthis.com/bookmark.php?v=250&amp;pubid=" + addThisPublisher,
				              "addthis_button").addContent("Bookmark and Share");
	}
}
