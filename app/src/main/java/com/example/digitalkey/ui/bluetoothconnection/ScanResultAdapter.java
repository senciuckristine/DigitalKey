
package com.example.digitalkey.ui.bluetoothconnection;

import android.Manifest;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ScanResultAdapter extends BaseAdapter {

    private ArrayList<ScanResult> mArrayList;

    private Context mContext;

    private LayoutInflater mInflater;

    ScanResultAdapter(Context context, LayoutInflater inflater) {
        super();
        mContext = context;
        mInflater = inflater;
        mArrayList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mArrayList.get(position).getDevice().getAddress().hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Reuse an old view if we can, otherwise create a new one.
        if (view == null) {
            view = mInflater.inflate(com.example.digitalkey.R.layout.fragment_bluetoothconnection, null);
        }

        TextView deviceNameView = (TextView) view.findViewById(com.example.digitalkey.R.id.device_name);
        TextView deviceAddressView = (TextView) view.findViewById(com.example.digitalkey.R.id.device_address);
        TextView lastSeenView = (TextView) view.findViewById(com.example.digitalkey.R.id.last_seen);

        ScanResult scanResult = mArrayList.get(position);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String name = scanResult.getDevice().getName();
            if (name == null) {
                name = "(no_name)";
            }
            deviceNameView.setText(name);
            deviceAddressView.setText(scanResult.getDevice().getAddress());
            lastSeenView.setText(getTimeSinceString(mContext, scanResult.getTimestampNanos()));
        }


        return view;
    }

    /**
     * Search the adapter for an existing device address and return it, otherwise return -1.
     */
    private int getPosition(String address) {
        int position = -1;
        for (int i = 0; i < mArrayList.size(); i++) {
            if (mArrayList.get(i).getDevice().getAddress().equals(address)) {
                position = i;
                break;
            }
        }
        return position;
    }


    /**
     * Add a ScanResult item to the adapter if a result from that device isn't already present.
     * Otherwise updates the existing position with the new ScanResult.
     */
    public void add(ScanResult scanResult) {

        int existingPosition = getPosition(scanResult.getDevice().getAddress());

        if (existingPosition >= 0) {
            // Device is already in list, update its record.
            mArrayList.set(existingPosition, scanResult);
        } else {
            // Add new Device's ScanResult to list.
            mArrayList.add(scanResult);
        }
    }

    /**
     * Clear out the adapter.
     */
    public void clear() {
        mArrayList.clear();
    }

    /**
     * Takes in a number of nanoseconds and returns a human-readable string giving a vague
     * description of how long ago that was.
     */
    public static String getTimeSinceString(Context context, long timeNanoseconds) {
        String lastSeenText = "Last_seen: " + " ";

        long timeSince = SystemClock.elapsedRealtimeNanos() - timeNanoseconds;
        long secondsSince = TimeUnit.SECONDS.convert(timeSince, TimeUnit.NANOSECONDS);

        if (secondsSince < 5) {
            lastSeenText += "just_now";
        } else if (secondsSince < 60) {
            lastSeenText += secondsSince + " " + "seconds_ago";
        } else {
            long minutesSince = TimeUnit.MINUTES.convert(secondsSince, TimeUnit.SECONDS);
            if (minutesSince < 60) {
                if (minutesSince == 1) {
                    lastSeenText += minutesSince + " " + "minute_ago";
                } else {
                    lastSeenText += minutesSince + " " + "minutes_ago";
                }
            } else {
                long hoursSince = TimeUnit.HOURS.convert(minutesSince, TimeUnit.MINUTES);
                if (hoursSince == 1) {
                    lastSeenText += hoursSince + " " + "hour_ago";
                } else {
                    lastSeenText += hoursSince + " " + "hours_ago";
                }
            }
        }

        return lastSeenText;
    }
}
