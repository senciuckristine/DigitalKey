package com.example.admindigitalkey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    FirebaseAuth  mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://digitalkeylogin-default-rtdb.europe-west1.firebasedatabase.app/");
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

        public static boolean isValid(CharSequence target) {
            if (target == null) {
               return false;
             } else {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            }
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        EditText emailEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);
        EditText macEditText = findViewById(R.id.mac);
        EditText carModelEditText = findViewById(R.id.carmodel);
        Button registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String mac = macEditText.getText().toString();
                final String carmodel = carModelEditText.getText().toString();
                if(email.isEmpty() || mac.isEmpty() || password.isEmpty() || carmodel.isEmpty()){
                    Toast.makeText(Register.this, "Please complete every filed.",
                            Toast.LENGTH_SHORT).show();
                }else if (password.length() < 6){
                    Toast.makeText(Register.this, "Password needs to be at least 6 characters.",
                            Toast.LENGTH_SHORT).show();
                }else if(!isValid(email)){
                    Toast.makeText(Register.this, "Email is not a valid format.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Account Created.",
                                                Toast.LENGTH_SHORT).show();
                                        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                                databaseReference.child("users").child(uid).child("mac").child(mac).setValue(carmodel);
                                                databaseReference.child("users").child(uid).child("email").setValue(email);
                                                databaseReference.child("users").child(uid).child("password").setValue(password);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }



            }
        });
    }
}