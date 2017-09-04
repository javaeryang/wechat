package utils;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Administrator on 2017/8/8 0008.
 */

public class Shell {
    public static boolean shell(String command){
        try {
            Process process=Runtime.getRuntime().exec("su");//申请权限
            OutputStream os=process.getOutputStream();//获取输出流
            DataOutputStream dos=new DataOutputStream(os);
            dos.writeBytes(command);//输入命令
            dos.flush();
            dos.close();
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ja","執行出錯");
            return false;
        }
    }
}
