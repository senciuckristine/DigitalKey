package com.example.admindigitalkey;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

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
        holder.mac.setText(post.getMac());
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



                updatemac.setText(post.getMac());


                Button Update = holderView.findViewById(R.id.updateBtn);

                Update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("mac",updatemac.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(getRef(i).getKey())
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogPlus.dismiss();
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
            mac = itemView.findViewById(R.id.macform);
            password = itemView.findViewById(R.id.passwordform);
            emailaddress = itemView.findViewById(R.id.emailaddress);
            macaddress = itemView.findViewById(R.id.macaddress);
            passwordaddress = itemView.findViewById(R.id.passwordaddress);
            Edit = itemView.findViewById(R.id.edituser);
        }
    }
}
