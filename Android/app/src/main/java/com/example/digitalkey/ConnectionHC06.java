package com.example.digitalkey;

import androidx.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalkey.Protocol.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public boolean active;
    Handler bluetoothIn;
     Button mButtonConnectHC06,mButtonLedControl,mButtonDisconnected;
    ImageView mCarImage;
    private ConnectedThread MyConnectionBT;

    @Override
    public void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_hc06);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCarImage = findViewById(R.id.carImg);
        mButtonConnectHC06 = findViewById(R.id.ConnectHC06);
        mButtonLedControl = findViewById(R.id.ledControl);
        mButtonDisconnected = findViewById(R.id.Disconnect);
        final TextView textViewInfo = findViewById(R.id.textViewInfo);
        mButtonLedControl.setVisibility(View.INVISIBLE);
        mButtonDisconnected.setVisibility(View.INVISIBLE);
        mCarImage.setImageResource(R.drawable.car);
        byte[] message_STATUS_UNLOCK = Messages.createMessaje(Messages.messageType.STATUS_UNLOCK);
        byte[] message_STATUS_LOCK = Messages.createMessaje(Messages.messageType.STATUS_LOCK);
        byte[] message_GET_STATUS = Messages.createMessaje(Messages.messageType.GET_STATUS);
        byte[] message_LOCK = Messages.createMessaje(Messages.messageType.LOCK);
        byte[] message_UNLOCK = Messages.createMessaje(Messages.messageType.UNLOCK);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        String lastword = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lastword = extras.getString("key");
            address = lastword;
        }


        bluetoothIn = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg){
                if (msg.what == MESSAGE_READ) {
                    String arduinoMsg = msg.obj.toString();
                    arduinoMsg = arduinoMsg.replace("\r", "").replace("\n", "");
                    int arduinoMsgInt = Integer.parseInt(arduinoMsg);
                    String arduinoMsgBinary = Integer.toBinaryString(arduinoMsgInt);

                    byte byte1 = Utils.hexToByte(arduinoMsgBinary.substring(0, 2));
                    byte byte2 = Utils.hexToByte(arduinoMsgBinary.substring(2, 4));
                    byte byte3 = Utils.hexToByte(arduinoMsgBinary.substring(4, 6));
                    byte byte4 = Utils.hexToByte(arduinoMsgBinary.substring(6, 8));

                    if (byte1 == message_STATUS_LOCK[0] && byte2 == message_STATUS_LOCK[1]
                            && byte3 == message_STATUS_LOCK[2]) {
                        if (byte4 == message_STATUS_LOCK[3]) {
                            textViewInfo.setText(R.string.ard_message_locked);
                            mButtonLedControl.setText(R.string.unlock_car);
                            mCarImage.setImageResource(R.drawable.carlock);
                        } else if (byte4 == message_STATUS_UNLOCK[3]) {
                            textViewInfo.setText(R.string.ard_message_unlock);
                            mButtonLedControl.setText(R.string.lock_car);
                            mCarImage.setImageResource(R.drawable.carunlock);
                        }
                    }
                }
            }
        };
        mButtonConnectHC06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active = true;
                mButtonConnectHC06.setVisibility(View.INVISIBLE);
                mButtonDisconnected.setText(R.string.disconnect);
                mButtonLedControl.setVisibility(View.VISIBLE);
                mButtonDisconnected.setVisibility(View.VISIBLE);
                mCarImage.setVisibility(View.VISIBLE);
                textViewInfo.setVisibility(View.VISIBLE);
                onResume();
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
                MyConnectionBT.write(vv2);

            }
        });

        mButtonLedControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnState = mButtonLedControl.getText().toString().toLowerCase();
                switch (btnState) {
                    case "lock the car":
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
                        MyConnectionBT.write(v2);
                        break;
                    case "unlock the car":
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
                        MyConnectionBT.write(v10);
                        break;
                }

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
                MyConnectionBT.write(vv2);
            }

        });


        mButtonDisconnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    btSocket.close();
                    mButtonConnectHC06.setText(R.string.connect_to_car);
                    mButtonConnectHC06.setVisibility(View.VISIBLE);
                    mButtonLedControl.setVisibility(View.INVISIBLE);
                     mButtonDisconnected.setVisibility(View.INVISIBLE);
                    mCarImage.setImageResource(R.drawable.car);
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
        if (active) {
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket connection failed", Toast.LENGTH_LONG).show();
            }
            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                }
            }
            MyConnectionBT = new ConnectedThread(btSocket);
            MyConnectionBT.start();
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

        public void write(String input) {
            try {

                mmOutStream.write(input.getBytes());

            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

}