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
package pt.iflow.api.delegations;

import javax.ejb.EJBException;

import pt.iflow.api.utils.UserInfoInterface;

public interface DelegationInfo
{
   /**
    * Returns all received delegations, for both deployed and undeployed flows
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getReceivedDelegations( UserInfoInterface userInfo );
   
   /**
    * Returns deployed flows received delegations
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getDeployedReceivedDelegations( UserInfoInterface userInfo );

   /**
    * Returns undeployed flows received delegations
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getUnDeployedReceivedDelegations( UserInfoInterface userInfo );

   /**
    * Returns all sent delegations, for both deployed and undeployed flows
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getSentDelegations( UserInfoInterface userInfo );

   /**
    * Returns deployed flows sent delegations
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getDeployedSentDelegations( UserInfoInterface userInfo );

   /**
    * Returns undeployed flows sent delegations
    * @param userInfo the requesting user
    * @return a collection of DelegationInfoData objects
    * @throws EJBException
    */
   public java.util.Collection<DelegationInfoData> getUnDeployedSentDelegations( UserInfoInterface userInfo );

}
