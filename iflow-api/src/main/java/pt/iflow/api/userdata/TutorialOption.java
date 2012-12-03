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
package pt.iflow.api.userdata;

public class TutorialOption {

  public final static String CLASS_ACTIVE = "active";
  public final static String CLASS_DONE = "done";
  public final static String CLASS_DEFAULT = "default";
  public final static String CLASS_NEXT = "next";

  Tutorial parent = null;
  
  private String name;
  private String cssclass;
  private boolean current;
  private boolean next;
  private String text;
  private boolean done;
  
  public TutorialOption (Tutorial parent, String name, String text) {
    this.parent = parent;
    this.name = name;
    this.cssclass = CLASS_DONE;
    this.current = false;
    this.done = false;
    this.next = false;
    this.text = text;
  }
  
  public String getCssclass() {
    return cssclass;
  }
  public void setCssclass(String cssclass) {
    this.cssclass = cssclass;
  }
  public boolean isCurrent() {
    return current;
  }
  public void setCurrent(boolean current) {
    this.current = current;
  }
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }

  private String format(String asClass) {

    String opt = isCurrent()?"maybe":(isDone()?"yes":"no");
    String prefix = "<img src=\"images/checkbox_" + opt + ".png\">";
    
    if (isNext()) {
      return "<li class=\"" + asClass + "\">" + prefix + text + "<a class=\"" + asClass + "\" href=\"javascript:openTutorial('" + name + "','false');\">" + parent.getPREFIX_NEXT_STEP() + "</a></li>"; 
    }
    else if (isCurrent() || CLASS_DONE.equals(asClass)) {
      return "<li class=\"" + asClass + "\">" + prefix + "<a class=\"" + asClass + "\" href=\"javascript:openTutorial('" + name + "','false');\">" + text + "</a></li>"; 
    }
    else {
      return "<li class=\"" + asClass + "\">" + prefix + text + "</li>";             
    }
  }
  
  public String getHtmlCode() {
    if ( isCurrent() ) {
      return format(CLASS_ACTIVE);
    }
    else if (isNext()){
      return format(CLASS_NEXT);      
    }
    else if (isDone()) {
      return format(CLASS_DONE);
    }
    else {
      return format(CLASS_DEFAULT);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isNext() {
    return next;
  }

  public void setNext(boolean next) {
    this.next = next;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
  
}

