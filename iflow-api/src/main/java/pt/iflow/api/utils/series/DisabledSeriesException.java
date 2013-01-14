package pt.iflow.api.utils.series;

public class DisabledSeriesException extends SeriesException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1595199576838436772L;

	protected DisabledSeriesException(SeriesProcessor series) {
		super(series, "series is disabled");
	}
}
