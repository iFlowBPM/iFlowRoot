package pt.iknow.floweditor;

/**
 * 
 * @author Ana
 * Guarda informacao do Atributos dos blocos.
 */
public class Attribute {

	private String name;/** Nome do atributo**/
	private String description; /** descricao do atributo**/
	private String value; /** valor do atributo**/
	
	public Attribute(String name, String value) {
		this.name=name;
		this.description=name;
		this.value=value;
	}
	
	/**
	 * 
	 * @return nome do atributo
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return descricao do atributo
	 */
	public String getDescrption(){
		return description;
	}
	
	/**
	 * 
	 * @return valor do atributo
	 */
	public String getValue(){
		return value;
	}
		
}
