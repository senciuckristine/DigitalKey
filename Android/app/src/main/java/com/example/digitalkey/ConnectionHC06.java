package com.example.digitalkey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.digitalkey.Protocol.Message;
import com.example.digitalkey.Protocol.Protocol;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ConnectionHC06 extends AppCompatActivity {

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public boolean activar;
    Handler bluetoothIn;
    final int handlerState = 0;
    Button mButtonConnectHC06,mButtonLedOn,mButtonLedOff,mButtonDisconnected;
    private ConnectedThread MyConexionBT;
    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_hc06);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButtonConnectHC06 = findViewById(R.id.ConnectHC06);
        mButtonLedOn = findViewById(R.id.ledOn);
        mButtonLedOff = findViewById(R.id.ledOff);
        mButtonDisconnected = findViewById(R.id.Disconnect);
        mButtonLedOn.setVisibility(View.INVISIBLE);
        mButtonLedOff.setVisibility(View.INVISIBLE);
        mButtonDisconnected.setVisibility(View.INVISIBLE);


        btAdapter = BluetoothAdapter.getDefaultAdapter();

        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();

        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("HC-06")){

                address = pairedDevice.getAddress();
            }


        }

        String data="10010010";
        Protocol.decode(data);


        mButtonConnectHC06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activar = true;
                mButtonConnectHC06.setText("CONNECTED TO HC06");
                mButtonDisconnected.setText("DISCONECT");
                mButtonLedOn.setVisibility(View.VISIBLE);
                mButtonLedOff.setVisibility(View.VISIBLE);
                mButtonDisconnected.setVisibility(View.VISIBLE);
                onResume();
            }
        });

        mButtonLedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("1");

            }
        });
        mButtonLedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                byte[] message_GET_STATUS = Message.createMessaje(Message.messageType.GET_STATUS);

                String str = message_GET_STATUS.toString() ;
                
                MyConexionBT.write(message_GET_STATUS.toString());
                MyConexionBT.read();
            }
        });

        mButtonDisconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    btSocket.close();
                    mButtonConnectHC06.setText("CONNECT TO HC06");
                    mButtonLedOn.setVisibility(View.INVISIBLE);
                    mButtonLedOff.setVisibility(View.INVISIBLE);
                    mButtonDisconnected.setVisibility(View.INVISIBLE);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });





    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException{

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }




    @SuppressLint("MissingPermission")
    public void onResume() {
        super.onResume();


        if (activar) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                btSocket = createBluetoothSocket(device);

            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket connection failed", Toast.LENGTH_LONG).show();
            }
            // Establece la conexión con el socket Bluetooth.
            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {

                }
            }
            MyConexionBT = new ConnectedThread(btSocket);
            MyConexionBT.start();
        }

    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {

                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input) {
            try {

                mmOutStream.write(input.getBytes());

            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        public void read() {
            // delegate to Protocol class
            byte[] data = new byte[256];
            try {

                mmInStream.read(data);
                Protocol.decode(new String(data));


            } catch (IOException e) {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

}