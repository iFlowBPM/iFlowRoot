package pt.iflow.servlets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PageMapper {
	
	public final static String DEFAULT_GOTO_VALUE = "main.jsp";
	public final static String PARAM_GOTO = "goto";
	static private HashMap<String, PageLocation> hsLocations = new HashMap<String, PageLocation>();
	static PageLocation plDEFAULT_LOCATION = null;
	
	String sGoto = null;	
	String urlparamstring = "";
		
	static {
		PageLocation plDEFAULT_LOCATION =
			new PageLocation (
					"1",
					"main_content.jsp",
					"data=procs",
					"main_content.jsp",
					"data=tasks"
			);

		PageLocation plCONFIRMAR_AGENDAMENTO = 
			new PageLocation (
					"5",
					"gestao_tarefas_nav.jsp",
					"sel",
					"confirmar_agendamento.jsp",
					"id&owner&dkey"
			);

		PageLocation plTAREFA_PENDENTE = 
			new PageLocation (
					"3",
					"main_content.jsp",
					"data=procs",
					"actividade_user.jsp",
					"flowid&pid&subpid"
			);

		PageLocation plCONTA_PESSOAL = 
			new PageLocation (
					"6",
					"personal_account_nav.jsp",
					"sel=1",
					"personal_account.jsp",
					""
			);

		PageLocation plINICIAR_PROCESSO = 
			new PageLocation (
					"3",
					"main_content.jsp",
					"data=procs",
					"inicio_flow.jsp",
					"flowid"
			);

		PageLocation plORGANIZATION =
			new PageLocation (
					"4",
					"Admin/admin_nav.jsp",
					"sel="+AdminNavConsts.ORGANIZATION_PROPERTIES,
					"Admin/Organization/organization.jsp",
					""
			);

		PageLocation plACTIVIDADE = 
			new PageLocation (
					"2",
					"actividades_filtro.jsp",
					"showflowid",
					"actividades.jsp",
					"showflowid"
			);

		PageLocation plINBOX =
			new PageLocation (
					"6",
					"",
					"",
					"inbox.jsp",
					""
			);

        PageLocation plUSER_PROC =
            new PageLocation (
                    "8",
                    "user_procs_filtro.jsp",
                    "",
                    "user_proc_tasks.jsp",
                    "flowid&pid&subpid"
            );
        
		hsLocations.put(DEFAULT_GOTO_VALUE, plDEFAULT_LOCATION);
		hsLocations.put("confirmar_agendamento.jsp", plCONFIRMAR_AGENDAMENTO);
		hsLocations.put("actividade_user.jsp", plTAREFA_PENDENTE);
		hsLocations.put("personal_account.jsp", plCONTA_PESSOAL);
		hsLocations.put("inicio_flow.jsp", plINICIAR_PROCESSO);
		hsLocations.put("organization.jsp", plORGANIZATION);
		hsLocations.put("inbox.jsp", plINBOX);
		hsLocations.put("actividades.jsp", plACTIVIDADE);
        hsLocations.put("user_proc_tasks.jsp", plUSER_PROC);

	}
	
	private static PageLocation getLocation(String asLocID) {
		PageLocation pl = (PageLocation)hsLocations.get(asLocID);
		if (pl == null) {
			return (PageLocation)hsLocations.get(plDEFAULT_LOCATION);
		}
		return pl;
	}
	
	public static String urlParam (String asParam, String asValue) {
		if (asParam == null) return null;
		return (asParam + "=" + asValue + "&");
	}
	
	protected PageMapper(Map<String, String[]> aParamMap) {
		
		if (aParamMap == null) return;

		String[] gotos = (String[])aParamMap.get(PageMapper.PARAM_GOTO);
		
		if (gotos != null && gotos.length > 0) {
			sGoto = gotos[0];
		}
		else {
			sGoto = DEFAULT_GOTO_VALUE;
		}
		
		PageLocation pl = PageMapper.getLocation(sGoto);
		String sTab = pl.getTab();
		String sNav = pl.getNav();
		String sContent = pl.getContent();
		Map<String, String[]> hmNavParams = pl.getNavParams();
		Map<String, String[]> hmContentParams = pl.getContentParams();
		
		StringBuffer sbParams = new StringBuffer();
		// add tab, nav and content pages
		sbParams.append(urlParam (PageLocation.formatParamTab(""),sTab));
		sbParams.append(urlParam (PageLocation.formatParamNav(""),sNav));
		sbParams.append(urlParam (PageLocation.formatParamContent(""),sContent));
		
		Iterator<String> iter = aParamMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String[] values = aParamMap.get(key);

			for (int i=0; i < values.length; i++) {
				// process all but first goto
				if (!key.equals(PageMapper.PARAM_GOTO) || i != 0) {
					// lpp<type>_<key>=value
					if (hmNavParams.containsKey(key)) {
						sbParams.append(urlParam (PageLocation.formatParamNavParam(key),values[i]));
					}
					if (hmContentParams.containsKey(key)){
						sbParams.append(urlParam (PageLocation.formatParamContentParam(key),values[i]));
					}
				}
			}
		}
		
		urlparamstring = sbParams.toString();
	}

	public String getUrlString() {
		return DEFAULT_GOTO_VALUE + "?" + urlparamstring;
	}

	public static String getDefaultUrlString() {
		return DEFAULT_GOTO_VALUE;
	}
}
