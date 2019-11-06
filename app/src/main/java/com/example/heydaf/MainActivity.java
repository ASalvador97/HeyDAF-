package com.example.heydaf;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {


    private ImageView bluetoothImageView;
    private TextView bluetoothTextView;
    private Button retryButton;

    private List<Functionality> functionalities = new ArrayList<>();
    private Recognizer speechRecognizer;
    public BluetoothConnector bluetoothConnector;
    private RecyclerView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Hey !DAF");

        this.listview = findViewById(R.id.switchListView);

        initFunctionalities();

        ListAdapter listAdapter = new ListAdapter(functionalities, this);
        listview.setAdapter(listAdapter);

        bluetoothConnector = new BluetoothConnector(this);
        speechRecognizer = new Recognizer(this, this.functionalities, bluetoothConnector);
        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, 1);
            return;
        }
        speechRecognizer.runRecognizerSetup();

        bluetoothImageView = findViewById(R.id.Bluetooth);
        bluetoothTextView = findViewById(R.id.bluetoothText);
        retryButton = findViewById(R.id.buttonRetry);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnector.connect();
            }
        });

        updateBluetoothImage();
    }

    private void updateBluetoothImage() {
        if (!bluetoothConnector.getBluetoothAdapter().isEnabled()) {
            bluetoothImageView.setImageResource(R.drawable.bluetoothoff);
            bluetoothTextView.setText("Bluetooth is off");
        } else {
            bluetoothImageView.setImageResource(R.drawable.bluetoothon);
            bluetoothTextView.setText("Bluetooth is on");
        }
    }

    private void initFunctionalities() {
        functionalities.add(new Functionality(1, "Lights", "turn on the lights",
                "turn off the lights", (ImageView) findViewById(R.id.truckArt2)));
        functionalities.add(new Functionality(2, "Left Indicator", "blink left indicator",
                "stop blinking left indicator", (ImageView) findViewById(R.id.dafindicatorleft)));
        functionalities.add(new Functionality(3, "Right Indicator", "blink right indicator",
                "stop blinking right indicator", (ImageView) findViewById(R.id.dafindicatorright)));
        functionalities.add(new Functionality(4, "Disco mode", "start disco mode",
                "stop disco mode"));
        functionalities.add(new Functionality(5, "Honk", "where are you",
                "stop honking", (ImageView) findViewById(R.id.dafhonk)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                speechRecognizer.runRecognizerSetup();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        speechRecognizer.onStop();
    }

    void sendMessage() {
        StringBuilder result = new StringBuilder();
        for (Functionality functionality : this.functionalities) {
            result.append("f").append(functionality.getFunctionNumber()).append("=").append(functionality.getStatus()).append(",");
        }
        this.bluetoothConnector.sendString(result.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            updateBluetoothImage();
        }
    }

}
