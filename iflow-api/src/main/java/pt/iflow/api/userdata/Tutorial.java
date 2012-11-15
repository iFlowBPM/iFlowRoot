package pt.iflow.api.userdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.utils.UserInfoInterface;

public class Tutorial {

  public static final String TUTORIAL_DEFAULT = "none";

  public static final String TUTORIAL_USERS = "users";
  public static final String TUTORIAL_ORG_UNITS = "org_units";
  public static final String TUTORIAL_PROFILES = "profiles";
  public static final String TUTORIAL_FLOWS = "flows";
  public static final String TUTORIAL_PERMISSIONS = "permissions";
  public static final String TUTORIAL_MENUS = "menus";

  String PREFIX_CURRENT_STEP = ""; 
  String PREFIX_NEXT_STEP = "";
  String PREFIX_NONE = "";
    
  static List<String> steps;
  static Map<String,Integer> map;
  
  List<TutorialOption> tutorialOptions = null;
  Map<String, TutorialOption> optionMap = null;
  
  static {
    steps = new ArrayList<String>();
    map = new HashMap<String,Integer>();
    
    steps.add(TUTORIAL_USERS);
    steps.add(TUTORIAL_ORG_UNITS);
    steps.add(TUTORIAL_PROFILES);
    steps.add(TUTORIAL_FLOWS);
    steps.add(TUTORIAL_PERMISSIONS);
    steps.add(TUTORIAL_MENUS);
    
    int counter = 0;
    for(String step : steps) {
      map.put(step,counter++);
    }
    
  }
  
  public static List<String> getSteps() {
    return steps;
  }
  
  public static String nextStep (String currentStep) {
    try {
      if (map.containsKey(currentStep)) {
        return steps.get(map.get(currentStep)+1);
      }
    }
    catch (Exception e) {
    }
    return null;
  }
  
  public static Tutorial getInstance (UserInfoInterface userInfo) {
    return new Tutorial(userInfo);
  }
  
  private Tutorial (UserInfoInterface userInfo) {
    
    IMessages messages = userInfo.getMessages();
    PREFIX_CURRENT_STEP = messages.getString("main.tutorial.select.option.currentStep");
    PREFIX_NEXT_STEP = messages.getString("main.tutorial.select.option.nextStep");
    
    tutorialOptions = new ArrayList<TutorialOption>();
    optionMap = new HashMap<String,TutorialOption>();
    
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_USERS, messages.getString("main.tutorial.select.option.users")));
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_ORG_UNITS, messages.getString("main.tutorial.select.option.org_units")));
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_PROFILES, messages.getString("main.tutorial.select.option.profiles")));
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_FLOWS, messages.getString("main.tutorial.select.option.flows")));
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_PERMISSIONS, messages.getString("main.tutorial.select.option.permissions")));
    tutorialOptions.add(new TutorialOption(this, TUTORIAL_MENUS, messages.getString("main.tutorial.select.option.menus")));
    
    for(TutorialOption option: tutorialOptions) {
      optionMap.put(option.getName(), option);
    }

  }
  
  public String generateOptionsHtml () {
    StringBuffer sHtml = new StringBuffer();

    sHtml.append("<ul id=\"select_tutorial\" style=\"padding-left: 20px;\">");
    
    for (TutorialOption option : tutorialOptions) {
      sHtml.append(option.getHtmlCode());
    }

    sHtml.append("</ul>");
    
    return sHtml.toString();
  }
  
  
  private void resetOptions () {
    for (TutorialOption option : tutorialOptions) {
        option.setCssclass(TutorialOption.CLASS_DONE);
        option.setCurrent(false);
        option.setDone(false);
        option.setNext(false);
    }
  }
  
  private void setPrevious(String current) {
    List<TutorialOption> tutPrevList = getAllPrevious(current);
    if (tutPrevList != null) {
      for (TutorialOption tutorialOption : tutPrevList) {
        tutorialOption.setDone(true);
      }
    }
  }
  
  private boolean setPair(String current, String next) {
    
    boolean ret = true;
    
    if (current != null && optionMap.containsKey(current)) {
      optionMap.get(current).setCurrent(true);
    }
    else {
      ret = false;
    }
    
    if (next != null && optionMap.containsKey(next)) {
      optionMap.get(next).setNext(true);
    }
    
    setPrevious(current);
    return ret;
  }

  public boolean setCurrentOption(String current) {  
    resetOptions();
    return setPair(current, Tutorial.nextStep(current));
  }
  
  public List<TutorialOption> getAllPrevious(String current) {  
    
    TutorialOption tutOp = null;
    List<TutorialOption> tutPrevList = new ArrayList<TutorialOption>();
    
    if (current != null && optionMap.containsKey(current)) {
      tutOp = optionMap.get(current);
    }
    else {
      return null;
    }
    
    for (String step  : steps) {
      TutorialOption tutorialOption = optionMap.get(step);
      if (tutorialOption.getName().equals(tutOp.getName())){
        break;
      }
      else {
        tutPrevList.add(tutorialOption);
      }
    }
    
    return tutPrevList;
  }
  
  public TutorialOption getOption (String name) {
    if (name != null && optionMap.containsKey(name)) {
      return optionMap.get(name);
    }
    return null;
  }

  public String getPREFIX_NEXT_STEP() {
    return PREFIX_NEXT_STEP;
  }
  
  public String getPREFIX_CURRENT_STEP() {
    return PREFIX_CURRENT_STEP;
  }
  
}
  