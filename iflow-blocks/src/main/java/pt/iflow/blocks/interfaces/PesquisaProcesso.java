package pt.iflow.blocks.interfaces;

public interface PesquisaProcesso {

  public static final String PROCESS_VAR = "VarProcesso"; //$NON-NLS-1$
  public static final String STATE_DATE = "DataEstado"; //$NON-NLS-1$
  public static final String STATE_DESC = "DescEstado"; //$NON-NLS-1$
  public static final String CREATION_DATE = "DataCriacao"; //$NON-NLS-1$

  public final static String[] saTYPES = {
    PROCESS_VAR, 
    STATE_DATE, 
    STATE_DESC, 
    CREATION_DATE,
  };
  
  
  public static final String sTYPE  = "type"; //$NON-NLS-1$
  public static final String sVAR   = "var"; //$NON-NLS-1$
  public static final String sDESTVAR = "destvar"; //$NON-NLS-1$
  public static final String sFETCH = "fetch"; //$NON-NLS-1$
  public static final String sMODE  = "mode"; //$NON-NLS-1$
  public static final String sOP    = "op"; //$NON-NLS-1$
  public static final String sVALUE = "value"; //$NON-NLS-1$
  public static final String sCASE  = "casesensitive"; //$NON-NLS-1$

  public static final int nVAR   = 0;
  public static final int nFETCH = 1;
  public static final int nMODE  = 2;
  public static final int nOP    = 3;
  public static final int nVALUE = 4;
  public static final int nCASE  = 5;
  public static final int nTYPE  = 6;
  public static final int nDESTVAR = 7;

  public final static String[] varNames = {
    sTYPE,
    sVAR,
    sDESTVAR,
    sFETCH,
    sMODE,
    sOP,
    sVALUE,
    sCASE,
  };

  public static final String YES = "sim"; //$NON-NLS-1$
  public static final String NO = "não"; //$NON-NLS-1$
  public static final String YES_OPEN = "sim-abertos"; //$NON-NLS-1$
  public static final String YES_CLOSED = "sim-fechados"; //$NON-NLS-1$
  public static final String YES_ALL = "sim-todos"; //$NON-NLS-1$

  public final static String[] saSEARCH_MODES = {
    YES_OPEN,
    YES_CLOSED,
    YES_ALL,
    NO,
  };
  public final static String[] saFETCH_MODES = {
    YES,
    NO,
  };
  public final static String[] saCASE_MODES = {
    NO,
    YES,
  };

}
