package com.javaer.vsetting.V;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;

public interface HookManage {
    public String createKey();

    public Set<XC_MethodHook.Unhook> createInfo();
}
