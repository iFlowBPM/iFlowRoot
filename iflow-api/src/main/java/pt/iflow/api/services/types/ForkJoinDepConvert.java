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
