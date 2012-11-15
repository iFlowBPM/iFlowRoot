package pt.iflow.api.utils.series;

public class OverlimitException extends SeriesException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 818744634854305256L;

	protected OverlimitException(SeriesProcessor series) {
		this(series, "series has reached maximum value");
	}
	
	protected OverlimitException(SeriesProcessor series, String msg) {
		super(series, msg);	
	}
}
