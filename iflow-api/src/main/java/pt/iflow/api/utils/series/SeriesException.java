/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
