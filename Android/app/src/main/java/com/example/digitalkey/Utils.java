package com.example.digitalkey;

public class Utils {
    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }
    public static String convertStringArrayToString(String[] strarr) {
        StringBuilder sb = new StringBuilder();
        for(String str: strarr) {
            sb.append(str);
        }
        return sb.substring(0,sb.length());
    }
}
