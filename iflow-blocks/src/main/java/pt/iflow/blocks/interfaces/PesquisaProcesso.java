/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
