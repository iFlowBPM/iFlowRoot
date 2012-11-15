package pt.iflow.api.utils.series;

public class ReadOnlyException extends SeriesException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 247111959877622017L;

	protected ReadOnlyException(SeriesProcessor series) {
		super(series, "series is read only");
	}
}
