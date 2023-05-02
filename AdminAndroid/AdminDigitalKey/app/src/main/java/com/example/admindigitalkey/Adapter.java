package com.example.admindigitalkey;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter extends FirebaseRecyclerAdapter<Post,Adapter.PostViewHolder>{

    private MainActivity context;

    public Adapter(@NonNull FirebaseRecyclerOptions<Post> options,MainActivity context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int i, @NonNull Post post) {
        getRef(i).getKey();
        holder.email.setText(post.getEmail());
       // holder.mac.setText(post.getMac());
        holder.password.setText(post.getPassword());

        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogPlus dialogPlus = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50,0,50,0)
                        .setContentHolder(new ViewHolder(R.layout.edit))
                        .setExpanded(false)
                        .create();

                View holderView = (LinearLayout) dialogPlus.getHolderView();

                EditText updatemac = holderView.findViewById(R.id.macupdate);
                TextView textView = holderView.findViewById(R.id.listofmacaddresses);
                ListView Deviceslist = holderView.findViewById(R.id.listView1);
                ArrayAdapter<String> arrayAdapter;
                ArrayList arrayList ;
                arrayList = new ArrayList<>();
                arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,arrayList);
                Deviceslist.setAdapter(arrayAdapter);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://digitalkeylogin-default-rtdb.europe-west1.firebasedatabase.app/");

                // updatemac.setText(post.getMac());
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int index = 0;
                        for(DataSnapshot ds : snapshot.child(getRef(i).getKey()).child("mac").getChildren()) {
                            index++;
                            arrayList.add("Mac Address "+ index + ": " + ds.getValue(String.class));
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Button Update = holderView.findViewById(R.id.updateBtn);

                Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int index = 0;
                                for(DataSnapshot ds : snapshot.child(getRef(i).getKey()).child("mac").getChildren()) {
                                    index++;
                                    arrayList.add("Mac Address "+ index + ": " + ds.getValue(String.class));
                                    arrayAdapter.notifyDataSetChanged();
                                }
                                index++;
                                databaseReference.child("users").child(getRef(i).getKey()).child("mac").child(String.valueOf(index)).setValue(updatemac.getText().toString());
                                dialogPlus.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
                dialogPlus.show();
            }
        });

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_form,parent,false);
        return new Adapter.PostViewHolder(view);


    }

    public class PostViewHolder  extends RecyclerView.ViewHolder {

        TextView email, mac,password,emailaddress,macaddress,passwordaddress;
        ImageView Edit;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.emailform);
            password = itemView.findViewById(R.id.passwordform);
            emailaddress = itemView.findViewById(R.id.emailaddress);
            passwordaddress = itemView.findViewById(R.id.passwordaddress);
            Edit = itemView.findViewById(R.id.edituser);
        }
    }
}
