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
