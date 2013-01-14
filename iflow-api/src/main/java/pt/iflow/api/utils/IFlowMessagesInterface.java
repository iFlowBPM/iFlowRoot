package pt.iflow.api.utils;

import java.util.Hashtable;
import java.util.List;

public interface IFlowMessagesInterface {

	/**
	 * Replaces {n} values by passed attributes
	 * 
	 * @param asMessage
	 * @param asAttributes
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String... asAttributes);

	/**
	 * Replaces {n} values by passed attributes
	 * 
	 * @param asMessage
	 * @param asAttributes
	 * @return parsed string
	 */
    public abstract String getString(String key, Object... asAtributes);

    /**
     * Same functionality as {@link #getString(String, Object...)}, created for java 1.4 compatibility (due to Velocity tool).
     * 
     * @see #getString(String, Object...)
     */
    public abstract String getString(String key, List<String> asAtributes);

	/**
	 * Convenience method for single attribute
	 * 
	 * @param asMessage
	 * @param asAttribute
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String asAttribute);

	/**
	 * Convenience method for double attribute
	 * 
	 * @param asMessage
	 * @param asAttribute0
	 * @param asAttribute1
	 * @return parsed string
	 */
	public abstract String getString(String asMessage, String asAttribute0,
			String asAttribute1);
	
	public abstract String getString(String key);

	/**
	 * Retrieves all the key/value pair messages from the current resource bundle.
	 * 
	 * @return Hashtable with the retrieved messages
	 */
	public Hashtable<String, String> getMessages();
	
	public boolean hasKey(String key);
}