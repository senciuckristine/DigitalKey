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
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9634FB");
    private BluetoothSocket btSocket = null;
    private String address = null;
    TextView mStatusBluetooth, mBluetoothDevices;
    ImageView mBluetoothImage;
    Button mButtonOn, mButtonOff, mButtonDiscover, mButtonListOfDevices,mButtonConnect;
    BluetoothAdapter mBleAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList arrayList ;
    ListView Deviceslist;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://digitalkeylogin-default-rtdb.europe-west1.firebasedatabase.app/");
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconnection);
        textView = findViewById(R.id.uid);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bluetoothconnection);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

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
        //mBluetoothDevices = findViewById(R.id.ShowBLEDevices);
        mBluetoothImage = findViewById(R.id.bluetoothImg);
        mButtonOn = findViewById(R.id.ButtonOn);
        mButtonOff = findViewById(R.id.ButtonOff);
        mButtonDiscover = findViewById(R.id.ButtonDiscoverable);
        mButtonListOfDevices = findViewById(R.id.ButtonDevices);
        Deviceslist = findViewById(R.id.listView1);
        mButtonConnect = findViewById(R.id.ButtonConnect);

        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        Deviceslist.setAdapter(arrayAdapter);

        Deviceslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (Deviceslist.getItemAtPosition(position));
                String lastword = selectedFromList.substring(selectedFromList.lastIndexOf(" ")+1);
                Intent intent = new Intent(BluetoothConnection.this, ConnectionHC06.class);
                intent.putExtra("key",lastword);
                    startActivity(intent);

            }
        });
        if (mBleAdapter == null) {
            mStatusBluetooth.setText("Bluetooth is not available");
        } else {
            mStatusBluetooth.setText("Bluetooth is available");
        }

        if (mBleAdapter.isEnabled()) {
            mBluetoothImage.setImageResource(R.drawable.ic_action_on);
        } else {
            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
        }
        mButtonDiscover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (!mBleAdapter.isDiscovering()) {
                        showToast("Making Your Device Discoverable");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityIntent2.launch(intent);
                    }
                }

            }
        });
        mButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (!mBleAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        showToast("Turning On Bluetooth...");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityIntent1.launch(intent);
                    }

                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });
        mButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (!mBleAdapter.isEnabled()) {
                    showToast("Bluetooth is already off");
                } else {
                    if (mBleAdapter.isEnabled()) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            mBleAdapter.disable();
                            arrayList.clear();
                            arrayAdapter.notifyDataSetChanged();
                            mButtonConnect.setVisibility(View.INVISIBLE);
                            showToast("Turning off...");
                            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
                        }
                    }
                }
            }
        });

//        mButtonListOfDevices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                if (mBleAdapter.isEnabled()) {
//                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        Set<BluetoothDevice> devices = mBleAdapter.getBondedDevices();
//                        mBluetoothDevices.setMovementMethod(new ScrollingMovementMethod());
//                        for(BluetoothDevice device: devices){
//                            if(devices.size() < 1) {
//                                showToast("No devices available");
//                                mBluetoothDevices.setText(" ");
//                            }else {
//                                mBluetoothDevices.append("\nDevice: " + device.getName() + "," + device);
//                            }
//                        }
//                    }
//                }else{
//                    showToast("Turn on Bluetooth to show devices");
//                }
//            }
//        });
//        mButtonListOfDevices.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View V) {
//                if (mBleAdapter.isEnabled()) {
//                    mBleAdapter.startDiscovery();
//                    Toast.makeText(getApplicationContext(), "Scanning Devices", Toast.LENGTH_LONG).show();
//
//
//                }
//            }
//        });
    }
    @SuppressLint("MissingPermission")
    public void discoverDevices(View V){
        if (mBleAdapter.isEnabled()) {
            showToast("Scanning for your car");
            mBleAdapter.startDiscovery();
        }else{
            showToast("You need to turn on Bluetooth");
        }
    }

    public void openNewActivity(){
        Intent intent = new Intent(this, ConnectionHC06.class);
        startActivity(intent);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device != null && device.getName() !=null) {
                     databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //    final String getMacAddress = snapshot.child(uid).child("mac").getValue(String.class);
                            for(DataSnapshot ds : snapshot.child(uid).child("mac").getChildren()) {
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
                        } else {
                            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
                            showToast("User denied to turn on");
                        }
                    }
                }

            });
    ActivityResultLauncher<Intent> startActivityIntent2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (REQUEST_DISCOVER_BT == 1) {
                        if (result.getResultCode() == RESULT_CANCELED) {
                            mBluetoothImage.setImageResource(R.drawable.ic_action_off);
                            showToast("Bluetooth is NOT Discoverable");
                        } else {
                            mBluetoothImage.setImageResource(R.drawable.ic_action_on);
                            showToast("Bluetooth is  Discoverable");
                        }
                    }
                }

            });
        private void showToast (String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }

}
