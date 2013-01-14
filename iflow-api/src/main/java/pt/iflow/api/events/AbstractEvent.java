package pt.iflow.api.events;



public abstract class AbstractEvent {

  public Boolean processEvent(EventData event) {
    return processEvent(
        event.getUserId(),
        new Integer(event.getId()),
        new Integer(event.getPid()),
        new Integer(event.getSubPid()),
        new Integer(event.getFid()),
        new Integer(event.getBlockid()),
        new Long(event.getStarttime()),
        event.getType(),
        event.getProperties()
    );
  }

  public abstract Boolean processEvent(String userId, Integer id, Integer pid, Integer subpid, Integer fid, Integer blockid, Long starttime, String type, String properties);

  public abstract Boolean processEvent();
  
  public abstract Integer initialEventCode();
}

				      
