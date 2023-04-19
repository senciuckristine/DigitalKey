package com.example.digitalkey;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalkey.Protocol.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class ConnectionHC06 extends AppCompatActivity {

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    public static String address = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public boolean activar;
    Handler bluetoothIn;
    final int handlerState = 0;
    Button mButtonConnectHC06,mButtonLedControl,mButtonDisconnected;
    private ConnectedThread MyConexionBT;
    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_hc06);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButtonConnectHC06 = findViewById(R.id.ConnectHC06);
        mButtonLedControl = findViewById(R.id.ledControl);
        mButtonDisconnected = findViewById(R.id.Disconnect);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);
        mButtonLedControl.setVisibility(View.INVISIBLE);
        mButtonDisconnected.setVisibility(View.INVISIBLE);


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();

        for(BluetoothDevice pairedDevice : pairedDeveicesList){
            if(pairedDevice.getName().equals("HC-06")){

                address = pairedDevice.getAddress();
            }


        }

        //String data="10010010";
        //Protocol.decode(data);


        bluetoothIn = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg){
                switch (msg.what) {


                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        switch (arduinoMsg.toLowerCase()) {
                            case "car is locked":
                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                mButtonLedControl.setText("Unlock the car");
                                break;
                            case "car is unlocked":
                                textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                mButtonLedControl.setText("Lock the car");
                                break;
                        }
                        break;
                }
            }
        };
        mButtonConnectHC06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activar = true;
                mButtonConnectHC06.setText("CONNECTED TO HC06");
                mButtonDisconnected.setText("DISCONECT");
                mButtonLedControl.setVisibility(View.VISIBLE);
                mButtonDisconnected.setVisibility(View.VISIBLE);
                textViewInfo.setVisibility(View.VISIBLE);
                onResume();
                byte[] message_GET_STATUS = Messages.createMessaje(Messages.messageType.GET_STATUS);


                String vv5 = Arrays.toString(message_GET_STATUS);
                String vv4= vv5.replaceAll(" ","");
                String[] vv1 =  vv4.substring(1, vv4.length() - 1).split(",");

                for(int i=0;i<vv1.length;i++) {
                    if(vv1[i].equals("1")) {
                        vv1[i]="01";
                    }
                    if(vv1[i].equals("0")) {
                        vv1[i]="00";
                    }
                    if(vv1[i].equals("16")) {
                        vv1[i]="10";
                    }
                    if(vv1[i].equals("17")) {
                        vv1[i]="11";
                    }
                }
                String vv2 = Utils.convertStringArrayToString(vv1);
                MyConexionBT.write(vv2);

            }
        });

        mButtonLedControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnState = mButtonLedControl.getText().toString().toLowerCase();
                switch (btnState) {
                    case "lock the car":
                        byte[] message_LOCK = Messages.createMessaje(Messages.messageType.LOCK);
                        String v5 = Arrays.toString(message_LOCK);
                        String v4 = v5.replaceAll(" ", "");
                        String[] v1 = v4.substring(1, v4.length() - 1).split(",");

                        for (int i = 0; i < v1.length; i++) {
                            if (v1[i].equals("1")) {
                                v1[i] = "01";
                            }
                            if (v1[i].equals("0")) {
                                v1[i] = "00";
                            }
                            if (v1[i].equals("16")) {
                                v1[i] = "10";
                            }
                            if (v1[i].equals("17")) {
                                v1[i] = "11";
                            }
                        }
                        String v2 = Utils.convertStringArrayToString(v1);
                        MyConexionBT.write(v2);
                        break;
                    case "unlock the car":
                        byte[] message_UNLOCK = Messages.createMessaje(Messages.messageType.UNLOCK);

                        String v7 = Arrays.toString(message_UNLOCK);
                        String v8 = v7.replaceAll(" ", "");
                        String[] v9 = v8.substring(1, v8.length() - 1).split(",");

                        for (int i = 0; i < v9.length; i++) {
                            if (v9[i].equals("1")) {
                                v9[i] = "01";
                            }
                            if (v9[i].equals("0")) {
                                v9[i] = "00";
                            }
                            if (v9[i].equals("16")) {
                                v9[i] = "10";
                            }
                            if (v9[i].equals("17")) {
                                v9[i] = "11";
                            }
                        }
                        String v10 = Utils.convertStringArrayToString(v9);
                        MyConexionBT.write(v10);
                        break;
                }
                byte[] message_GET_STATUS = Messages.createMessaje(Messages.messageType.GET_STATUS);


                String vv5 = Arrays.toString(message_GET_STATUS);
                String vv4= vv5.replaceAll(" ","");
                String[] vv1 =  vv4.substring(1, vv4.length() - 1).split(",");

                for(int i=0;i<vv1.length;i++) {
                    if(vv1[i].equals("1")) {
                        vv1[i]="01";
                    }
                    if(vv1[i].equals("0")) {
                        vv1[i]="00";
                    }
                    if(vv1[i].equals("16")) {
                        vv1[i]="10";
                    }
                    if(vv1[i].equals("17")) {
                        vv1[i]="11";
                    }
                }
                String vv2 = Utils.convertStringArrayToString(vv1);
                MyConexionBT.write(vv2);
            }

                //MyConexionBT.write("01101000");


        });


        mButtonDisconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    btSocket.close();
                    mButtonConnectHC06.setText("CONNECT TO HC06");
                    mButtonLedControl.setVisibility(View.INVISIBLE);
                     mButtonDisconnected.setVisibility(View.INVISIBLE);
                    textViewInfo.setVisibility(View.INVISIBLE);
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
            int bytes = 0;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.e("Arduino Message",readMessage);
                        bluetoothIn.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
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

//        public void read() {
//            // delegate to Protocol class
//            byte[] data = new byte[256];
//            try {
//
//                mmInStream.read(data);
//                Protocol.decode(new String(data));
//
//
//            } catch (IOException e) {
//                //si no es posible enviar datos se cierra la conexión
//                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }


    }

}