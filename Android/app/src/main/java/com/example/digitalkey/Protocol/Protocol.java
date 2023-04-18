package com.example.digitalkey.Protocol;


import com.example.digitalkey.Utils;

public class Protocol {

    private byte[] message_GET_STATUS = Messages.createMessaje(Messages.messageType.GET_STATUS);
    private byte[] message_LOCK = Messages.createMessaje(Messages.messageType.LOCK);
    private byte[] message_UNLOCK = Messages.createMessaje(Messages.messageType.UNLOCK);

    // for read
    private static byte[] message_STATUS_LOCK = Messages.createMessaje(Messages.messageType.STATUS_LOCK);
    private static byte[] message_STATUS_UNLOCK = Messages.createMessaje(Messages.messageType.STATUS_UNLOCK);



    // function decode for receiving a message from arduino
    public static void decode(String data){
        // convert from String to byte in hex
        byte byte1 = Utils.hexToByte(data.substring(0,2));
        byte byte2 = Utils.hexToByte(data.substring(2,4));
        byte byte3 = Utils.hexToByte(data.substring(4,6));
        byte byte4 = Utils.hexToByte(data.substring(6,8));

        // compare the first 3 bytes as first action
        if(byte1 == message_STATUS_LOCK[0] && byte2 == message_STATUS_LOCK[1]
                && byte3 == message_STATUS_LOCK[2]){
            // if match then compare the last byte
            if(byte4 == message_STATUS_LOCK[3]){ //lock status
                // lock command received
                // schimb mesajul pe buton in comanda toggle
            }else if(byte4 == message_STATUS_UNLOCK[3]){    //unlock status
                // unlock command received
                // schimb mesajul pe buton in comanda toggle
            }
        }
        else{
            // else is not for us the message received
        }
    }
}
