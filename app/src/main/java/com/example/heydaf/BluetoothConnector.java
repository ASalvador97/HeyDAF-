package com.example.heydaf;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnector {

    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private final String btDeviceName = "raspberrypi";
    private final String tag = "Baran";
    private OutputStream outputStream;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket socket;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public boolean BluetoothConnected = false;

    BluetoothConnector(Activity activity) {
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        turn_bluetooth_on();
        connect();
    }

    public void connect() {
        bluetoothDevice = list_paired_devices();
        try {
            if (bluetoothDevice == null) {
                return;
            }
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            Log.d(tag, "Socket created");
            Log.d(tag, "Connected to device: " + bluetoothDevice.getName());
            BluetoothConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    private void turn_bluetooth_on() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOn, 0);
            Log.d(tag, "Turning bluetooth on");
        } else {
            Log.d(tag, "Bluetooth is alread on");
        }
    }

    private BluetoothDevice list_paired_devices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        Log.d(tag, "Paired devices:");
        for (BluetoothDevice bt : pairedDevices) {
            Log.d(tag, bt.getName());
            if (bt.getName().equals(btDeviceName)) {
                return bt;
            }
        }
        return null;
    }

    void sendString(String message) {
        if (bluetoothDevice != null) {
            ParcelUuid[] uuids = bluetoothDevice.getUuids();
            try {
                if (!socket.isConnected()) {
                    socket.connect();
                }
                outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                Log.d(tag, "Sent message: " + message);
                Thread.sleep(100);

            } catch (IOException e) {
                Log.d(tag, "Could not connect to device: " + e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
