package com.wuyu.android.client.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class HomeFragmentManager {

    private BaseFragment[] fragments;

    public HomeFragmentManager() {
        fragments = new BaseFragment[2];
    }

    public Fragment newInstance(int pageId) {
        switch (pageId) {
        case 0:
            if (fragments[pageId] == null) {
                fragments[pageId] = new HomeNavigatFragment();
            }
            break;

        case 1:
            if (fragments[pageId] == null) {
                fragments[pageId] = new HomeCommonFragment();
            }
            break;
            
        }
        return fragments[pageId];
    }

    public void destroy(FragmentManager fm) {
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i] != null) {
                fm.beginTransaction().remove(fragments[i]);
                // fm.beginTransaction().commit();
                fm.beginTransaction().commitAllowingStateLoss();// 如果在Activity保存玩状态后再给它添加Fragment就会出错。解决办法就是把commit（）方法替换成
                                                                // commitAllowingStateLoss()就行

                fragments[i] = null;
            }
        }
        fragments = null;
    }
}
