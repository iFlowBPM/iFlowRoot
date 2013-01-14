package pt.iflow.api.utils.series;

public class FirstOverlimitException extends OverlimitException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 818744634854305256L;

	protected FirstOverlimitException(SeriesProcessor series) {
		super(series, "series reached maximum value");
	}
}
