package pt.iknow.floweditor;

import java.util.ArrayList;

/**
 * @author Ana
 * Guarda informação de um bloco.
 */
public class BlocoXML {
	
	private String id; /** id do bloco**/
	private String name;/** nome do bloco **/
	private String classe;/** tipo de bloco **/
	private String[] portOut; /** blocos a que esta ligado **/
	private int countPort; /** numero de blocos a que esta ligado**/
	private float xCoordinate; /**coordenada x do bloco**/
	private float yCoordinate; /**coordenada y do bloco**/
	private ArrayList<Attribute> eAttributes;  /**atributos do bloco**/
	private int idXmlBloco; /**conversao do id do bloco para inteiro**/
	private String artifactType; /** caso seja artefacto, tipo do mesmo**/
	private String textAnotation; /**caso seja artefacto, textoAnotation**/
	private boolean isError; /**diz se um bloco é do tipo compensacao**/
	private boolean isExclusive; /**diz se a gateway e do tipo exclusiva**/
	
	
	public BlocoXML(String id, String name, int countBlocos) {
		
		this.id=id;
		this.name=name;
		this.idXmlBloco=countBlocos;
		this.countPort=0;
		this.classe="";
		this.xCoordinate=0;
		this.yCoordinate=0;
		this.eAttributes= new ArrayList<Attribute>();
		this.portOut= new String[10];
		this.artifactType="";
		this.textAnotation="";
		this.isError=false;
		this.isExclusive=false;
	}

	/**
	 * 
	 * @return id xml do bloco
	 */
	public int getIdXmlBloco(){
		return idXmlBloco;
	}
	
	/**
	 * 
	 * @return id BPMN do bloco
	 */
	public String getID(){
		return id;
	}
	
	/**
	 * 
	 * @return nome do bloco
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return tipo do bloco
	 */
	public String getClasse(){
		return classe;
	}
	
	/**
	 * 
	 * @param classe tipo do bloco
	 * Altera tipo do bloco
	 */
	public void setClasse(String classe) {
		this.classe=classe;	
	}

	/**
	 * 
	 * @return coordenada x do bloco
	 */
	public float getXCoordinate(){
		return xCoordinate;
	}
	
	/**
	 * Altera coordenada x do bloco
	 * @param xCoordinate - coordenada x do bloco
	 */
	public void setXCoordinate(String xCoordinate) {
		this.xCoordinate=Float.valueOf(xCoordinate);	
	}

	/**
	 * 
	 * @return coordenada y do bloco
	 */
	public float getYCoordinate(){
		return yCoordinate;
	}
	
	/**
	 * Altera coordenada y do bloco
	 * @param yCoordinate - coordenada y do bloco
	 */
	public void setYCoordinate(String yCoordinate) {
		this.yCoordinate=Float.valueOf(yCoordinate);	
	}

	/**
	 * Devolve numero de blocos a que este esta ligado.
	 */
	public int getCounterPort(){
		return countPort;
	}
	
	/**
	 * Adiciona novo bloco ligado
	 * @param i- id do bloco
	 */
	public void setTo(String i) {
		portOut[countPort]=i;
		countPort++;	
	}
	
	/**
	 * Remove ligacao a um bloco
	 * @param i- id do bloco
	 */
	public void removePort(int i) {
		portOut[i]=null;
		countPort--;	
	}

	/**
	 * 
	 * @return todos os bocos ligados
	 */
	public String[] getPortOut(){
		return portOut;
	}
	
	/**
	 * Adiciona novo atributo.
	 * @param atribute - nome do atributo
	 * @param value - valor do atributo
	 */
	public void setExtendedAttributes(String atribute,String value) {
		Attribute a=new Attribute(atribute,value);
		eAttributes.add(a);
	}

	/**
	 * 
	 * @return todos os atributos.
	 */
	public ArrayList<Attribute> getExtendedAttributes(){
		return eAttributes;
	}
	
	/**
	 * 
	 * @return textoAnotation de um artefacto
	 */
	public String getArtifactTextAnotation(){
		return textAnotation;
	}	
	
	/**
	 * Altera o textAnotation de um artefacto.
	 * @param anotation- textAnotation
	 */
	public void setArtifactTextAnotation(String anotation){
		textAnotation=anotation;
	}	
	
	/**
	 * 
	 * @return devolve o tipo de artefacto
	 */
	public String getArtifactType(){
		return artifactType;
	}
	
	/**
	 * Altera o tipo de artefacto
	 * @param type - tipo de artefacto
	 */
	public void setArtifactType(String type){
		artifactType=type;
	}
	
	/**
	 * Se for bloco do tipo compensacao, variavel passa a true.
	 */
	public void setIsError(boolean b) {
		isError=true;	
	}

	/**
	 * 
	 * @return se é do tipo compensacao ou nao
	 */
	public boolean getIsError(){
		return isError;
	}

	/**
	 * 
	 * Se for route do tipo exclusiva devolve true
	 */
	public void setisExclusive(boolean b) {
		isExclusive=b;
	}
	
	/**
	 * 
	 * @return se é do tipo exclusiva ou nao
	 */
	public boolean isExclusive(){
		return isExclusive;
	}	
}
