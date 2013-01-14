package pt.iflow.presentation;

public class ProcessEndDisplaySettings {

  public enum TaskType { NEWEST, LATEST };
  
  
  private int flowid = -1;
  private TaskType type = TaskType.NEWEST;
  private int num_tasks = 5;
  
  
  public static ProcessEndDisplaySettings getDefault() {
    return new ProcessEndDisplaySettings();
  }
  
  public ProcessEndDisplaySettings setFlowId(int flowid) {
    this.flowid = flowid;
    return this;
  }
  
  public ProcessEndDisplaySettings setFlowId(String flowid) {
    this.flowid = Integer.parseInt(flowid);
    return this;
  }
  
  
  public ProcessEndDisplaySettings setTaskType(TaskType type) {
    this.type = type;
    return this;
  }

  public ProcessEndDisplaySettings setNumTasks(int num_tasks) {
    this.num_tasks = num_tasks;
    return this;
  }

  public int getFlowid() {
    return flowid;
  }

  public TaskType getType() {
    return type;
  }

  public int getNumTasks() {
    return num_tasks;
  }
  
  public boolean hasFlowid() {
    return flowid > 0;
  }

}