package com.nouspartageons.django.nouspartageonsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by django on 27/12/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                RequestFragment reqFrag = new RequestFragment();
                return reqFrag;
            case 1:
                ChatFragment chatFrag = new ChatFragment();
                return chatFrag;
            case 2:
                FriendsFragment friendsFrag = new FriendsFragment();
                return friendsFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        switch (pos) {
            case 0:
                return "INVITATIONS";
            case 1:
                return "MESSAGES";
            case 2:
                return "AMIS";
            default:
                return null;
        }
    }
}
