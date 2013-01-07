package nz.ac.lconz.irr.dspace.app.xmlui.aspect.irrstats;

import org.dspace.core.Context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for the LCoNZ Institutional Research Repositories
 */
public class IRRStatsCreator {
	public static void main(String[] args) throws SQLException, IOException {
		if (args.length < 3) {
			System.out.println("Usage: IRRStatsCreator fromDate toDate filename");
			System.out.println("  Dates given as yyyy-MM-dd; date range is inclusive (eg for all of 2012, specify 2012-01-01 2012-12-31");
			return;
		}

		Date startDate;
		Date endDate;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			startDate = dateFormat.parse(args[0]);
			endDate = dateFormat.parse(args[1]);

			System.out.println("Using start date " + dateFormat.format(startDate));
			System.out.println("Using end date " + dateFormat.format(endDate));
		} catch (ParseException e) {
			e.printStackTrace(System.err);
			return;
		}

		Context context = null;
		try {
			context = new Context();
			context.turnOffAuthorisationSystem();

			IRRStatsController controller = new IRRStatsController();
			controller.gatherData(context, startDate, endDate);
			BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
			for (Metric metric : Metric.values()) {
				writer.write(metric.name());
				writer.write(",");
				writer.write(Long.toString(controller.getValueFor(metric)));
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		} catch (StatsDataException e) {
			e.printStackTrace(System.err);
		} finally {
			if (context != null) {
				context.abort();
			}
		}
	}
}
