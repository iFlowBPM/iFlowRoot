package pt.iflow.api.core;

import java.util.List;
import java.util.Map;

import pt.iflow.api.processtype.ProcessDataType;

public interface ProcessCatalogue {
  public boolean isList(String var);

  public boolean hasVar(String var);

  public ProcessDataType getDataType(String var);

  public List<String> getSimpleVariableNames();

  public List<String> getListVariableNames();

  public List<String> getBindableVariableNames();

  public boolean hasPublicName(String var);

  public String getPublicName(String var);

  public String getDisplayName(String var);

  public String getDefaultValueExpression(String var);

  public boolean isSearchable(String var);

  public Map<String,Integer> getSearchables();

  public String[] getOrderedSearchableNames();
}
