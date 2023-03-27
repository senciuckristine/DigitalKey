package com.example.digitalkey.Protocol;


import java.util.stream.IntStream;

public class Protocol {

    private byte[] message_GET_STATUS = Message.createMessaje(Message.messageType.GET_STATUS);
    private byte[] message_LOCK = Message.createMessaje(Message.messageType.LOCK);
    private byte[] message_UNLOCK = Message.createMessaje(Message.messageType.UNLOCK);

    // for read
    private static byte[] message_STATUS_LOCK = Message.createMessaje(Message.messageType.STATUS_LOCK);
    private static byte[] message_STATUS_UNLOCK = Message.createMessaje(Message.messageType.STATUS_UNLOCK);


    // function decode for receiving a message from arduino
    public static void decode(String data){
        // convert from String to byte[]
        byte[] byteArray = data.getBytes();

        // compare the first 3 bytes as first action
        if(byteArray[0] == message_STATUS_LOCK[0] && byteArray[1] == message_STATUS_LOCK[1]
                && byteArray[2] == message_STATUS_LOCK[2]){
                // if match then compare the last byte
                 if(byteArray[3] == message_STATUS_LOCK[3]){ //lock status
                    // lock command received
                     // schimb mesajul pe buton in comanda toggle
                 }else if(byteArray[3] == message_STATUS_UNLOCK[3]){    //unlock status
                    // unlock command received
                     // schimb mesajul pe buton in comanda toggle
                 }
        }
        else{
            // else is not for us the message received
        }
    }
}
