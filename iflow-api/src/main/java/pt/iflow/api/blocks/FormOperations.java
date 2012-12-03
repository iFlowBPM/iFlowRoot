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
package pt.iflow.api.blocks;

import java.io.OutputStream;
import java.util.Map;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.search.PesquisaProcessoSessionData;
import pt.iknow.utils.html.FormData;

public interface FormOperations {
  public final static int OP_REFRESH_CACHE = 1;
  public final static int OP_GENERATE_FORM = 2;
  public final static int OP_PROCESS_FORM = 3;
  public final static int OP_HAS_ERROR = 4;
  public final static int OP_SET_ERROR = 5;
  public final static int OP_GET_ERROR = 6;
  public final static int OP_GET_FORM_NAME = 7;
  public final static int OP_EXPORT_SPREADSHEET = 8;
  public final static int OP_PRINT = 9;
  public final static int OP_GET_PRINT_STYLESHEET = 10;
  public final static int OP_EXPORT_PROCTABLE_STYLESHEET = 11;
  public final static int OP_GET_AUTOPRINT = 12;
  public final static int OP_GET_PROCESS_SEARCH = 13;
  public final static int OP_AUTO_SUBMIT = 14;
  public final static int OP_PROCESS_FILE = 15;
  public final static int OP_ONCHANGE_SUBMIT = 16;

  // methods...
  public void refreshCache(final UserInfoInterface userInfo);

  public String generateForm(final UserInfoInterface userInfo, final ProcessData procData,
      final Map<String, String> ahmHiddenFields, final ServletUtils response);

  public ProcessData processForm(final UserInfoInterface userInfo, final ProcessData pdProcData, final FormData afdFormData, final ServletUtils servletContext, final boolean ignoreValidation);

  public boolean hasError(ProcessData procData);

  public boolean setError(ProcessData procData, String asError);

  public String getError(ProcessData procData);

  public String getFormName();

  public String exportFieldToSpreadSheet(final UserInfoInterface userInfo, final ProcessData procData, final String asField,
      final OutputStream apsOut, final ServletUtils response);

  public String print(UserInfoInterface userInfo, ProcessData procData, String asField, ServletUtils response);

  public String getPrintStylesheet();

  public String exportProcTableToSpreadSheet(UserInfoInterface userInfo, ProcessData procData, PesquisaProcessoSessionData ppsd,
      OutputStream apsOut);

  public String autoPrint();

  public String getPesquisaProcessoFIDs();

  public Boolean isForwardOnSubmit();

}
