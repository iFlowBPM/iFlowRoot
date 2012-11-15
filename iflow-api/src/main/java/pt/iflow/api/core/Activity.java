package pt.iflow.api.core;

import java.sql.Timestamp;
import java.util.Date;

public class Activity {
  public String userid;
  public int flowid;
  public int pid;
  public int subpid;
  public int type;
  public int priority;
  public Date created;
  public Date started;
  public Date archived;
  public String description;
  public String url;
  public int status;
  public boolean notify;
  public boolean delegated;
  public String profilename;
  public String pnumber;
  private boolean read;
  public int mid = -1;
  public int folderid = -1;
  public String annotationIcon = "";
  
  public Activity() {
    super();
    // Default constructor to avoid some axis problems
  }

  // copy constructor
  public Activity(Activity orig) {
    this();
    userid = orig.userid;
    flowid = orig.flowid;
    pid = orig.pid;
    subpid = orig.subpid;
    type = orig.type;
    priority = orig.priority;
    created = orig.created;
    started = orig.started;
    archived = orig.archived;
    description = orig.description;
    url = orig.url;
    status = orig.status;
    notify = orig.notify;
    delegated = orig.delegated;
    profilename = orig.profilename;
    pnumber = orig.pnumber;
    read = orig.read;
    mid = orig.mid;
  }
  
  public Activity(String u, int f, int p, int sp, Timestamp c, String descr, String ur) {
    this();
    userid = u;
    flowid = f;
    pid = p;
    subpid = sp;
    created = c;
    description = descr;
    url = ur;
    notify = true;
    delegated = true;
  }

  public Activity(String u, int f, int p, int sp, int t, int pri, String descr, String ur) {
    this();
    userid = u;
    flowid = f;
    pid = p;
    subpid = sp;
    type = t;
    priority = pri;
    description = descr;
    url = ur;
    notify = true;
    delegated = false;
  }

  public Activity(String u, int f, int p, int sp, int t, int pri, String descr, String ur, int notif) {
    this();
    userid = u;
    flowid = f;
    pid = p;
    subpid = sp;
    type = t;
    priority = pri;
    description = descr;
    url = ur;
    if (notif == 1) {
      notify = true;
    }
    else {
      notify = false;
    }
	 delegated = false;
  }

  public Activity(String u, int f, int p, int sp, int t, int pri,
    Timestamp s, Timestamp d, Timestamp a, String descr, String ur, int stat, int notif) {
    this();
    userid = u;
    flowid = f;
    pid = p;
    subpid = sp;
    type = t;
    priority = pri;
    created = s;
    started = d;
    archived = a;
    description = descr;
    url = ur;
    status = stat;
    if (notif == 1) {
      notify = true;
    }
    else {
      notify = false;
    }
	 delegated = false;
  }

  public Activity(String u, int f, int p, int sp, int t, int pri,
		  Timestamp s, Timestamp d, Timestamp a, String descr, String ur, int stat, int notif, String profName) {
	  this();
	  userid = u;
	  flowid = f;
	  pid = p;
	  subpid = sp;
	  type = t;
	  priority = pri;
	  created = s;
	  started = d;
	  archived = a;
	  description = descr;
	  url = ur;
	  status = stat;
	  profilename = profName;
	  if (notif == 1) {
		  notify = true;
	  }
	  else {
		  notify = false;
	  }
	  delegated = false;
  }

  /**
   * 
   * Getter method for archived
   * 
   * @return Returns the archived.
   */
  public Date getArchived() {
    return archived;
  }

  /**
   * 
   * Getter method for created
   * 
   * @return Returns the created.
   */
  public Date getCreated() {
    return created;
  }

  /**
   * 
   * Getter method for delegated
   * 
   * @return Returns the delegated.
   */
  public boolean getDelegated() {
    return delegated;
  }

  /**
   * 
   * Getter method for description
   * 
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * 
   * Getter method for flowid
   * 
   * @return Returns the flowid.
   */
  public int getFlowid() {
    return flowid;
  }

  /**
   * 
   * Getter method for notify
   * 
   * @return Returns the notify.
   */
  public boolean getNotify() {
    return notify;
  }

  /**
   * 
   * Getter method for pid
   * 
   * @return Returns the pid.
   */
  public int getPid() {
    return pid;
  }

  /**
   * 
   * Getter method for priority
   * 
   * @return Returns the priority.
   */
  public int getPriority() {
    return priority;
  }

  /**
   * 
   * Getter method for started
   * 
   * @return Returns the started.
   */
  public Date getStarted() {
    return started;
  }

  /**
   * 
   * Getter method for status
   * 
   * @return Returns the status.
   */
  public int getStatus() {
    return status;
  }

  /**
   * 
   * Getter method for subpid
   * 
   * @return Returns the subpid.
   */
  public int getSubpid() {
    return subpid;
  }

  /**
   * 
   * Getter method for type
   * 
   * @return Returns the type.
   */
  public int getType() {
    return type;
  }

  /**
   * 
   * Getter method for url
   * 
   * @return Returns the url.
   */
  public String getUrl() {
    return url;
  }

  /**
   * 
   * Getter method for userid
   * 
   * @return Returns the userid.
   */
  public String getUserid() {
    return userid;
  }

  public int getFolderid() {
	  return this.folderid;
  }
  
  /**
   * 
   * Getter method for profilename
   * 
   * @return Returns the profilename.
   */
  public String getProfileName() {
    return profilename;
  }
  
  public void setRead() {
    read = true;
  }
  
  public void setUnread() {
    read = false;
  }
  
  public void setFolderid(int folderid) {
	    this.folderid = folderid;
	  }
  
  public boolean isRead() {
    return read;
  }

  public String getAnnotationIcon() {
    return annotationIcon;
  }

  public void setAnnotationIcon(String annotationIcon) {
    this.annotationIcon = annotationIcon;
  }
}
