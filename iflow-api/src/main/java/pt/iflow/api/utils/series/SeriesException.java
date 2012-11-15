package pt.iflow.api.utils.series;

import org.apache.commons.lang.StringUtils;

public class SeriesException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5333824099476433156L;
	
	
	protected SeriesProcessor _series;
	protected String _reason;
	protected Exception _genericException;
	
	protected SeriesException() {		
	}
	
	protected SeriesException(SeriesProcessor series, String reason) {
		_series = series;
		_reason = reason;
	}

	protected SeriesException(Exception e) {
		_genericException = e;
	}

	@Override
	public String getMessage() {
		if (_series != null)
			return getSeriesMessage();
		else if (_genericException != null)
			return getGenericExceptionMessage();
		
		return getGenericMessage();
	}
	
	private String getSeriesMessage() {
		String myReason = StringUtils.isNotEmpty(_reason) ? ": " + _reason : "";
		return "Exception caught for series named \"" + _series.getName()  + "\"" + myReason;		
	}

	private String getGenericExceptionMessage() {
		return "Series generic exception caught: " + _genericException.getMessage();
	}
	
	private String getGenericMessage() {
		return "Series generic exception caught";
	}
}
