package nz.ac.lconz.irr.dspace.app.xmlui.aspect.irrstats;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.log4j.Logger;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.utils.UIException;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.*;
import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.core.LogManager;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for the LCoNZ Institutional Research Repositories
 */
public class IRRStatsTransformer extends AbstractDSpaceTransformer {
	private static Logger log = Logger.getLogger(IRRStatsTransformer.class);

	private static final Message T_dspace_home = message("xmlui.general.dspace_home");
	private static final Message T_trail = message("xmlui.aspect.IRRStats.IRRStatsTransformer.trail");
	private static final Message T_title = message("xmlui.aspect.IRRStats.IRRStatsTransformer.title");

	private static final Message T_head = message("xmlui.aspect.IRRStats.IRRStatsTransformer.head");
	private static final Message T_not_authorised = message("xmlui.aspect.IRRStats.IRRStatsTransformer.not_authorised");

	private static final Message T_para_help = message("xmlui.aspect.IRRStats.IRRStatsTransformer.help");
	private static final Message T_error = message("xmlui.aspect.IRRStats.IRRStatsTransformer.error");

	private static final Message T_tablehead_id = message("xmlui.aspect.IRRStats.IRRStatsTransformer.tablehead_id");
	private static final Message T_tablehead_metric = message("xmlui.aspect.IRRStats.IRRStatsTransformer.tablehead_metric");
	private static final Message T_tablehead_value = message("xmlui.aspect.IRRStats.IRRStatsTransformer.tablehead_value");

	private static final NumberFormat VALUE_FORMAT = NumberFormat.getNumberInstance();

	private IRRStatsController dataSource;

	@Override
	public void addPageMeta(PageMeta pageMeta) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException {
		pageMeta.addTrailLink(contextPath + "/", T_dspace_home);
		pageMeta.addTrail().addContent(T_trail);
		pageMeta.addMetadata("title").addContent(T_title);
	}

	@Override
	public void setup(SourceResolver resolver, Map objectModel, String src, Parameters parameters) throws ProcessingException, SAXException, IOException {
		super.setup(resolver, objectModel, src, parameters);
		if (dataSource == null) {
			dataSource = new IRRStatsController();
		}
	}

	@Override
	public void recycle() {
		super.recycle();
		dataSource = null;
	}

	@Override
	public void addBody(Body body) throws SAXException, WingException, UIException, SQLException, IOException, AuthorizeException, ProcessingException {
		if (!AuthorizeManager.isAdmin(context)) {
			Division div = body.addDivision("irr-stats", "irrstats primary repository");
			div.setHead(T_head);
			div.addPara(T_not_authorised);
			return;
		}

		log.info(LogManager.getHeader(context, "view_irrstats", ""));

		Division div = body.addDivision("irr-stats", "irrstats primary repository");
		div.setHead(T_head);
		div.addPara(T_para_help);

		try {
			Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse("2012-01-01");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2012-12-31");
			dataSource.gatherData(context, startDate, endDate);
		} catch (ParseException e) {
			div.addPara(T_error);
			log.error("Problem setting start/end date for IRR Statistics", e);
			return;
		} catch (StatsDataException e) {
			div.addPara(T_error);
			log.error("Problem gathering data for IRR Statistics", e);
			return;
		}

		Table table = div.addTable("irr-stats-data", 11, 3, "ds-table detailtable");
		Row row = table.addRow(Row.ROLE_HEADER);
		row.addCell(Cell.ROLE_HEADER).addContent(T_tablehead_id);
		row.addCell(Cell.ROLE_HEADER).addContent(T_tablehead_metric);
		row.addCell(Cell.ROLE_HEADER).addContent(T_tablehead_value);

		int i = 1;
		for (Metric metric : Metric.values()) {
			row = table.addRow(Row.ROLE_DATA);
			row.addCell(Cell.ROLE_HEADER).addContent(i++);
			row.addCell(Cell.ROLE_DATA).addContent(message("xmlui.aspect.IRRStats.metric." + metric.name()));
			row.addCell(Cell.ROLE_DATA).addContent(VALUE_FORMAT.format(dataSource.getValueFor(metric)));
		}
	}
}

