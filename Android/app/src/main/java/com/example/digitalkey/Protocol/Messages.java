package com.example.digitalkey.Protocol;

public class Messages {


    public enum messageType {
        GET_STATUS,
        LOCK,
        UNLOCK,
        STATUS_LOCK,
        STATUS_UNLOCK
    }

    public static byte[] createMessaje(messageType type){
        byte[] content = new byte[4];
        switch(type){
            case GET_STATUS:
                content[0] = 0x01 ;
                content[1] = 0x10 ;
                content[2] = 0x01 ;
                content[3] = 0x00 ;
                break;
            case LOCK:
                content[0] = 0x01 ;
                content[1] = 0x10 ;
                content[2] = 0x10 ;
                content[3] = 0x00 ;
                break;
            case UNLOCK:
                content[0] = 0x01 ;
                content[1] = 0x10 ;
                content[2] = 0x11 ;
                content[3] = 0x00 ;
                break;
            case STATUS_LOCK:
                content[0] = 0x10 ;
                content[1] = 0x01 ;
                content[2] = 0x00 ;
                content[3] = 0x10 ;
                break;
            case STATUS_UNLOCK:
                content[0] = 0x10 ;
                content[1] = 0x01 ;
                content[2] = 0x00 ;
                content[3] = 0x11 ;
                break;
        }
        return content;
    }
}
