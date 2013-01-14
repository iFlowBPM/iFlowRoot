package pt.iknow.floweditor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface WebFormAction {
  String EventName();
  WebFileType ActionType() default WebFileType.HTML;
}
