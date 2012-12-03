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
package pt.iflow.ldap;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.lang.StringUtils;

public abstract class LDAPMapping implements Serializable {
  private static final long serialVersionUID = 775724601195639706L;

  protected String key;
  
  protected String dnAttr;
  protected String obj1;
  protected String pos1;
  protected String obj2;
  protected String pos2;
  protected String defValue;

  // DN manip
  private static Pattern p1 = Pattern.compile("\"(.*)\"");
  private static Pattern p2 = Pattern.compile("(\\w+)\\s*\\[\\s*(\\.?[a-zA-Z]\\w*)\\s*(:\\s*(\\d+))?(\\s*,\\s*(\\.?[a-zA-Z]\\w*)?(\\s*:?\\s*(\\d+))?)?(\\s*;\\s*(\\w+|(\"[^\"]+\")))?\\s*\\]");

  // Remove Base DN
  private static Pattern r1 = Pattern.compile("removeBase\\s*\\(\\s*(.+)\\s*\\)");

  // queries
  private static Pattern q1 = Pattern.compile("findFDN\\s*\\(\\s*(.+)\\s*;\\s*(.+)\\s*\\)");
  private static Pattern q2 = Pattern.compile("findDN\\s*\\(\\s*(.+)\\s*;\\s*(.+)\\s*\\)");
  private static Pattern q3 = Pattern.compile("findQuery\\s*\\(\\s*(.+)\\s*;\\s*(.+)\\s*;\\s*(.+)\\s*\\)");
  
  public abstract void setupMapping(Properties parameters, Map<String,String> map);
  
  public abstract void updateAttributes(Map<String,String> map);
  
  public static LDAPMapping getMapping(String key, String val) {
    LDAPMapping mapping = null;
    // pre-process map
    Matcher m;
    if((m = p1.matcher(val)).matches()) {
      mapping = new DefaultValueLDAPMapping();
      mapping.defValue = m.group(1);
    }
    else if((m = p2.matcher(val)).matches()) {
      // dn manipulation value
      mapping = new DNLDAPMapping();
      mapping.dnAttr = m.group(1);
      mapping.obj1 = m.group(2);
      mapping.pos1 = m.group(4);
      mapping.obj2 = m.group(6);
      mapping.pos2 = m.group(8);
      mapping.defValue = m.group(10);
    } else if((m = q1.matcher(val)).matches()) {
      mapping = new FindDNMapping(m.group(1),m.group(2), true);
    } else if((m = q2.matcher(val)).matches()) {
      mapping = new FindDNMapping(m.group(1),m.group(2), false);
    } else if((m = q3.matcher(val)).matches()) {
      mapping = new FindQueryMapping(m.group(1),m.group(2),m.group(3));
    } else if((m = r1.matcher(val)).matches()) {
      mapping = new RemoveBaseMapping(m.group(1));
    } else { // default one...
      mapping = new PlainLDAPMapping();
      mapping.defValue = val;
    }
    mapping.key = key;

    return mapping;
  }

  
  private static class PlainLDAPMapping extends LDAPMapping {
    private static final long serialVersionUID = 7610150712605171844L;
    
    public void setupMapping(Properties parameters, Map<String,String> map) {
      map.put(key,defValue);
    }
    
    public void updateAttributes(Map<String,String> map) {
      
    }
  }
  
  private static class DefaultValueLDAPMapping extends LDAPMapping {
    private static final long serialVersionUID = 4820760698589715663L;

    public void setupMapping(Properties parameters, Map<String,String> map) {
      map.put(key,key); // just in case
    }

    public void updateAttributes(Map<String,String> map) {
      map.put(key,defValue);
    }
  }
  
  private static class DNLDAPMapping extends LDAPMapping {
    private static final long serialVersionUID = 1068262313068982385L;
    
    public void setupMapping(Properties parameters, Map<String,String> map) {
      map.put(key,key); // just in case
    }
    
