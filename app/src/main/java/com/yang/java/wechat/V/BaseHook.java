package com.javaer.vsetting.V;

import com.javaer.vsetting.Log.Vlog;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public abstract class BaseHook {

    private Set<XC_MethodHook.Unhook> unhookSet = new HashSet<>();

    public abstract void dealHook(ClassLoader classLoader);

    public abstract void hookSet(Set<XC_MethodHook.Unhook> unhooks);

    public void hook(ClassLoader classLoader){
        dealHook(classLoader);
    }

    public void doHook(String className, ClassLoader classLoader, String methodName, Object ... paramsAndCallback){
        try {
            XC_MethodHook.Unhook unhook = XposedHelpers.findAndHookMethod(className, classLoader, methodName, paramsAndCallback);
            unhookSet.add(unhook);

            hookSet(unhookSet);
        }catch (Throwable throwable){
            Vlog.log(throwable);
        }
    }
}
