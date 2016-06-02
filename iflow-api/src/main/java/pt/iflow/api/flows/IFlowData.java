package pt.iflow.api.flows;

import java.util.Map;
import java.util.Vector;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.core.ProcessCatalogue;
import pt.iflow.api.utils.UserInfoInterface;

public interface IFlowData {

  // TODO move attr constants to pt.iflow.api.blocks.BlockAttributes 
  // (here and in /iflow-editor/src/main/java/pt/iknow/floweditor/blocks/AlteraAtributosStart.java)
  
  // check if match vars in Editor's
  // pt.iknow.uniflow.editor.blocks.AlteraAtributos
  // public final static String sSETTING_PREFIX = "##_"; // not supported yet
  public static final String sSETTING = "setting_"; //$NON-NLS-1$
  public static final String sSETTING_DESC = "settingdesc_"; //$NON-NLS-1$
  public static final String sDETAIL = "detail_"; //$NON-NLS-1$
  public static final String sDETAIL_CLASS = "detailClass"; //$NON-NLS-1$
  public static final String sDETAIL_FORM = "detailForm"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_NODETAIL = "no"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_DEFAULT = "default"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_BLOCKFORM = "form"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_EXISTINGBLOCKFORM = "existingform"; //$NON-NLS-1$
  public static final String sDETAIL_FORM_BID = "detailFormBID"; //$NON-NLS-1$
  public static final int NO_SERIES = 0;

  public static final String sMAIL_START_INFO_PREFIX = "msinfo_"; //$NON-NLS-1$
  public static final String sMAIL_START_VARS_PREFIX = "msvars_"; //$NON-NLS-1$
  public static String MAILSTART_FROM_EMAIL_PROP = "MS_FROMEMAIL"; //$NON-NLS-1$
  public static String MAILSTART_FROM_NAME_PROP = "MS_FROMNAME"; //$NON-NLS-1$
  public static String MAILSTART_SUBJECT_PROP = "MS_SUBJ"; //$NON-NLS-1$
  public static String MAILSTART_SENT_DATE_PROP = "MS_SD"; //$NON-NLS-1$
  public static String MAILSTART_DOCS_PROP = "MS_DOCS"; //$NON-NLS-1$
  public static String MAILSTART_TEXT_PROP = "MS_TEXT";
  public static final String MAILSTART_MAIL_PROP = "MS_MP"; //$NON-NLS-1$
  public static final String MAILSTART_FLOW_VAR = "MS_VAR"; //$NON-NLS-1$

  
  public abstract String getApplicationId();

  public abstract void setApplicationId(String applicationId);

  public abstract String getApplicationName();

  public abstract void setApplicationName(String applicationName);

  public abstract int getId();

  public abstract String getName();

  public abstract String getFileName();

  public abstract boolean isOnline();

  public abstract boolean isDeployed();

  public abstract boolean hasSubFlow(String subflow);

  public abstract boolean hasError();

  public abstract String getError();

  public abstract Vector<Block> getFlow();

  public abstract Map<String, Integer> getIndexVars();

  public abstract String[] getIndexVarStrings();

  public abstract String getOrganizationId();

  public abstract boolean runMaximized();

  public abstract long getLastModified();

  public abstract long getCreated();

  public abstract boolean hasDetail();

  public abstract Block getDetailForm();

  public abstract int getSeriesId();

  public abstract void setSeriesId(int seriesId);

  public abstract ProcessCatalogue getCatalogue();

  public abstract boolean isGuestCompatible(UserInfoInterface userInfo);

  public abstract Form getFormTemplate(String name);
  
  public abstract MailStartSettings getMailSettings();
  
  public abstract FlowType getFlowType();

  public abstract boolean isVisibleInMenu();

  public abstract boolean hasSchedules();

  public abstract void setHasSchedules(boolean hasSchedules);

  public abstract int getMaxBlockId();
}