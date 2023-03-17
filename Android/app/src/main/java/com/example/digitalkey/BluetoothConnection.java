package com.example.digitalkey;

import static android.app.PendingIntent.getActivity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;


public class BluetoothConnection extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    TextView mStatusBluetooth, mBluetoothDevices;
    ImageView mBluetoothImage;
    Button mButtonOn, mButtonOff, mButtonListOfDevices;
    BluetoothAdapter mBleAdapter;

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
        mStatusBluetooth = findViewById(R.id.statusBluetooth);
        mBluetoothDevices = findViewById(R.id.ShowBLEDevices);
        mBluetoothImage = findViewById(R.id.bluetoothImg);
        mButtonOn = findViewById(R.id.ButtonOn);
        mButtonOff = findViewById(R.id.ButtonOff);
        mButtonListOfDevices = findViewById(R.id.ButtonDevices);

        mBleAdapter = BluetoothAdapter.getDefaultAdapter();

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

        mButtonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (!mBleAdapter.isEnabled()) {
                    showToast("Turning On Bluetooth...");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        startActivityIntent.launch(intent);
                        return;
                    }

                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });
        mButtonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
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
                        for(BluetoothDevice device: devices) {
                            mBluetoothDevices.setText(" ");
                        }
                        showToast("Turning off...");
                        mBluetoothImage.setImageResource(R.drawable.ic_action_off);

                    } else {
                        showToast("Bluetooth is already off");
                    }
                    return;
                }

            }
        });
        mButtonListOfDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (mBleAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Set<BluetoothDevice> devices = mBleAdapter.getBondedDevices();
                        mBluetoothDevices.setMovementMethod(new ScrollingMovementMethod());
                        for(BluetoothDevice device: devices){
                            mBluetoothDevices.append("\nDevice: "+ device.getName() + "," + device);
                        }
                        return;
                    }

                }else{
                    showToast("Turn on Bluetooth to show devices");
                }
            }
        });
    }
    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(REQUEST_ENABLE_BT == 0){
                        if(result != null){
                              mBluetoothImage.setImageResource(R.drawable.ic_action_on);
                              showToast("Bluetooth is On");
                        }else{
                            showToast("User denied to turn on");
                        }
                    }
                }
            });
        private void showToast (String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }

}
