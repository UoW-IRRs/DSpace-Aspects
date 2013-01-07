package nz.ac.lconz.irr.dspace.app.xmlui.aspect.irrstats;

import java.sql.SQLException;

/**
 * @author Andrea Schweer schweer@waikato.ac.nz for the LCoNZ Institutional Research Repositories
 */
public class StatsDataException extends Throwable {
	public StatsDataException(SQLException cause) {
		super(cause);
	}
}
