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
package pt.iflow.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

public class PageLocation {
	
	static final int nPARAM_TYPE_TAB = 1;
	static final int nPARAM_TYPE_NAV = 2;
	static final int nPARAM_TYPE_NAV_PARAM = 3;
	static final int nPARAM_TYPE_CONTENT = 4;
	static final int nPARAM_TYPE_CONTENT_PARAM = 5;
	
	static final String sPARAM_PREFIX_TAB = "_lpptab_";
	static final String sPARAM_PREFIX_NAV = "_lppnav_";
	static final String sPARAM_PREFIX_NAV_PARAM = "_lppnavp_";
	static final String sPARAM_PREFIX_CONTENT = "_lppcontent_";
	static final String sPARAM_PREFIX_CONTENT_PARAM = "_lppcontentp_";
	
	String tab = null;
	String nav = null;
	Map<String,String[]> navParams = null;
	String content = null;
	Map<String,String[]> contentParams = null;

	
	protected PageLocation(
			String aTab, 
			String aNav,
			String aNavParams,
			String aContent,
			String aContentParams) {
		
		tab = aTab;
		nav = aNav;
		content = aContent;
		
		StringTokenizer st = null;
		int nrParams = 0;
		
		st = new StringTokenizer(aNavParams, "&");
		nrParams = st.countTokens();
		navParams = new HashMap<String, String[]>();

		for (int i=0; i < nrParams; i++) {
			String par = st.nextToken();
			if (par.indexOf("=") != 0) {
				navParams.put(par, new String[0]);
			}
			else {
				navParams.put(par.substring(0, par.indexOf("=")), new String[]{par.substring(par.indexOf("="))});
			}
		}

		st = new StringTokenizer(aContentParams, "&");
		nrParams = st.countTokens();
		contentParams = new HashMap<String, String[]>();
		for (int i=0; i < nrParams; i++) {			
			String par = st.nextToken();
			if (par.indexOf("=") != 0) {
				contentParams.put(par, new String[0]);
			}
			else {
				contentParams.put(par.substring(0, par.indexOf("=")), new String[]{par.substring(par.indexOf("="))});
			}
		}
	}
	
	public PageLocation(String asURI) {
		
		if (StringUtils.isEmpty(asURI)) return;

		navParams = new HashMap<String, String[]>();
		contentParams = new HashMap<String, String[]>();
		
		Map<String,List<String>> tmpNavPar = new HashMap<String, List<String>>();
		Map<String,List<String>> tmpCntPar = new HashMap<String, List<String>>();
		
		StringTokenizer st = null;
		StringTokenizer nv = null;
		
		st = new StringTokenizer(asURI, "&");
		int nrParams = st.countTokens();
		for (int i=0; i < nrParams; i++) {
			String param = st.nextToken();
			nv = new StringTokenizer(param, "=");
			String pname = "";
			String pval = "";
			if (nv.hasMoreTokens()) pname = nv.nextToken();
			if (nv.hasMoreTokens()) pval = nv.nextToken();
			
			int index = 0;
			if ((index = pname.indexOf(PageLocation.sPARAM_PREFIX_TAB)) == 0) {
				tab = pval;
			}
			else if ((index = pname.indexOf(PageLocation.sPARAM_PREFIX_NAV)) == 0) {
				nav = pval;
			}
			else if ((index = pname.indexOf(PageLocation.sPARAM_PREFIX_CONTENT)) == 0) {
				content = pval;
			}
			else if ((index = pname.indexOf(PageLocation.sPARAM_PREFIX_NAV_PARAM)) == 0) {
				String key = pname.substring(index + PageLocation.sPARAM_PREFIX_NAV_PARAM.length());
				List<String> al = null;
				if (tmpNavPar != null && tmpNavPar.containsKey(key)) {
					al = tmpNavPar.get(key);
				}
				else {
					al = new ArrayList<String>();
				}
				al.add(pval);
				tmpNavPar.put(key, al);
			}
			else if ((index = pname.indexOf(PageLocation.sPARAM_PREFIX_CONTENT_PARAM)) == 0) {
				String key = pname.substring(index + PageLocation.sPARAM_PREFIX_CONTENT_PARAM.length());
				List<String> al = null;
				if (tmpCntPar != null && tmpCntPar.containsKey(key)) {
					al = tmpCntPar.get(key);
				}
				else {
					al = new ArrayList<String>();
				}
				al.add(pval);
				tmpCntPar.put(key, al);
			}
		}
		
		// process parameters
		Iterator<String> iter = null;
		
		iter = tmpNavPar.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			List<String> values = tmpNavPar.get(key);
			
      String[] params = values.toArray(new String[values.size()]);
			navParams.put(key, params);
		}

		iter = tmpCntPar.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			List<String> values = tmpCntPar.get(key);
			
			String[] params = values.toArray(new String[values.size()]);
			contentParams.put(key, params);
		}
	}

	protected boolean isInNavParams (String asKey) {
		if (navParams.containsKey(asKey)) {
			return true;
		}
		return false;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public Map<String,String[]> getNavParams() {
		return navParams;
	}

	public void setNavParams(Map<String,String[]> navParams) {
		this.navParams = navParams;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String,String[]> getContentParams() {
		return contentParams;
	}

	public void setContentParams(Map<String,String[]> contentParams) {
		this.contentParams = contentParams;
	}
	

	
	private static String getPrefix(int anType) {
		
		String prefix = null;
		switch (anType) {
		case PageLocation.nPARAM_TYPE_TAB:
			prefix = PageLocation.sPARAM_PREFIX_TAB;
			break;

		case PageLocation.nPARAM_TYPE_NAV:
			prefix = PageLocation.sPARAM_PREFIX_NAV;
			break;

		case PageLocation.nPARAM_TYPE_CONTENT:
			prefix = PageLocation.sPARAM_PREFIX_CONTENT;
			break;

		case PageLocation.nPARAM_TYPE_NAV_PARAM:
			prefix = PageLocation.sPARAM_PREFIX_NAV_PARAM;
			break;

		case PageLocation.nPARAM_TYPE_CONTENT_PARAM:
			prefix = PageLocation.sPARAM_PREFIX_CONTENT_PARAM;
			break;

		default:
			prefix = "";
			break;
		}
		
		return prefix;
		
	}
	
	private static String formatParamUrl(int anType, String asParam) {
		if (asParam == null) return asParam;
		return (getPrefix(anType)+asParam);
	}
	
	public static String formatParamTab(String asParam) {
		return formatParamUrl(nPARAM_TYPE_TAB, asParam);
	}

	public static String formatParamNav(String asParam) {
		return formatParamUrl(nPARAM_TYPE_NAV, asParam);
	}

	public static String formatParamContent(String asParam) {
		return formatParamUrl(nPARAM_TYPE_CONTENT, asParam);
	}

	public static String formatParamNavParam(String asParam) {
		return formatParamUrl(nPARAM_TYPE_NAV_PARAM, asParam);
	}

	public static String formatParamContentParam(String asParam) {
		return formatParamUrl(nPARAM_TYPE_CONTENT_PARAM, asParam);
	}
	
}
