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
