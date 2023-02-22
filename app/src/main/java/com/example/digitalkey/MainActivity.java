package com.example.digitalkey;

//import static com.example.digitalkey.R.id.error_textview;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.digitalkey.ui.bluetoothconnection.BluetoothConnectionFragment;
import com.example.digitalkey.ui.bluetoothconnection.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.digitalkey.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_bluetoothconnection)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if (savedInstanceState == null) {

            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE))
                    .getAdapter();

            // Is Bluetooth supported on this device?
            if (mBluetoothAdapter != null) {

                // Is Bluetooth turned on?
                if (mBluetoothAdapter.isEnabled()) {

                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                        // Everything is supported and enabled, load the fragments.
                        setupFragments();

                    } else {

                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported);


                    }
                } else {

                    // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
                }
            } else {

                // Bluetooth is not supported.
                showErrorText(com.example.digitalkey.R.string.bt_not_supported);
            }
        }
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case Constants.REQUEST_ENABLE_BT:

                    if (resultCode == RESULT_OK) {

                        // Bluetooth is now Enabled, are Bluetooth Advertisements supported on
                        // this device?
                        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

                            // Everything is supported and enabled, load the fragments.
                            setupFragments();

                        } else {

                            // Bluetooth Advertisements are not supported.
                            showErrorText(R.string.bt_ads_not_supported);
                        }
                    } else {

                        // User declined to enable Bluetooth, exit the app.
                        Toast.makeText(this, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }


        private void setupFragments() {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            BluetoothConnectionFragment scannerFragment = new BluetoothConnectionFragment();
            // Fragments can't access system services directly, so pass it the BluetoothAdapter
            scannerFragment.setBluetoothAdapter(mBluetoothAdapter);
            transaction.replace(com.example.digitalkey.R.id.navigation_bluetoothconnection, scannerFragment);

            transaction.commit();
        }

       private void showErrorText(int messageId) {

            TextView view = (TextView) findViewById(R.id.error_textview);
            view.setText(getString(messageId));
        }

    }

