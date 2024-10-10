package pt.iflow.user_passimage;

import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.Serializable;

import pt.iflow.api.utils.Logger;

public class ImageIconObject implements Serializable, ObjectInputValidation
{

	private Object image;

	public ImageIconObject( Object image )
	{
		this.image = image;
	}
	
	@Override
	public void validateObject() throws InvalidObjectException 
	{
		Logger.trace(this, "ImageIconObject.validateObject", "validating ImageIconObject");	
	}

	public Object getImage() {
		return image;
	}
	
	

}
