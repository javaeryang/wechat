package com.yang.java.wechat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import utils.Shell;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button reboot,start_wechat;
    public static Context context;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            mSettingsFragment=new SettingsFragment();
            replaceFragment(R.id.settings_container,mSettingsFragment);
        }
        context=getApplicationContext();

        initUI();
        SharedPreferences preferences=getSharedPreferences("config",0);
        boolean show=preferences.getBoolean("show",true);
        if (show){
            showDialog();
        }
        boolean show_help=preferences.getBoolean("show_tip",true);
        if (show_help){
            showHelp();
        }

        //Log.i("ja","visible:"+ View.VISIBLE+" invisible:"+View.INVISIBLE+" gone:"+ View.GONE);

        //changeStepCount(getStepCount());
    }

    private void replaceFragment(int viewId, Fragment fragment){
        FragmentManager manager=getFragmentManager();
        manager.beginTransaction().replace(viewId,fragment).commit();
    }

    public static class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.pref_setting);

            Preference thank=findPreference("thank");
            thank.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://github.com/zhudongya123"));
                    startActivity(intent);
                    return true;
                }
            });

            Preference very=findPreference("very");
            very.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://github.com/veryyoung"));
                    startActivity(intent);
                    return true;
                }
            });

            Preference javaer=findPreference("author");
            javaer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://github.com/javaeryang"));
                    startActivity(intent);
                    return true;
                }
            });

            Preference donate=findPreference("donate");
            donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    String url="https://QR.ALIPAY.COM/FKX052505EWLFSEHGF4LDC";
                    intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode="+url));
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                        startActivity(intent);
                    }else {
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                    return true;
                }
            });

            Preference wechat=findPreference("wechat");
            wechat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent=new Intent(MainActivity.context,Donate.class);
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.choose_pic:
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0x11);
                break;
            case R.id.show_help:
                showHelp();
                break;
        }
        return true;
    }

    private HashMap<Integer, Long> hashMap = null;
    private HashMap<Integer, Long> getStepCount(){
        Shell.shell("am force-stop com.tencent.mm");
        Shell.shell("mount -o remount rw /system");
        Shell.shell("chmod 777 /data/data/com.tencent.mm/MicroMsg");
        Shell.shell("chmod 777 /data/data/com.tencent.mm/MicroMsg/stepcounter.cfg");
        File f=new File("/data/data/com.tencent.mm/MicroMsg/stepcounter.cfg");
        FileInputStream fis= null;
        try {
            fis = new FileInputStream(f);
            ObjectInputStream ois=new ObjectInputStream(fis);
            hashMap= (HashMap<Integer, Long>) ois.readObject();
            if (hashMap != null){
                Log.i("ja",hashMap.toString());
            }
            ois.close();
            fis.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private boolean changeStepCount(HashMap<Integer, Long> hashMap){
        boolean b=false;
        Shell.shell("am force-stop com.tencent.mm");
        Shell.shell("mount -o remount rw /system");
        Shell.shell("chmod 777 /data/data/com.tencent.mm/MicroMsg");
        Shell.shell("chmod 777 /data/data/com.tencent.mm/MicroMsg/stepcounter.cfg");
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.context);
        if (preferences.getBoolean("change",false)){
            if (hashMap != null){
                hashMap.put(201, (long) 98799);
                File f=new File("/data/data/com.tencent.mm/MicroMsg/stepcounter.cfg");
                try {
                    FileOutputStream fos=new FileOutputStream(f);
                    ObjectOutputStream oos=new ObjectOutputStream(fos);
                    oos.writeObject(hashMap);
                    oos.close();
                    fos.close();
                    Log.i("ja",hashMap.toString());
                    b=true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0x11:
                ContentResolver resolver=getContentResolver();
                if (data != null){
                    Uri uri=data.getData();
                    String[] proj={MediaStore.Images.Media.DATA};
                    Cursor cursor=managedQuery(uri,proj,null,null,null);
                    int index=cursor.getColumnIndexOrThrow(proj[0]);
                    cursor.moveToFirst();
                    String path=cursor.getString(index);
                    Log.i("ja",Environment.getExternalStorageDirectory().getPath());
                    Log.i("ja",path);
                    ClipboardManager cm= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setText(path);
                    Toast.makeText(MainActivity.this,"复制成功,粘贴到路径编辑框即可",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void initUI(){
        reboot= (Button) findViewById(R.id.reboot);//重启手机
        start_wechat= (Button) findViewById(R.id.start_wechat);//启动微信

        reboot.setOnClickListener(this);
        start_wechat.setOnClickListener(this);
    }

    private void showDialog(){
        final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("说明");
        dialog.setIcon(R.drawable.javaer);
        dialog.setMessage(context.getString(R.string.tip)+"\n\n"+context.getString(R.string.tip1)+"\n\n"+context.getString(R.string.tip2)+"\n\n"+context.getString(R.string.tip3));
        dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.setNegativeButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor=getSharedPreferences("config",0).edit();
                editor.putBoolean("show",false);
                editor.apply();
            }
        });
        dialog.show();
    }

    private void showHelp(){
        final AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("帮助");
        dialog.setIcon(R.drawable.javaer);
        dialog.setMessage(context.getString(R.string.h1)+"\n\n"+context.getString(R.string.h2)+"\n\n"+context.getString(R.string.h3)+"\n\n"+context.getString(R.string.h4));
        dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor=getSharedPreferences("config",0).edit();
                editor.putBoolean("show_tip",false);
                editor.apply();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reboot:
                try {
                    Utils.Shell("reboot");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.start_wechat:
                Intent intent=new Intent();
                PackageManager packageManager=MainActivity.this.getPackageManager();
                intent=packageManager.getLaunchIntentForPackage("com.tencent.mm");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
                break;
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (isEnable("com.yang.java.wechat/com.yang.java.wechat.IService")){
            status.setText(getString(R.string.open));
            status.setBackgroundColor(Color.GREEN);
        }else {
            status.setText(getString(R.string.close));
            status.setBackgroundColor(Color.MAGENTA);
        }
    }*/

    /*private boolean isEnable(String service) {//服务是否开启
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }*/

}
