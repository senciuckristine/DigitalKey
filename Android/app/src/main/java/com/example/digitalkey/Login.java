package com.example.digitalkey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class Login extends AppCompatActivity {
   private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailTxt = findViewById(R.id.email);
        final EditText passwordTxt = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.loginBtn);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(Login.this, BluetoothConnection.class);
            startActivity(intent);
        }
            loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailTxt.getText().toString();
                final String password = passwordTxt.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this,"Please enter your email or password",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                       Toast.makeText(Login.this, "Login Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),BluetoothConnection.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Login.this, "Login Failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }

            }
        });


    }

}