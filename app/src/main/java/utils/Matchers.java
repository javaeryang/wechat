package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/7 0007.
 */

public class Matchers {

    public static String match(String p, String str){
        Pattern pattern=Pattern.compile(p);
        Matcher m=pattern.matcher(str);
        if (m.find()){
            return m.group(1);
        }
        return null;
    }
}
