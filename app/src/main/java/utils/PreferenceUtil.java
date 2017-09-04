package utils;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by Administrator on 2017/8/25 0025.
 */

public class PreferenceUtil {

    private static XSharedPreferences instance=null;

    private static XSharedPreferences getInstance(){
        if (instance==null){
            instance=new XSharedPreferences("com.yang.java.wechat");
            instance.makeWorldReadable();
        }else {
            instance.reload();
        }
        return instance;
    }

    public static boolean is_impede_icon(){//去除冠军头像
        return getInstance().getBoolean("impede_icon",true);
    }

    public static boolean is_open(){//是否开启自定义文字
        return getInstance().getBoolean("open",false);
    }

    public static String get_text_value(){//自定义文字
        return getInstance().getString("text_value","");
    }

    public static boolean is_impede_background(){//阻止装逼封面
        return getInstance().getBoolean("impede",false);
    }

    public static boolean is_style_color(){//自定义文字颜色
        return getInstance().getBoolean("color",true);
    }

    public static String get_color_value(){//文字颜色值
        return getInstance().getString("color_value","");
    }

    public static boolean is_replace(){//自定义封面图片
        return getInstance().getBoolean("bac_img",false);
    }

    public static String get_path(){
        return getInstance().getString("path","");
    }

    public static boolean is_self(){
        return getInstance().getBoolean("self",false);
    }

    public static String get_self_path(){
        return getInstance().getString("self_path","");
    }

    public static boolean like(){//微信运动一键点赞
        return getInstance().getBoolean("like",false);
    }

    public static boolean is_like_color(){//是否自定义赞文字颜色
        return getInstance().getBoolean("like_color",false);
    }

    public static String get_like_color(){
        return getInstance().getString("like_text_color","");
    }

    public static boolean is_auto_login(){//电脑登陆自动确认
        return getInstance().getBoolean("auto_login",false);
    }

    public static boolean is_change(){//是否修改步数
        return getInstance().getBoolean("change",false);
    }
}
