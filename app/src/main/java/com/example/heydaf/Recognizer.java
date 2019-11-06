package com.example.heydaf;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Segment;

public class Recognizer implements RecognitionListener {

    /* We only need the keyphrase to start recognition, one menu with list of choices,
       and one word that is required for method switchSearch - it will bring recognizer
       back to listening for the keyphrase*/
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    /* Keyword we are looking for to activate recognition */
    //private static final String KEYPHRASE = "hey duff";
    private static final String KEYPHRASE = "hey duff";
    private String tag = "Baran";
    private List<Functionality> functionalities;
    private BluetoothConnector bluetoothConnector;
    /* Recognition object */
    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;
    private Activity activity;

    public boolean enableS2T = true;
    public boolean heyDafRecognized = false;
    public boolean inspeech = false;

    public Recognizer(Activity activity, List<Functionality> functionalities, BluetoothConnector bluetoothConnector) {
        this.activity = activity;
        this.functionalities = functionalities;
        this.bluetoothConnector = bluetoothConnector ;
    }

    public void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(activity);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.d(tag, result.getMessage());
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                // Disable this line if you don't want recognizer to save raw
                // audio files to app's storage
                //.setRawLogDir(assetsDir)
                .getRecognizer();
        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create your custom grammar-based search
        File menuGrammar = new File(assetsDir, "mymenu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
    }

    public void onStop() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();

        Decoder dec = recognizer.getDecoder();
        dec.seg();

        if (inspeech) {
            for (Segment s : dec.seg()) {
                Log.d(tag, "SEG WORD: " + s.getWord() + " ASCORE= " + s.getAscore() + " PROB= " + s.getProb());
                if (s.getWord().equals(KEYPHRASE) && s.getProb() > -1850) {
                    Log.d(tag, "triggered");
                    switchSearch(MENU_SEARCH);
                    return;
                }
            }
        }

        /*
        if (text.equals(KEYPHRASE)) {
            switchSearch(MENU_SEARCH);
        }
        */
        /*
        else if (text.equals("turn on the lights")) {
            Log.d(tag, "lights on");
        } else if (text.equals("turn off the lights")){
            Log.d(tag, "lights off");
        }
        else {
            Log.d(tag, "partial= " + text);
        }*/
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null && enableS2T) {
            String text = hypothesis.getHypstr();

            Log.d(tag, "FINAL= " + text.toUpperCase());

            StringBuilder result = new StringBuilder();
            boolean changes = false;
            for (Functionality functionality : this.functionalities) {
                if (functionality.getActivation().equals(text)) {
                    functionality.activate();
                    Log.d(tag, functionality.getName() + " " + functionality.getStatus());
                    changes = true;
                } else if (functionality.getDeactivation().equals(text)) {
                    functionality.deactivate();
                    Log.d(tag, functionality.getName() + " " + functionality.getStatus());
                    changes = true;
                }
                result.append("f").append(functionality.getFunctionNumber()).append("=").append(functionality.getStatus()).append(",");
            }

            if (changes) {
                heyDafRecognized = false;
                TextView listening = activity.findViewById(R.id.textViewListening);
                listening.setVisibility(View.INVISIBLE);
                Log.d(tag, "Hey Daf not recognized");

            }

            if (changes && bluetoothConnector.BluetoothConnected) {
                this.bluetoothConnector.sendString(result.toString());
            }
        }
    }

    @Override
    public void onBeginningOfSpeech() {

        Log.d(tag, "Speech began");
        inspeech = true;
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(tag, "Speech ended");
        inspeech = false;
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else {
            Log.d(tag, "Hey daf recognized");
            heyDafRecognized = true;
            TextView listening = activity.findViewById(R.id.textViewListening);
            listening.setVisibility(View.VISIBLE);
            recognizer.startListening(searchName, 10000);
        }
    }

    @Override
    public void onError(Exception error) {
        Log.d(tag, error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }
}
