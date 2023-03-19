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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothConnection extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    TextView mStatusBluetooth, mBluetoothDevices;
    ImageView mBluetoothImage;
    Button mButtonOn, mButtonOff, mButtonDiscover, mButtonListOfDevices;
    BluetoothAdapter mBleAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList arrayList ;
    ListView Deviceslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconnection);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bluetoothconnection);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(), DashBoard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.bluetoothconnection:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusBluetooth = findViewById(R.id.statusBluetooth);
        mBluetoothDevices = findViewById(R.id.ShowBLEDevices);
        mBluetoothImage = findViewById(R.id.bluetoothImg);
        mButtonOn = findViewById(R.id.ButtonOn);
        mButtonOff = findViewById(R.id.ButtonOff);
        mButtonDiscover = findViewById(R.id.ButtonDiscoverable);
        mButtonListOfDevices = findViewById(R.id.ButtonDevices);
        Deviceslist = findViewById(R.id.listView1);
        
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        Deviceslist.setAdapter(arrayAdapter);


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
                            Set<BluetoothDevice> devices = mBleAdapter.getBondedDevices();
                            for (BluetoothDevice device : devices) {
                                mBluetoothDevices.setText(" ");
                            }
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
            showToast("Scanning Devices");
            mBleAdapter.startDiscovery();
        }else{
            showToast("You need to turn on Bluetooth");
        }
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
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
