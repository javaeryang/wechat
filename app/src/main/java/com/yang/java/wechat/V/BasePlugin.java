package com.javaer.vsetting.V;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class BasePlugin {

    private Map<String, Set<XC_MethodHook.Unhook>> maps = new HashMap<>();

    public abstract String createKey();

    public abstract Set<XC_MethodHook.Unhook> createInfo();

    public abstract void loadPlugin(XC_LoadPackage.LoadPackageParam loadPackageParam);

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam){
        loadPlugin(loadPackageParam);

        maps.put(createKey(), createInfo());
    }
}
