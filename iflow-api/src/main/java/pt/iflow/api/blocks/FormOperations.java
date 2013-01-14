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
