package com.ni.parnasa.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CustomeFingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    private OnCustomeFingerprintListener listener;

    public interface OnCustomeFingerprintListener {
        void onAuthenticationError(String s);

        void onAuthenticationFailed(String s);

        void onAuthenticationHelp(String s);

        void onAuthenticationSucceeded(String s);
    }

    public CustomeFingerprintHandler(Context mContext, OnCustomeFingerprintListener listener) {
        context = mContext;
        this.listener = listener;
    }

//Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//
        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
        MyLogs.e("TAG", "onAuthenticationError : " + errString);
        listener.onAuthenticationError(errString.toString());
    }

    @Override
    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        MyLogs.e("TAG", "onAuthenticationFailed");
        listener.onAuthenticationFailed("Authentication failed");
    }

    @Override
    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
        MyLogs.e("TAG", "onAuthenticationHelp : " + helpString);
        listener.onAuthenticationHelp(helpString.toString());
    }

    @Override
    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "Authentication success!", Toast.LENGTH_LONG).show();
        MyLogs.e("TAG", "onAuthenticationSucceeded : " + result.getCryptoObject().getCipher().getAlgorithm());
        listener.onAuthenticationSucceeded("Authentication success!");
    }
}
