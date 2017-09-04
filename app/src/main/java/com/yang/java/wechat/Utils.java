package com.yang.java.wechat;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class Utils {

    public static final String TAG="Javaer";

    public static boolean Shell(String command) throws IOException {//Shell命令
        java.lang.Process process=null;
        DataOutputStream os=null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"ROOT REE: "+e.getMessage());
            return false;
        }finally {
            try{
                if (os!=null)
                    os.close();
                process.destroy();
            }catch (Exception e){

            }
        }
        Log.i(TAG,"ROOT SUC");
        return true;
    }
}
