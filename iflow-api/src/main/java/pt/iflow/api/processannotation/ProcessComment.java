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
