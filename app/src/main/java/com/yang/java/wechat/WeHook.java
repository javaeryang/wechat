package com.yang.java.wechat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import utils.Matchers;
import utils.PreferenceUtil;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Administrator on 2017/8/1 0001.
 */

public class WeHook implements IXposedHookLoadPackage{

    private ClassLoader classLoader=null;
    private static final String pkg="com.tencent.mm";
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (! loadPackageParam.packageName.equals(pkg))
            return;
        Context mContext = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
        String versionName=mContext.getPackageManager().getPackageInfo(loadPackageParam.packageName,0).versionName;
        XposedBridge.log("Hello WeHook: "+versionName);

        classLoader=loadPackageParam.classLoader;

        String getMessageClass = "com.tencent.mm.e.b.cd";//获取微信消息
        final List<String> goldMessages=new ArrayList<>();
        final List<String> inviteUrls=new ArrayList<>();
        XposedHelpers.findAndHookMethod(getMessageClass, loadPackageParam.classLoader, "b", Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.thisObject;
                int type= (int) XposedHelpers.getObjectField(object,"field_type");
                String talker= (String) XposedHelpers.getObjectField(object,"field_talker");
                int isSend= (int) XposedHelpers.getObjectField(object,"field_isSend");
                String content= (String) XposedHelpers.getObjectField(object,"field_content");

                if (type==49 && content.contains("腾讯黄金红包")){
                    String gols_url=content.split("<url>|</url>")[1];
                    if (!isInstance(gols_url,goldMessages)){

                        startWebViewUI(loadPackageParam.classLoader,gols_url);

                        goldMessages.add(gols_url);

                        XposedBridge.log("黄金红包链接集合:"+goldMessages.toString());

                        XposedBridge.log("type: "+type+", talker: "+talker+", url: "+gols_url+", isSend"+isSend);
                    }

                }/*else {
                    Log.i("ja","type: "+type+", talker: "+talker+", content: "+content+", isSend"+isSend);
                }*/
                if (type==49 && content.contains("邀请你加入群聊") &&content.contains("，进入可查看详情。") && isSend==0){
                    String tmp=content.split("<url>|</url>")[1];
                    String invite_url=tmp.substring(9,tmp.lastIndexOf("]")-1);

                    String match= Matchers.match("<url><!\\[CDATA\\[(.*?)\\]\\]></url>",content);

                    XposedBridge.log("match匹配:"+match);

                    if (!isInstance(invite_url,inviteUrls)){

                        startWebViewUI(loadPackageParam.classLoader,invite_url);

                        inviteUrls.add(invite_url);

                        XposedBridge.log("群邀请链接集合:"+inviteUrls.toString());
                    }
                }

                if (type==436207665 || type==469762097){
                    String luckymoney_msgid=Matchers.match("<!\\[CDATA\\[(\\d+)\\]\\]>",content);
                    XposedBridge.log("红包id: "+luckymoney_msgid);
                }


            }
        });

        String cls5 = "com.tencent.mm.plugin.exdevice.ui.ExdeviceRankChampionInfoView";//微信运动冠军文字
        String m5="uq";
        findAndHookMethod(cls5, loadPackageParam.classLoader, m5, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.thisObject;
                //String paramString= (String) param.args[0];
                //XposedBridge.log("参数String: "+paramString);
                TextView tv= (TextView) getObjectField(object,"krF");//自定义文字
                if (PreferenceUtil.is_open()){
                    if (PreferenceUtil.get_text_value().equals("")){
                        tv.setText("Javaer");
                    }else {
                        tv.setText(PreferenceUtil.get_text_value());
                    }
                }
                //XposedBridge.log("状态:"+PreferenceUtil.is_open());
                if (PreferenceUtil.is_style_color()){
                    if (PreferenceUtil.get_color_value().equals("")){//自定义文字颜色
                        tv.setTextColor(Color.MAGENTA);
                    }else {
                        tv.setTextColor(Color.parseColor(PreferenceUtil.get_color_value()));
                    }

                }
                setObjectField(object,"krF",tv);

                ImageView img= (ImageView) getObjectField(object,"liN");//去除冠军头像
                if (PreferenceUtil.is_impede_icon()){
                    img.setImageBitmap(null);
                    img.setVisibility(View.GONE);
                }
                if (PreferenceUtil.is_self()){
                    if (!PreferenceUtil.get_self_path().equals("")){
                        Bitmap bit=BitmapFactory.decodeFile(PreferenceUtil.get_self_path());
                        img.setImageBitmap(bit);
                    }
                }
                setObjectField(object,"liN",img);
            }
        });

        /*Class<?> cls9=classLoader.loadClass("com.tencent.mm.plugin.luckymoney.ui.i");
        findAndHookMethod(cls9, "oJ", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.getResult();
                Object nkD=object.getClass().getField("nkD").get(object);//抢到的红包金额
                Object nkE=object.getClass().getField("nkE").get(object);//抢包顺序
                Object nkQ=object.getClass().getField("nkQ").get(object);//抢包者名字
                Object nkR=object.getClass().getField("nkR").get(object);//
                Object nkS=object.getClass().getField("nkS").get(object);//抢包后留言
                Object nkT=object.getClass().getField("nkT").get(object);//是否手气最佳,否为空
                Object nkh=object.getClass().getField("nkh").get(object);//红包id
                Object userName=object.getClass().getField("userName").get(object);//抢包者wxid
                XposedBridge.log(" nkD:"+nkD.toString()+" nkE:"+nkE.toString()
                        +" nkQ:"+nkQ.toString()+" nkR:"+nkR.toString()
                        +" nkS:"+nkS.toString()+" nkT:"+nkT.toString()
                        +" nkh:"+nkh.toString()+" userName:"+userName.toString());
            }
        });*/

        /*findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               ClassLoader clsLoader =((Context)param.args[0]).getClassLoader();
                String cls11="com.tencent.mm.plugin.exdevice.ui.ExdeviceLikeView";
                Class<?> c= clsLoader.loadClass(cls11);
                XposedBridge.log("START HOOK 构造函数");
                //hook里面再加hook
                XposedBridge.hookAllConstructors(c, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        TextView tv= (TextView) findFirstFieldByExactType(param.thisObject.getClass(),TextView.class).get(param.thisObject);

                        XposedBridge.log("获取TextView");

                        XposedBridge.log("读取tv文字:"+tv.getText().toString());
                    }
                });
            }
        });*/

        String cls13="com.tencent.mm.plugin.exdevice.ui.ExdeviceRankInfoUI";
        /*findAndHookMethod(cls13, loadPackageParam.classLoader, "c", loadPackageParam.classLoader.loadClass(cls13), String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String path= (String) param.args[1];
                XposedBridge.log("分享图片路径:"+path);
            }
        });*/
        findAndHookMethod(cls13, loadPackageParam.classLoader, "aox", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.thisObject;
                if (PreferenceUtil.is_impede_background()){//阻止装逼封面
                    ImageView img= (ImageView) getObjectField(object,"lmU");
                    //XposedBridge.log("获取图片0");
                    img.setVisibility(View.GONE);
                    setObjectField(object,"lmU",img);
                    //XposedBridge.log("修改完毕0");
                }
                if (PreferenceUtil.is_replace()){
                    if (!PreferenceUtil.get_path().equals("")){
                        ImageView img= (ImageView) getObjectField(object,"lmU");
                        //XposedBridge.log("获取图片1");
                        Bitmap bit= BitmapFactory.decodeFile(PreferenceUtil.get_path());
                        img.setImageBitmap(bit);
                        setObjectField(object,"lmU",img);
                        //XposedBridge.log("修改完毕1");
                    }
                }
            }
        });

        String cls15="com.tencent.mm.plugin.exdevice.ui.ExdeviceLikeView";
        findAndHookConstructor(cls15, loadPackageParam.classLoader, Context.class, AttributeSet.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!PreferenceUtil.is_like_color())
                    return;
                Object object=param.thisObject;
                TextView tv= (TextView) getObjectField(object,"lmf");
                if (PreferenceUtil.get_like_color().equals("")){
                    tv.setTextColor(Color.MAGENTA);
                }else {
                    tv.setTextColor(Color.parseColor(PreferenceUtil.get_like_color()));
                }

            }
        });
        /*String cls16="com.tencent.mm.plugin.exdevice.ui.b";
        findAndHookMethod(cls16,loadPackageParam.classLoader, "getView",int.class,View.class, ViewGroup.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!PreferenceUtil.like())
                    return;
                int i1= (int) param.args[0];
                int i2=PreferenceUtil.get_rank()+1;
                //XposedBridge.log("(i1)paramInt: "+i1);
                Object object=param.getResult();
                if (object instanceof RelativeLayout){
                    RelativeLayout grantparent= (RelativeLayout) object;//一级RelativeLayout
                    //XposedBridge.log("grantparent:"+grantparent.getChildCount());
                    *//*for (int i=0;i<3;i++){
                        Class<? extends View> s=grantparent.getChildAt(i).getClass();
                        XposedBridge.log(s.toString());
                    }*//*
                    LinearLayout parent= (LinearLayout) grantparent.getChildAt(1);//二级LinearLayout
                    if (parent != null){
                        RelativeLayout son= (RelativeLayout) parent.getChildAt(1);//三级RelativeLayout
                        //RelativeLayout rank= (RelativeLayout) son.getChildAt(0);//四级RelativeLayout[0]
                        RelativeLayout target= (RelativeLayout) son.getChildAt(1);//四级RelativeLayout[1] //目标布局
                        if (i1==0 | i1 == i2 ){
                            //第0项,自己,不赞   第(名次+1)项,自己,不赞
                            //Log.i("ja","Self, Don't Like. "+i1);
                        }else {
                            //Log.i("ja","i2:"+i2+" i1:"+i1);
                            target.performClick();
                        }
                    }
                }
            }
        });*/

        /*String cls17="com.tencent.mm.plugin.exdevice.ui.e.a";
        findAndHookMethod(cls17, loadPackageParam.classLoader, "aoG", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.getResult();
                //Log.i("ja","aoG: "+object.toString());
                String s=object.toString();
                if (!s.contains("view type: 0")){
                    String tmp=s.substring(s.lastIndexOf("{"),s.indexOf("}")+1);
                    Log.i("ja","tmp:"+tmp);
                }
            }
        });*/

        String cls18="com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI";
        findAndHookMethod(cls18, loadPackageParam.classLoader, "Kg", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferenceUtil.is_auto_login()){
                    Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                    if (button.getText().toString().equals("登录")){
                        button.performClick();
                    }
                }
            }
        });


        oneClick(classLoader);
    }

    private void oneClick(ClassLoader c){
        String s="com.tencent.mm.plugin.exdevice.ui.ExdeviceRankInfoUI";
        Log.i("ja","start_one");
        findAndHookMethod(s, c, "Kg", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object o=param.thisObject;
                ListView listview= (ListView) getObjectField(o,"lof");
                //Log.i("ja","start");
                final HeaderViewListAdapter adapter= (HeaderViewListAdapter) listview.getAdapter();
                //Log.i("ja","adapter count:"+adapter.getCount());
                String cls19="com.tencent.mm.ui.widget.e";
                findAndHookConstructor(cls19, classLoader, Context.class, int.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (!PreferenceUtil.like())
                            return;
                        Object object=param.thisObject;
                        View v= (View) getObjectField(object,"kRv");
                        if (v instanceof LinearLayout){
                            LinearLayout l= (LinearLayout) v;
                            Context context=v.getContext();
                            //Log.i("ja","l.child_count:"+l.getChildCount());
                            LinearLayout content=new LinearLayout(context);
                            content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            TextView tv=new TextView(context);
                            tv.setText("微信运动一键点赞[免费_by_Javaer]");
                            tv.setTextSize(20);
                            tv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.i("ja","Like Click");
                                    for (int i=2;i<adapter.getCount();i++){
                                        View v=adapter.getView(i,null,null);
                                        if (v instanceof RelativeLayout){
                                            RelativeLayout first_layout= (RelativeLayout) v;
                                            LinearLayout second_layout= (LinearLayout) first_layout.getChildAt(1);
                                            if (second_layout != null){
                                                RelativeLayout third_layout= (RelativeLayout) second_layout.getChildAt(1);
                                                RelativeLayout forth_layout= (RelativeLayout) third_layout.getChildAt(1);
                                                forth_layout.performClick();
                                            }
                                        }
                                        Log.i("ja","loop:"+i);
                                    }
                                    //Log.i("ja","stop");
                                    //Log.i("ja","Clicked");
                                }
                            });
                            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tv.setPadding(80,10,0,10);
                            content.addView(tv);
                            l.addView(content,0);
                            //Log.i("ja","success");
                        }
                    }
                });

                /*for (int i=0;i<adapter.getCount();i++){
                    View v=adapter.getView(i,null,null);
                    if (v instanceof RelativeLayout){
                        RelativeLayout first_layout= (RelativeLayout) v;
                        LinearLayout second_layout= (LinearLayout) first_layout.getChildAt(1);
                        if (second_layout != null){
                            RelativeLayout third_layout= (RelativeLayout) second_layout.getChildAt(1);
                            RelativeLayout forth_layout= (RelativeLayout) third_layout.getChildAt(1);
                            forth_layout.performClick();
                        }
                    }
                }
                Log.i("ja","stop");*/
            }
        });
    }

    private void xposedLogAllFields(Object object,String... strings){
        if (strings.length==1)
            XposedBridge.log((String) XposedHelpers.getObjectField(object,strings[0]));
        for (int i=0;i<strings.length;i++){
            XposedBridge.log((String) XposedHelpers.getObjectField(object,strings[i]));
        }
    }

    Activity mActivity=null;
    private void startWebViewUI(ClassLoader classLoader,String url) throws ClassNotFoundException {//打开微信网页
        if (mActivity != null){
            Class<?> WebViewUIclass=classLoader.loadClass("com.tencent.mm.plugin.webview.ui.tools.WebViewUI");
            XposedBridge.log("微信WebViewClass: "+WebViewUIclass.toString());
            Intent intent=new Intent(mActivity,WebViewUIclass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("rawUrl",url);
            mActivity.startActivity(intent);
        }else {
            hookMainActivity(classLoader);
        }
    }

    private void hookMainActivity(ClassLoader classLoader){
        String cls="com.tencent.mm.ui.LauncherUI";
        XposedHelpers.findAndHookMethod(cls, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object object=param.thisObject;
                mActivity= (Activity) object;
            }
        });
    }

    private boolean isInstance(String content, List<String> list){//判断是否属于某个集合
        if (list.size()==0)
            return false;
        for (int i=0;i<list.size();i++){
            if (content.equals(list.get(i)))
                return true;
        }
        return false;
    }
}
