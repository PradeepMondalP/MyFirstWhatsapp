package com.example.myfirstwhatsapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

  private View groupFragmentView;
  private ListView listView;
  private ArrayAdapter<String> arrayAdapter;
  private ArrayList<String> list_of_groups = new ArrayList<>();
  private DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView= inflater.inflate(R.layout.fragment_groups, container, false);

        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        initialize_fields();

        retrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cuurentGroupName =parent.getItemAtPosition(position).toString();

                Intent obj = new Intent(getContext() ,GroupChatActivity.class);
                obj.putExtra("groupName" , cuurentGroupName);
                startActivity(obj);
            }
        });

        return groupFragmentView;
    }


    private void initialize_fields() {

        listView = (ListView)groupFragmentView.findViewById(R.id.id_list_view_group_fragment);
        arrayAdapter = new ArrayAdapter<String>(getContext() , android.R.layout.simple_list_item_1 , list_of_groups);
        listView.setAdapter(arrayAdapter);
    }

    private void retrieveAndDisplayGroups() {

        groupRef.keepSynced(true);

     groupRef.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             Set<String> set = new HashSet<>();
             Iterator iterator = dataSnapshot.getChildren().iterator();

             while (iterator.hasNext())
             {
                 set.add( ( (DataSnapshot)iterator.next() ) .getKey() );
             }
             list_of_groups.clear();
             list_of_groups.addAll(set);
             arrayAdapter.notifyDataSetChanged();
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
             Toast.makeText(getContext(),
                     "could load groups", Toast.LENGTH_SHORT).show();

         }
     });
    }

}
