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
package pt.iflow.api.services.types;

import java.util.Hashtable;
import java.util.Set;

import pt.iflow.api.services.types.ForkJoinDepConvert;

public class ForkJoinDepConvert {
	private int FORK;
	private int JOIN;
	private Integer _blockId;
	private int _type;
	private Set<Integer> _hsStates;
	private Hashtable<Integer, ForkJoinDepConvert> _htJFStates;
	
	public int getFORK() {
		return FORK;
	}

	public void setFORK(int fORK) {
		FORK = fORK;
	}

	public int getJOIN() {
		return JOIN;
	}

	public void setJOIN(int jOIN) {
		JOIN = jOIN;
	}
	
	public ForkJoinDepConvert() {
		super();
	}

	public Integer get_blockId() {
		return _blockId;
	}

	public void set_blockId(Integer blockId) {
		_blockId = blockId;
	}

	public int get_type() {
		return _type;
	}

	public void set_type(int type) {
		_type = type;
	}

	public Set<Integer> get_hsStates() {
		return _hsStates;
	}

	public void set_hsStates(Set<Integer> hsStates) {
		_hsStates = hsStates;
	}

	public Hashtable<Integer, ForkJoinDepConvert> get_htJFStates() {
		return _htJFStates;
	}

	public void set_htJFStates(Hashtable<Integer, ForkJoinDepConvert> htJFStates) {
		_htJFStates = htJFStates;
	}
}
