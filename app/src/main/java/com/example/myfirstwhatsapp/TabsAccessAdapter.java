package com.example.myfirstwhatsapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessAdapter extends FragmentPagerAdapter {

    public TabsAccessAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i)
        {

            case 0:
                ContactsFragment contactsFragment  = new ContactsFragment();
                return  contactsFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return  groupsFragment;


            case 2:
                RequestsFragment requestsFragment  = new RequestsFragment();
                return  requestsFragment;

              default: return  null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
//            case 0:
//                return "chats";

            case 0: return "chats";

            case 1:
                return "groups";

            case 2: return "Requests" ;


            default: return  null;
        }
    }
}
