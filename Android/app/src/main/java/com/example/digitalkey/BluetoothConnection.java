package com.example.digitalkey;

import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class BluetoothConnection extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    TextView mStatusBluetooth;
    ImageView mBluetoothImage;
    Button mButtonOnOff, mButtonListOfDevices;
    BluetoothAdapter mBleAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList arrayList;
    ListView Deviceslist;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://digitalkeylogin-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconnection);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bluetoothconnection);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bluetoothconnection:
                        return true;

                }
                return false;
            }
        });

        mBleAdapter = BluetoothAdapter.getDefaultAdapter();

        mStatusBluetooth = findViewById(R.id.statusBluetooth);
        mBluetoothImage = findViewById(R.id.bluetoothImg);
        mButtonOnOff = findViewById(R.id.ButtonOnOff);
        mButtonListOfDevices = findViewById(R.id.ButtonDevices);
        Deviceslist = findViewById(R.id.listView1);


        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        Deviceslist.setAdapter(arrayAdapter);

        Deviceslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (Deviceslist.getItemAtPosition(position));
                String lastword = selectedFromList.substring(selectedFromList.lastIndexOf(" ") + 1);
                Intent intent = new Intent(BluetoothConnection.this, ConnectionHC06.class);
                intent.putExtra("key", lastword);
                startActivity(intent);

            }
        });
        if (mBleAdapter == null) {
            mStatusBluetooth.setText(R.string.Bluetooth_is_not_available);
        } else {
            mStatusBluetooth.setText(R.string.bluetooth_is_available);
        }

        if (mBleAdapter.isEnabled()) {
            mButtonOnOff.setText(R.string.turn_off);
            mBluetoothImage.setImageResource(R.drawable.ic_action_on);
        } else {
            mButtonOnOff.setText(R.string.turn_on);
            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
        }

        mButtonOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String btnState = mButtonOnOff.getText().toString().toLowerCase();
                switch (btnState) {
                    case "turn on":
                        if (!mBleAdapter.isEnabled()) {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                showToast("Turning On Bluetooth...");
                                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityIntent1.launch(intent);

                            }
                        }
                    case "turn off":
                        if (mBleAdapter.isEnabled()) {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                mBleAdapter.disable();
                                arrayList.clear();
                                arrayAdapter.notifyDataSetChanged();
                                showToast("Turning off...");
                                mBluetoothImage.setImageResource(R.drawable.ic_action_off);
                                mButtonOnOff.setText("Turn On");
                            }
                        }
                }

            }
        });

    }

    public void discoverDevices(View V) {
        if (mBleAdapter.isEnabled()) {
            showToast("Scanning for your car");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                mBleAdapter.startDiscovery();
            }
        } else {
            showToast("You need to turn on Bluetooth");
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                if (device != null && device.getName() != null) {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            for (DataSnapshot ds : snapshot.child(uid).child("mac").getChildren()) {
                                String keyValue = ds.getKey();
                                if (device.getAddress().equals(keyValue)) {
                                    arrayList.add("Connect to your car: " + ds.getValue() + " " + keyValue);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }else{
                    Toast.makeText(getApplicationContext(), "Couldn't find nearby cars.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            }

        }
    };


    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    ActivityResultLauncher<Intent> startActivityIntent1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (REQUEST_ENABLE_BT == 0) {
                        if (result.getResultCode() == RESULT_OK) {
                            mBluetoothImage.setImageResource(R.drawable.ic_action_on);
                            showToast("Bluetooth is On");
                            mButtonOnOff.setText(R.string.turn_off);
                        } else {
                            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
                            showToast("User denied to turn on");
                            mButtonOnOff.setText(R.string.turn_on);
                        }
                    }
                }

            });

        private void showToast (String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }

}
