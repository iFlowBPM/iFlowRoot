package pt.iknow.floweditor;

/**
 * @author Ana
 * Guarda informacao sobre eventos do tipo compensacao 
 */
public class ErrorEvent {

	private String idError; /** id do evento de compensacao**/
	private String idFrom; /** id do bloco anterior*/
	private String idTo; /** id do bloco seguinte*/

	public ErrorEvent(){
		this.idError="";
		this.idFrom="";
		this.idTo="";
	}
	
	/**
	 * altera id do bloco de compensacao
	 * @param id- id do evento de compensacao 
	 */
	public void setBlockError(String id){
		this.idError=id;
	}

	/**
	 * altera id do bloco anterior
	 * @param id - id do bloco anterior
	 */
	public void setBlockFrom(String id){
		this.idFrom=id;
	}
	
	/**
	 *  altera id do bloco seguinte
	 * @param id- id do bloco seguinte
	 */
	public void setBlockTO(String id){
		this.idTo=id;
	}

	/**
	 * 
	 * @return id do evento de compensacao
	 */
	public String getBlockError(){
		return this.idError;
	}

	/**
	 * 
	 * @return id do bloco anterior
	 */
	public String getBlockFrom(){
		return this.idFrom;
	} 

	/**
	 * 
	 * @return id do bloco seguinte
	 */
	public String getBlockTo(){
		return this.idTo;
	}
}
