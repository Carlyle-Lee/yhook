package lab.galaxy.yahfa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yuchaofei on 16/3/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HookVars {

    byte MATCH_EQUAL = 0x01;
    byte MATCH_LESS = 0x02;
    byte MATCH_GREATER = 0x04;

    int sdkVersion() default -1;

    //配合sdkVersion使用
    byte sdkType() default MATCH_EQUAL;

    String className();

    String methodName();

    String methodSig();
}
