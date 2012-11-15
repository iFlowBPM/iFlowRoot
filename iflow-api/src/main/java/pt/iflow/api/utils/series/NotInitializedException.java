package pt.iflow.api.utils.series;

public class NotInitializedException extends SeriesException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4876650562928488434L;

	protected NotInitializedException(SeriesProcessor series) {
		super(series, "series instance has not been initialized yet (hint: call getNext first)");
	}
}
