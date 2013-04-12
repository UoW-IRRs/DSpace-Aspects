package nz.ac.lconz.irr.dspace.app.xmlui.aspect.irrstats;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.*;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.handle.HandleManager;
import org.dspace.statistics.ObjectCount;
import org.dspace.statistics.SolrLogger;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for the LCoNZ Institutional Research Repositories
 */
class IRRStatsController {
	private final static Logger log = Logger.getLogger(IRRStatsController.class);
	protected static final DateFormat QUERY_FORMAT = new SimpleDateFormat(SolrLogger.DATE_FORMAT_8601);
	static {
		QUERY_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private Map<Metric, Long> values = new HashMap<Metric, Long>();

	long getValueFor(Metric metric) throws IllegalStateException {
		return values.containsKey(metric) ? values.get(metric) : 0L;
	}

	void gatherData(Context context, Date startDate, Date endDate) throws StatsDataException {
		try {
			ItemIterator items = Item.findAll(context);
			while (items.hasNext()) {
				Item item = items.next();
				try {
					if (item.isArchived() && !item.isWithdrawn() && isPublic(context, item)) {
						if (addedBefore(endDate, item)) {
							boolean addedInPeriod = addedSince(startDate, item);
							if (hasPublicFulltext(context, item)) {
								incrementValue(Metric.CountPublicFulltext);
								addToValue(Metric.AccessAnyFulltext, countDownloads(context, item, startDate, endDate));
								if (addedInPeriod) {
									incrementValue(Metric.AddedAnyFulltext);
								}
							} else if (hasInternalFulltext(context, item)) {
								incrementValue(Metric.CountInternalFulltext);
								addToValue(Metric.AccessAnyFulltext, countDownloads(context, item, startDate, endDate));
								if (addedInPeriod) {
									incrementValue(Metric.AddedAnyFulltext);
								}
							} else if (isMetadataOnly(context, item)) {
								incrementValue(Metric.CountMetadataOnly);
								addToValue(Metric.AccessMetadataOnly, countPageViews(context, item, startDate, endDate));
								if (addedInPeriod) {
									incrementValue(Metric.AddedMetadataOnly);
								}
							}
						}
					}
				} catch (RuntimeException e) {
					log.error("Problem encountered with item " + item.getID() + ", not counting it");
				} catch (SolrServerException e) {
					log.error("Problem encountered with item " + item.getID() + ", not counting it");
				}
				item.decache();
			}
			sumUpValues(Metric.CountAnyFulltext, Metric.CountPublicFulltext, Metric.CountInternalFulltext);
			sumUpValues(Metric.CountAll, Metric.CountAnyFulltext, Metric.CountMetadataOnly);
			sumUpValues(Metric.AccessAll, Metric.AccessAnyFulltext, Metric.AccessMetadataOnly);
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			throw new StatsDataException(e);
		}
	}

	private void sumUpValues(Metric targetMetric, Metric... constituents) {
		long sum = 0L;
		for (Metric constituent : constituents) {
			if (values.containsKey(constituent)) {
				Long value = values.get(constituent);
				if (value != null) {
					sum += value.longValue();
				}
			}
		}
		values.put(targetMetric, sum);
	}

	private boolean hasPublicFulltext(Context context, Item item) throws SQLException {
		Bitstream[] bitstreams = item.getNonInternalBitstreams();
		for (Bitstream bitstream : bitstreams) {
			if (isPublic(context, bitstream)) {
				return true;
			}
		}
		System.out.println("Counting item as no public fulltext: item_id=" + item.getID() + "; " + HandleManager.resolveToURL(context, item.getHandle()));
		return false;
	}

	private boolean hasInternalFulltext(Context context, Item item) {
		return false;  //To change body of created methods use File | Settings | File Templates.
	}

	private boolean isMetadataOnly(Context context, Item item) throws SQLException {
		Bitstream[] bitstreams = item.getNonInternalBitstreams();
		for (Bitstream bitstream : bitstreams) {
			if (isPublic(context, bitstream)) {
				return false;
			}
		}
		System.out.println("Counting item as md-only: item_id=" + item.getID() + "; " + HandleManager.resolveToURL(context, item.getHandle()));
		return true;
	}

	private long countDownloads(Context context, Item item, Date startDate, Date endDate) throws SolrServerException {
		StringBuilder query = new StringBuilder("type:");
		query.append(Constants.BITSTREAM);
		query.append(" AND owningItem:");
		query.append(item.getID());

		StringBuilder timeQuery = new StringBuilder();
		timeQuery.append("time:[");
		timeQuery.append(QUERY_FORMAT.format(startDate));
		timeQuery.append(" TO ");
		timeQuery.append(QUERY_FORMAT.format(endDate));
		timeQuery.append("]");

		ObjectCount downloads = SolrLogger.queryTotal(query.toString(), timeQuery.toString());
		return downloads.getCount();
	}

	private long countPageViews(Context context, Item item, Date startDate, Date endDate) throws SolrServerException {
		StringBuilder query = new StringBuilder("type:");
		query.append(Constants.ITEM);
		query.append(" AND id:");
		query.append(item.getID());

		StringBuilder timeQuery = new StringBuilder();
		timeQuery.append("time:[");
		timeQuery.append(QUERY_FORMAT.format(startDate));
		timeQuery.append(" TO ");
		timeQuery.append(QUERY_FORMAT.format(endDate));
		timeQuery.append("]");

		ObjectCount pageViews = SolrLogger.queryTotal(query.toString(), timeQuery.toString());
		return pageViews.getCount();
	}


	private void incrementValue(Metric metric) {
		addToValue(metric, 1L);
	}

	private void addToValue(Metric metric, long amount) {
		long existing = 0L;
		if (values.containsKey(metric)) {
			Long value = values.get(metric);
			if (value != null) {
				existing = value.longValue();
			}
		}
		values.put(metric, existing + amount);
	}

	private boolean addedBefore(Date endDate, Item item) {
		DCValue[] accessioned = item.getMetadata("dc", "date", "accessioned", Item.ANY);
		try {
			Date accessionDate = new DCDate(accessioned[0].value).toDate();
			return accessionDate.before(endDate);
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean addedSince(Date startDate, Item item) {
		DCValue[] accessioned = item.getMetadata("dc", "date", "accessioned", Item.ANY);
		try {
			Date accessionDate = new DCDate(accessioned[0].value).toDate();
			return !accessionDate.before(startDate);
		} catch (RuntimeException e) {
			return false;
		}
	}

	private boolean isPublic(Context context, DSpaceObject dso) throws SQLException {
		Group[] readGroups = AuthorizeManager.getAuthorizedGroups(context, dso, Constants.READ);
		for (Group group : readGroups) {
			if (group.getID() == 0) {
				return true;
			}
		}
		return false;
	}

}