    public void updateAttributes(Map<String,String> map) {
      String dn = map.get(dnAttr);
      String baseDN = LDAPInterface.getBaseDN();
      int basePos = dn.lastIndexOf(baseDN);
      if(basePos != -1) dn = dn.substring(0, basePos);
      else dn = dn+",";
      
      // Get default value
      String value = defValue;
      if(null != defValue) {
        Matcher m1 = p1.matcher(defValue);
        if(m1.matches()) {
          value = m1.group(1);
        } else {
          value = map.get(defValue);
        }
      }
      
      List<Rdn> rdns = null;
      try {
        rdns = new LdapName(dn).getRdns();
      } catch (InvalidNameException e) {
      }
      
      List<Rdn> resultList = new ArrayList<Rdn>();
      if(null != rdns) {
        int n = rdns.size();
        int from = 1;
        int to = n;
        if(null == obj2) obj2 = obj1;
        try {
          from = Integer.parseInt(pos1);
        } catch (Throwable t) {
        }
        try {
          to = Integer.parseInt(pos2);
        } catch (Throwable t) {
        }
        
        Map<String,Integer> counts = new Hashtable<String, Integer>();
        for(int i = 1; i < n; i++) { // i < n porque n eh o base DN
          Rdn rdn = rdns.get(n-i);
          String type = rdn.getType();
          if(i > from && i < to 
              && StringUtils.equalsIgnoreCase(obj1, ".parent") 
              && StringUtils.equalsIgnoreCase(obj2, ".parent")) {
            resultList.add(rdn);
            continue;
          }
          
          // contar os objectos
          int cur = 0;
          if(counts.containsKey(type.toLowerCase()))
            cur = counts.get(type.toLowerCase());
          cur++;
          counts.put(type.toLowerCase(), cur);
          
          int cur1 = 0;
          int cur2 = 0;
          if(counts.containsKey(obj1.toLowerCase()))
            cur1 = counts.get(obj1.toLowerCase());
          if(counts.containsKey(obj2.toLowerCase()))
            cur2 = counts.get(obj2.toLowerCase());
          
          if(from <= cur1 && to > cur2) {
            resultList.add(rdn);
          }
        }
        
        if(!resultList.isEmpty()) {
          Collections.reverse(resultList);
          value = new LdapName(resultList).toString();
        }
        
      }
      try {
      map.put(key, value); // Try to insert a possible null. Catch any exception thrown
      } catch (Throwable t) {}
    }
  }
  
  private static class RemoveBaseMapping extends LDAPMapping {
    private static final long serialVersionUID = 4820765463466715663L;
    private String attr;
    
    RemoveBaseMapping(String attr) {
      this.attr = attr;
    }

    public void setupMapping(Properties parameters, Map<String,String> map) {
      map.put(key,key); // just in case
    }

    public void updateAttributes(Map<String,String> map) {
      String dn = map.get(attr);
      if(StringUtils.isNotEmpty(dn))
        dn = removeBaseDN(dn, LDAPInterface.getBaseDN());
      map.put(key,dn);
    }
  }
  
  private static class FindDNMapping extends LDAPMapping {
    private static final long serialVersionUID = -5644440406222654914L;
    private String fromAttr;
    private String destAttr;
    private boolean removeBase;
    
    FindDNMapping(String fromAttr, String destAttr, boolean removeBase) {
      this.fromAttr = fromAttr;
      this.destAttr = destAttr;
      this.removeBase = removeBase;
    }
    
    public void setupMapping(Properties parameters, Map<String, String> map) {
      map.put(key,key); // just in case
    }

    public void updateAttributes(Map<String, String> map) {
      String dn = map.get(fromAttr);
      if(StringUtils.isEmpty(dn)) {
        map.remove(key);
        return;
      }
      
      if(removeBase) {
        dn = removeBaseDN(dn, LDAPInterface.getBaseDN());
      }
      
      Map<String,String> search = LDAPInterface.getByDN(dn);
      if(null == search) {
        map.remove(key);
        return;
      }
      
      String value = search.get(destAttr);
      if(StringUtils.isEmpty(value)) {
        map.remove(key);
        return;
      }
      map.put(key, value);
    }
    
  }
  
  private static class FindQueryMapping extends LDAPMapping {
    private static final long serialVersionUID = -5644446546222654914L;
    private String fromAttr;
    private String destAttr;
    private String queryName;
    private String query;
    
    FindQueryMapping(String fromAttr, String queryName, String destAttr) {
      this.fromAttr = fromAttr;
      this.destAttr = destAttr;
      this.query = this.queryName = queryName;
    }
    
    public void setupMapping(Properties parameters, Map<String, String> map) {
      map.put(key,key); // just in case
      // Retrieve search query
      String qName = parameters.getProperty(queryName);
      if(StringUtils.isNotEmpty(qName)) this.query = qName;
    }

    public void updateAttributes(Map<String, String> map) {
      String searchValue = map.get(fromAttr);
      if(StringUtils.isEmpty(searchValue)) {
        map.remove(key);
        return;
      }
      
      String ldapQuery = MessageFormat.format(this.query, new Object[]{searchValue});
      Collection<Map<String,String>> results = LDAPInterface.searchDeep(ldapQuery);
      
      if(results.isEmpty()) {
        map.remove(key);
        return;
      }
      Map<String,String> search = results.iterator().next();
      if(null == search) {
        map.remove(key);
        return;
      }
      
      String value = search.get(destAttr);
      if(StringUtils.isEmpty(value)) {
        map.remove(key);
        return;
      }
      map.put(key, value);
    }
    
  }
  
  protected String removeBaseDN(String dn, String base) {
    try {
      LdapName nDn = new LdapName(dn);
      LdapName bDn = new LdapName(base);
      if(nDn.startsWith(bDn)) {
        List<Rdn> rdns = new ArrayList<Rdn>(nDn.size());
        for(int i = bDn.size(); i < nDn.size(); i++)
          rdns.add(nDn.getRdn(i));
        
        LdapName pDn = new LdapName(rdns);
        dn = pDn.toString();
      }
    
    } catch (Throwable t) {}
    return dn;
  }
}
