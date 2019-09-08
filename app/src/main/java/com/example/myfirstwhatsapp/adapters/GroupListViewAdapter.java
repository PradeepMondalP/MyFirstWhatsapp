package com.example.myfirstwhatsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myfirstwhatsapp.R;

import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupListViewAdapter extends BaseAdapter {

    Context ctx;
    String[] groupName;
    LayoutInflater inflater;

    public GroupListViewAdapter(Context ctx, String[] groupName) {
        this.ctx = ctx;
        this.groupName = groupName;
    }

    @Override
    public int getCount() {
         return groupName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView gName;

        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.user_defined_all_group_displaying_layout , parent , false);

        gName = (TextView)view.findViewById(R.id.nameTV);

        gName.setText(groupName[position]);

        return view;
    }
}
