/*
 *
 * Created on May 23, 2005 by iKnow
 *
 */

package pt.iflow.feed;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.iflow.api.core.Activity;
import pt.iflow.api.feed.FeedGenerator;
import pt.iflow.api.flows.IFlowData;
import pt.iflow.api.msg.IMessages;
import pt.iflow.api.notification.Notification;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.presentation.FlowMenuItems;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.msg.Messages;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.impl.DateParser;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */
public class DefaultFeedGenerator implements FeedGenerator {

  public void generateCategoriesFeed(UserInfoInterface user,Collection<FlowAppMenu> cats,String baseUrl, Writer writer) {
    SyndFeedOutput output = new SyndFeedOutput();
    IMessages msg = user.getMessages();

    SyndFeed feed = new SyndFeedImpl();

    final Date today = new Date();
    feed.setFeedType(Const.FEED_FORMAT);
    feed.setTitle(msg.getString("DefaultFeedGenerator.categories.title")); //$NON-NLS-1$
    feed.setLink(baseUrl + "GoTo?goto=flows.jsp"); //$NON-NLS-1$
    feed.setDescription(msg.getString("DefaultFeedGenerator.categories.description")); //$NON-NLS-1$
    feed.setPublishedDate(today);
    List<SyndEntry> entries = new ArrayList<SyndEntry>();

    Iterator<FlowAppMenu> iter = cats.iterator();
    while(iter.hasNext()) {
      FlowAppMenu item = iter.next();
      SyndEntry entry = new SyndEntryImpl();
      SyndContent description = new SyndContentImpl();
      String url = baseUrl + "FeedServlet?feed=flows&category=" + item.getAppID();
      description.setType("text/plain"); //$NON-NLS-1$
      description.setValue("<a href=\"" + url + "\">" + msg.getString("DefaultFeedGenerator.categories.link") + item.getAppDesc() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      entry.setTitle(item.getAppDesc());
      entry.setLink(url);
      entry.setPublishedDate(today);
      entry.setDescription(description);

      entries.add(entry);

    }

    feed.setEntries(entries);

    try {
      output.output(feed,writer);
      writer.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateFlowsFeed(UserInfoInterface user,Collection<FlowAppMenu> categories,String baseUrl, Writer writer) {
    SyndFeedOutput output = new SyndFeedOutput();
    IMessages msg = user.getMessages();

    SyndFeed feed = new SyndFeedImpl();

    feed.setFeedType(Const.FEED_FORMAT);
    feed.setTitle(msg.getString("DefaultFeedGenerator.flows.title")); //$NON-NLS-1$
    feed.setLink(baseUrl + "GoTo?goto=flows.jsp"); //$NON-NLS-1$
    feed.setDescription(msg.getString("DefaultFeedGenerator.flows.description")); //$NON-NLS-1$

    List<SyndEntry> entries = new ArrayList<SyndEntry>();
    List<SyndCategory> catList = new ArrayList<SyndCategory>();
    List<SyndCategory> entryCatList = null;
    SyndCategory syndCat = null;

    Date lastFlowDate = new Date(0);
    Iterator<FlowAppMenu> iterCat = categories.iterator();
    while(iterCat.hasNext()) {
      FlowAppMenu appMenu = iterCat.next();
      
      String catName = appMenu.getAppDesc();
      syndCat = new SyndCategoryImpl();
      syndCat.setName(catName);
      entryCatList = new ArrayList<SyndCategory>();
      catList.add(syndCat);
      entryCatList.add(syndCat);
      
      FlowMenuItems menuItems = appMenu.getMenuItems();
      

      Iterator<IFlowData> iter = menuItems.getFlows().iterator();
      while(iter.hasNext()) {
        IFlowData aFlow = iter.next();
        
        SyndEntry entry = new SyndEntryImpl();
        SyndContent description = new SyndContentImpl();
        String url = baseUrl + "GoTo?goto=inicio_flow.jsp&flowid=" + aFlow.getId();// + "&channel=rss&key=" + user.getFeedKey(); //$NON-NLS-1$ //$NON-NLS-2$
        description.setType("text/plain"); //$NON-NLS-1$
        description.setValue("<a href=\"" + url + "\">" + msg.getString("DefaultFeedGenerator.flows.link") + aFlow.getName() + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Date lastModified = new Date(aFlow.getLastModified());
        entry.setTitle(aFlow.getName());
        entry.setLink(url);
        entry.setPublishedDate(lastModified);
        entry.setDescription(description);
        
        entry.setCategories(entryCatList);
        
        entries.add(entry);
        if(lastModified.after(lastFlowDate)) lastFlowDate = lastModified;
      }

    }
    
    feed.setEntries(entries);
    feed.setCategories(catList);
    feed.setPublishedDate(lastFlowDate);    

    try {
      output.output(feed,writer);
      writer.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateTasksFeed(UserInfoInterface user,Collection<FlowAppMenu> cats,Map<Integer,Iterator<Activity>> activityData, String baseUrl,Writer writer) {
	  generateTasksFeed(user, cats, activityData, baseUrl, writer, false);
  }
  
  public void generateTasksFeed(UserInfoInterface user,Collection<FlowAppMenu> cats,Map<Integer,Iterator<Activity>> activityData, String baseUrl,Writer writer,boolean isOverview) {

    SyndFeedOutput output = new SyndFeedOutput();
    IMessages msg = user.getMessages();

    SyndFeed feed = new SyndFeedImpl();

    feed.setFeedType(Const.FEED_FORMAT);
    if(isOverview) {
      feed.setTitle(msg.getString("DefaultFeedGenerator.tasksoverview.title"));
    } else {
    	feed.setTitle(msg.getString("DefaultFeedGenerator.tasks.title")); 
    }
    feed.setLink(baseUrl + "GoTo?goto=actividades.jsp"); //$NON-NLS-1$
    feed.setDescription(msg.getString("DefaultFeedGenerator.tasks.description")); //$NON-NLS-1$
    
    List<SyndEntry> entries = new ArrayList<SyndEntry>();
    List<SyndCategory> catList = new ArrayList<SyndCategory>();
    SyndCategory syndCat = null;
    List<SyndCategory> entryCatList = null;

    Iterator<FlowAppMenu> iterCat = cats.iterator();
    Date channelDate = new Date(0);
    while(iterCat.hasNext()) {
      FlowAppMenu appMenu = iterCat.next();
      
      String catName = appMenu.getAppDesc();
      syndCat = new SyndCategoryImpl();
      syndCat.setName(catName);
      entryCatList = new ArrayList<SyndCategory>();
      catList.add(syndCat);
      entryCatList.add(syndCat);
      
      FlowMenuItems menuItems = appMenu.getMenuItems();
      

      Iterator<IFlowData> iter = menuItems.getFlows().iterator();
      while(iter.hasNext()) {
        IFlowData aFlow = iter.next();

        Iterator<Activity> flowActivities = activityData.get(aFlow.getId());
        if (flowActivities.hasNext()) {
          SyndEntry entry = new SyndEntryImpl();
          SyndContent description = new SyndContentImpl();

          description.setType("text/plain"); //$NON-NLS-1$


          entry.setTitle(msg.getString("DefaultFeedGenerator.tasks.syndTitle") + aFlow.getName()); //$NON-NLS-1$
          entry.setLink(baseUrl + "GoTo?goto=actividades.jsp&showflowid=" + aFlow.getId()); //$NON-NLS-1$


          //List subEntries = new ArrayList();
          StringBuilder descText = new StringBuilder(msg.getString("DefaultFeedGenerator.tasks.syndDescription") + aFlow.getName() + ":<br>"); //$NON-NLS-1$ //$NON-NLS-2$
          Date last = new Date(0);
          Date first = new Date();
          int counter = 0; 
          while (flowActivities.hasNext()) {
            counter++;
            SyndContent subEntry = new SyndContentImpl();
            subEntry.setType("text/plain"); //$NON-NLS-1$

            Activity theActivity = flowActivities.next();
            String url = baseUrl + "GoTo?goto=actividade_user.jsp&flowid=" + aFlow.getId() + "&pid=" + theActivity.pid +"&subpid=" + theActivity.subpid;// + "&channel=rss&key="+user.getFeedKey(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            descText.append("<a href=\"" + url + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
            descText.append(msg.getString("DefaultFeedGenerator.tasks.task") + theActivity.pid + msg.getString("DefaultFeedGenerator.tasks.toFlow") + aFlow.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            descText.append(msg.getString("DefaultFeedGenerator.tasks.initiated") + DateParser.formatRFC822(theActivity.created)); //$NON-NLS-1$
            descText.append(msg.getString("</a><br>")); //$NON-NLS-1$

            if(theActivity.created.before(first)) {
              first = theActivity.created;
            }
            if(theActivity.created.after(last)) {
              last=theActivity.created;
            }
          }
          if(isOverview) {
            entry.setPublishedDate(first);
            description.setValue("" + counter);
            entry.setDescription(description);
          } else {
          entry.setPublishedDate(last);
          description.setValue(descText.toString());
          entry.setDescription(description);
          }
          entry.setCategories(entryCatList);
          entries.add(entry);
          if(last.after(channelDate)) channelDate=last;
        }
      }
    }

    feed.setEntries(entries);
    feed.setCategories(catList);
    feed.setPublishedDate(channelDate);

    try {
      output.output(feed,writer);
      writer.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateMessagesFeed(UserInfoInterface user,Collection<Notification> messages,String baseUrl, Writer writer) {
    SyndFeedOutput output = new SyndFeedOutput();
    IMessages msg = user.getMessages();

    SyndFeed feed = new SyndFeedImpl();

    feed.setFeedType(Const.FEED_FORMAT);
    feed.setTitle(msg.getString("DefaultFeedGenerator.messages.title")); //$NON-NLS-1$
    feed.setLink(baseUrl + "GoTo?goto=inbox.jsp"); //$NON-NLS-1$
    feed.setDescription(msg.getString("DefaultFeedGenerator.messages.description")); //$NON-NLS-1$
    List<SyndEntry> entries = new ArrayList<SyndEntry>();

    Iterator<Notification> iter = messages.iterator();
    Date last = new Date(0);
    while(iter.hasNext()) {
      Notification notif = iter.next();
      SyndEntry entry = new SyndEntryImpl();
      SyndContent description = new SyndContentImpl();
      String url = baseUrl + "GoTo?goto=inbox.jsp";// + "&channel=rss&key=" + user.getFeedKey(); //$NON-NLS-1$ //$NON-NLS-2$
      description.setType("text/plain"); //$NON-NLS-1$
      description.setValue("<a href=\"" + url + "\">" + msg.getString("DefaultFeedGenerator.messages.description") + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

      entry.setTitle(notif.getMessage());
      entry.setLink(url);
      entry.setPublishedDate(notif.getCreated());
      entry.setDescription(description);

      entries.add(entry);

      if(notif.getCreated().after(last)) last = notif.getCreated();
    }

    feed.setPublishedDate(last);
    feed.setEntries(entries);

    try {
      output.output(feed,writer);
      writer.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void generateEmptyFeed(String baseUrl, Writer writer) {
    if(Logger.isDebugEnabled()) {
      Logger.debug("<none>", this, "generateEmptyFeed", "No user is defined, generating empty feed.");
    }
    SyndFeedOutput output = new SyndFeedOutput();
    IMessages msg = Messages.getInstance();
    SyndFeed feed = new SyndFeedImpl();
    feed.setFeedType(Const.FEED_FORMAT);
    feed.setTitle(msg.getString("DefaultFeedGenerator.empty.title"));
    feed.setLink(baseUrl + "/iFlow/login.jsp"); //$NON-NLS-1$
    feed.setDescription(msg.getString("DefaultFeedGenerator.empty.description"));
    try {
      output.output(feed, writer);
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  
  
  public void generateTasksFeedAtom(UserInfoInterface user,Collection<FlowAppMenu> cats,Map<Integer,Iterator<Activity>> activityData, String baseUrl,Writer writer) {
	  SyndFeedOutput output = new SyndFeedOutput();
	    IMessages msg = user.getMessages();

	    SyndFeed feed = new SyndFeedImpl();

	    feed.setFeedType("atom_1.0");
	    feed.setTitle(msg.getString("DefaultFeedGenerator.tasks.title")); //$NON-NLS-1$
	    feed.setLink(baseUrl + "GoTo?goto=actividades.jsp"); //$NON-NLS-1$
	    feed.setDescription(msg.getString("DefaultFeedGenerator.tasks.description")); //$NON-NLS-1$
	    
	    List<SyndEntry> entries = new ArrayList<SyndEntry>();
	    List<SyndCategory> catList = new ArrayList<SyndCategory>();
	    SyndCategory syndCat = null;
	    List<SyndCategory> entryCatList = null;
	    
	    Iterator<FlowAppMenu> iterCat = cats.iterator();
	    Date channelDate = new Date(0);
	    while(iterCat.hasNext()) {
	      FlowAppMenu appMenu = iterCat.next();
	      
	      String catName = appMenu.getAppDesc();
	      syndCat = new SyndCategoryImpl();
	      syndCat.setName(catName);
	      entryCatList = new ArrayList<SyndCategory>();
	      catList.add(syndCat);
	      entryCatList.add(syndCat);
	      
	      FlowMenuItems menuItems = appMenu.getMenuItems();
	      

	      Iterator<IFlowData> iter = menuItems.getFlows().iterator();
	      while(iter.hasNext()) {
	        IFlowData aFlow = iter.next();

	        Iterator<Activity> flowActivities = activityData.get(aFlow.getId());
	        if (flowActivities.hasNext()) {
//	          SyndEntry entry = new SyndEntryImpl();
//	          SyndContent description = new SyndContentImpl();
//
//	          description.setType("text/plain"); //$NON-NLS-1$


//	          entry.setTitle(msg.getString("DefaultFeedGenerator.tasks.syndTitle") + aFlow.getName()); //$NON-NLS-1$
//	          entry.setLink(baseUrl + "GoTo?goto=actividades.jsp&showflowid=" + aFlow.getId()); //$NON-NLS-1$


	          //List subEntries = new ArrayList();
	          StringBuilder descText = new StringBuilder(msg.getString("DefaultFeedGenerator.tasks.syndDescription") + aFlow.getName() + ":<br>"); //$NON-NLS-1$ //$NON-NLS-2$
	          Date last = new Date(0);
	          while (flowActivities.hasNext()) {
		          SyndEntry entry = new SyndEntryImpl();
		          SyndContent description = new SyndContentImpl();

		          description.setType("text/plain"); //$NON-NLS-1$
	        	  
	            SyndContent subEntry = new SyndContentImpl();
	            subEntry.setType("text/plain"); //$NON-NLS-1$

	            Activity theActivity = flowActivities.next();
	            String url = baseUrl + "GoTo?goto=actividade_user.jsp&flowid=" + aFlow.getId() + "&pid=" + theActivity.pid +"&subpid=" + theActivity.subpid;// + "&channel=rss&key="+user.getFeedKey(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	            descText.append("<a href=\"" + url + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
	            descText.append(msg.getString("DefaultFeedGenerator.tasks.task") + theActivity.pid + msg.getString("DefaultFeedGenerator.tasks.toFlow") + aFlow.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	            descText.append(msg.getString("DefaultFeedGenerator.tasks.initiated") + DateParser.formatRFC822(theActivity.created)); //$NON-NLS-1$
	            descText.append(msg.getString("</a><br>")); //$NON-NLS-1$
	            if(theActivity.created.after(last)) last=theActivity.created;
	            
	            String titulo = aFlow.getName()+" : "+theActivity.getDescription();
	            
	            if(theActivity.description.equals(""))
	            	titulo = aFlow.getName()+" : "+theActivity.getPid();
	            
		          entry.setTitle(titulo); //$NON-NLS-1$
		          entry.setLink(url); //$NON-NLS-1$
		          
		          entry.setPublishedDate(last);
//		          description.setValue(descText.toString());
//		          entry.setDescription(description);
		          entry.setCategories(entryCatList);
		          entries.add(entry);
	          }
//	          entry.setPublishedDate(last);
//	          description.setValue(descText.toString());
//	          entry.setDescription(description);
//	          entry.setCategories(entryCatList);
//	          entries.add(entry);
	          if(last.after(channelDate)) channelDate=last;
	        }
	      }
	    }

	    feed.setEntries(entries);
	    feed.setCategories(catList);
	    feed.setPublishedDate(channelDate);

	    try {
	      output.output(feed,writer);
	      writer.close();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
  
  
  
}
