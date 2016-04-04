package pt.iknow.floweditor;

/**
 * 
 * @author Ana
 * Guarda as informacoes de uma associacao BPMN
 */
public class AssociationC {
	
	private String source;/** Id do artefacto**/
	private String target;/** Id do bloco a que o artefacto esta ligado**/

	public AssociationC(String source, String target){
		this.source=source;
		this.target=target;
	}

	/**
	 * 
	 * @return a source da associacao
	 */
	public String getSource(){
		return this.source;
	}

	/**
	 * 
	 * @return a target da associacao
	 */
	public String getTarget(){
		return this.target;
	}
}
