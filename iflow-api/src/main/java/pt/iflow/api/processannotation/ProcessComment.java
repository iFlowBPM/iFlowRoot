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
package pt.iflow.api.processannotation;


public class ProcessComment {

	private String comment;
	private String userid;
	private String date;
	private int commentid;
  
  public ProcessComment(String comment) {
    this.comment = comment;
  }
  
  public ProcessComment(String comment, String userid, String date, int commentid) {
	    this.comment = comment;
	    this.userid = userid;
	    this.date = date;
	    this.commentid = commentid;
  }
  
  public int getid() {
    return commentid;
  }

  public String getComment() {
	return comment;
  }
  public void setComment(String comment) {
	this.comment = comment;
  }
  
  public String getUser() {
    return userid;
  }
  
  public void setUser(String userid) {
	  this.userid = userid;
  }

  public String getDate(){
	  return date;
  }

  public void setDate(String date){
	  this.date = date;
  }

	  
}
