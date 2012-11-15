package pt.iflow.api.processtype;


public interface ProcessDataType extends VariableConverter, VariableFormatter {
	public String toString();
	public Class<?> getSupportingClass();
	public boolean isBindable();
}
