package pt.iflow.api.utils.series;

public class DuplicateSeriesException extends SeriesException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6952864653616981630L;

	
	protected DuplicateSeriesException(SeriesProcessor series) {
		super(series, "there is already a series with this name");
	}
}
