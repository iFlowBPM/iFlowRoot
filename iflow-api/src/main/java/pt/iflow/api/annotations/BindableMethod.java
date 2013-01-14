package pt.iflow.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.iflow.api.processtype.DataTypeEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BindableMethod {
  String[] variables();
  DataTypeEnum dataType() default DataTypeEnum.Text;
}
