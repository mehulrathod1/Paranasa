package com.ni.parnasa.AsyncTaskUtils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 *
 * http://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically
 */
public class UploadImageAsyncTask extends AsyncTask<Void, Void, String> {

    private String url;
    private OnAsyncResult onAsyncResult;
    private Boolean resultFlag;
    File image_file;

    String charset = "UTF-8";
    ArrayList<String> arrparams, arrvalues;
    String image_param;

    public UploadImageAsyncTask(String url, OnAsyncResult listener, File image_file, ArrayList<String> params, ArrayList<String> values, String image_param) {
        this.url = url;
        this.onAsyncResult = listener;
        resultFlag = false;
        this.image_file = image_file;
        this.arrparams = params;
        this.arrvalues = values;
        this.image_param = image_param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            MultipartUtility multipart = new MultipartUtility(String.valueOf(url), charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

            for (int i = 0; i < arrparams.size(); i++) {
                multipart.addFormField(arrparams.get(i), arrvalues.get(i));
            }

            multipart.addFilePart(image_param, image_file);

            String response = multipart.finish();

            Log.e("Response :: ", response);
            resultFlag = true;
            return response;
        } catch (SocketTimeoutException e1) {
            resultFlag = false;
            return "Connection has timed out. Do you want to retry?";

        } catch (Exception e) {
            e.printStackTrace();
            resultFlag = false;
            return "Unexpected error has occurred";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (resultFlag) {
            if (onAsyncResult != null) {
                onAsyncResult.OnSuccess(result);
            }
        } else {
            if (onAsyncResult != null) {
                onAsyncResult.OnFailure(result);
            }
        }
    }
}