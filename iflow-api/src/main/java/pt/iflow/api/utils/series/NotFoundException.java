package pt.iflow.api.utils.series;

public class NotFoundException extends SeriesException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -373933490271666640L;

	private int _id = -1;
	private String _name = null;
	
	protected NotFoundException(int id) {
		_id = id;
	}
	
	protected NotFoundException(String name) {
		_name = name;
	}

	@Override
	public String getMessage() {
		if (_id > 0)
			return getIdMessage();
		
		return getNameMessage();
	}
	
	private String getIdMessage() {
		return "Series with ID " + _id + " not found";
	}
	
	private String getNameMessage() {
		return "Series named " + _name + " not found";		
	}
}
