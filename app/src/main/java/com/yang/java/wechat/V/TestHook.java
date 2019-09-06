package com.javaer.vsetting.V;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;

public class TestHook extends BaseHook implements HookManage{

    private Map<String, Set<XC_MethodHook.Unhook>> hooks = new HashMap<>();

    @Override
    public void dealHook(ClassLoader classLoader) {
        doHook("",
                classLoader,
                "",
                String.class,
                new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    @Override
    public void hookSet(Set<XC_MethodHook.Unhook> unhooks) {
        hooks.put(createKey(), unhooks);
    }

    @Override
    public String createKey() {
        return this.getClass().getName();
    }

    @Override
    public Set<XC_MethodHook.Unhook> createInfo() {
        return hooks.get(createKey());
    }
}
