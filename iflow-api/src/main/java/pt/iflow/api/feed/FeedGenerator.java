/*
 *
 * Created on May 23, 2005 by iKnow
 *
  */

package pt.iflow.api.feed;

import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import pt.iflow.api.core.Activity;
import pt.iflow.api.presentation.FlowAppMenu;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.notification.Notification;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */
public interface FeedGenerator {
  public void generateFlowsFeed(UserInfoInterface user,Collection<FlowAppMenu> categories,String baseUrl, Writer writer);
  public void generateTasksFeed(UserInfoInterface user,Collection<FlowAppMenu> categories,Map<Integer,Iterator<Activity>> activityData,String baseUrl, Writer writer);
  public void generateTasksFeed(UserInfoInterface user,Collection<FlowAppMenu> cats,Map<Integer,Iterator<Activity>> activityData, String baseUrl,Writer writer,boolean isOverview);
  public void generateMessagesFeed(UserInfoInterface user,Collection<Notification> collection,String baseUrl, Writer writer);
  public void generateCategoriesFeed(UserInfoInterface user,Collection<FlowAppMenu> categories,String baseUrl, Writer writer);
  public void generateEmptyFeed(String baseUrl, Writer writer);
  public void generateTasksFeedAtom(UserInfoInterface user,Collection<FlowAppMenu> cats,Map<Integer,Iterator<Activity>> activityData, String baseUrl,Writer writer);
}
