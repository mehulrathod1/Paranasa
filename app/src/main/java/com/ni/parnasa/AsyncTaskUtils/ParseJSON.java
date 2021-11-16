package com.ni.parnasa.AsyncTaskUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.ImageView;

import com.ni.parnasa.utils.MyLogs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * http://www.jsonschema2pojo.org/
 */

public class ParseJSON {

    private Context mContext;
    private GetAsyncTask getAsyncTask;
    private GetAsyncJsonReq getAsyncJsonReq = null;
    private UploadImageAsyncTask imageuploadAsyncTask;
    private Uri.Builder builder;

    public String url, image_param;
    boolean isInternetAvailable;
    public Object model;
    public ArrayList<String> params;
    public ArrayList<String> values;

    private OnResultListner onResultListner;
    private OnResultListenerNew onResultListenerNew;
    private ConnectionCheck cd;

    private File image_file;
    //    private ProgressDialog progressDialog;
    private ImageView ivLoader;
    private boolean isRequiredDialog = true;
    private CustomProgressDialog customProgressDialog;

    public interface OnResultListner {
        void onResult(boolean status, Object obj);
    }

    public interface OnResultListenerNew {
        void onResult(boolean status, Object obj);

        void onFailed();
    }

    public ParseJSON(Context mContext,
                     String url,
                     ArrayList<String> params,
                     ArrayList<String> values,
                     Object model,
                     OnResultListner onResultListner,
                     String image_param,
                     File image_file) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.model = model;
        this.mContext = mContext;
        this.onResultListner = onResultListner;
        this.image_param = image_param;
        this.image_file = image_file;
        customProgressDialog = new CustomProgressDialog(mContext);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            UploadImageAndGetData();
        } else {
            new ConnectionCheck().showconnectiondialog(mContext).show();
        }
    }

    public ParseJSON(Context mContext, String url,
                     ArrayList<String> params,
                     ArrayList<String> values,
                     Object model,
                     OnResultListner onResultListner) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.model = model;
        this.mContext = mContext;
        this.onResultListner = onResultListner;
        this.image_file = null;
//        this.isRequiredDialog = isRequiredDialog;
        customProgressDialog = new CustomProgressDialog(mContext);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getData(false);
        } else {
            new ConnectionCheck().showconnectiondialog(mContext).show();
        }
    }

    public ParseJSON(Context mContext, String url,
                     ArrayList<String> params,
                     ArrayList<String> values,
                     Object model,
                     OnResultListenerNew onResultListenerNew, boolean isRequiredDialog) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.model = model;
        this.mContext = mContext;
        this.onResultListenerNew = onResultListenerNew;
        this.image_file = null;
        this.isRequiredDialog = isRequiredDialog;
        customProgressDialog = new CustomProgressDialog(mContext);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getData(true);
        } else {
            new ConnectionCheck().showconnectiondialog(mContext).show();
        }
    }

    public ParseJSON(Context mContext, String url,
                     ArrayList<String> params,
                     ArrayList<String> values,
                     Object model,
                     OnResultListner onResultListner, boolean isRequiredDialog) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.model = model;
        this.mContext = mContext;
        this.onResultListner = onResultListner;
        this.image_file = null;
        this.isRequiredDialog = isRequiredDialog;
        customProgressDialog = new CustomProgressDialog(mContext);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getData(false);
        } else {
            new ConnectionCheck().showconnectiondialog(mContext).show();
        }
    }


    public void getData(final boolean isFromNewListener) {
//        builder = new Uri.Builder();

        final JSONObject jsonObject = new JSONObject();

        for (int i = 0; i < params.size(); i++) {
//            builder.appendQueryParameter(params.get(i), values.get(i));
            try {
                jsonObject.put(params.get(i), values.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while loading");*/

        if (isRequiredDialog) {
//            progressDialog.show();
            customProgressDialog.show();
        }

        // Async Result
        OnAsyncResult onAsyncResult = new OnAsyncResult() {
            @Override
            public void OnSuccess(String result) {

                try {
                    if (isRequiredDialog) {
//                        progressDialog.dismiss();
                        customProgressDialog.dismiss();
                    }

                    JSONObject object = new JSONObject(result);
                    if (object.getString("status").equalsIgnoreCase("Yes")
//                    if (object.getString("status").equalsIgnoreCase("success")
                            || object.getString("status").equalsIgnoreCase("true")) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
                        Gson gson = gsonBuilder.create();

                        if (isFromNewListener && onResultListenerNew != null) {
                            onResultListenerNew.onResult(true, gson.fromJson(result, (Class) model));
                        } else {
                            onResultListner.onResult(true, gson.fromJson(result, (Class) model));
                        }

                    } else {
                        try {
                            if (isFromNewListener && onResultListenerNew != null) {
                                onResultListenerNew.onResult(false, object.getString("message"));
                            } else {
                                onResultListner.onResult(false, object.getString("message"));
                            }
                        } catch (Exception e) {
                            if (isFromNewListener && onResultListenerNew != null) {
                                onResultListenerNew.onResult(false, "Error");
                            } else {
                                onResultListner.onResult(false, "Error");
                            }

//                            onResultListner.onResult(false, object.getString("error"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFailure(String result) {
                if (isRequiredDialog) {
                    customProgressDialog.dismiss();
                }

                if (isFromNewListener && onResultListenerNew != null) {

                    onResultListenerNew.onFailed();

                } else {

                    getAsyncJsonReq = new GetAsyncJsonReq(url, this, jsonObject);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Error");
                    builder.setMessage(result);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (isRequiredDialog) {
//                            progressDialog.show();
                                customProgressDialog.show();
                            }

                            getAsyncJsonReq.execute();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                    onResultListner.onResult(false, "Error");
                }
            }
        };
        getAsyncJsonReq = new GetAsyncJsonReq(url, onAsyncResult, jsonObject);
        getAsyncJsonReq.execute();
    }

    public void UploadImageAndGetData() {

        /*progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while loading");
        progressDialog.show();*/

        customProgressDialog.show();

        builder = new Uri.Builder();

        for (int i = 0; i < params.size(); i++) {
            builder.appendQueryParameter(params.get(i), values.get(i));
        }


        OnAsyncResult onAsyncResult = new OnAsyncResult() {
            @Override
            public void OnSuccess(String result) {
//                progressDialog.dismiss();
                customProgressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("status").equalsIgnoreCase("success") || object.getString("status").equalsIgnoreCase("Yes")) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
                        Gson gson = gsonBuilder.create();
                        onResultListner.onResult(true, gson.fromJson(result, (Class) model));
                    } else {
                        onResultListner.onResult(false, object.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFailure(String result) {
//                progressDialog.dismiss();
                customProgressDialog.dismiss();
                if (image_file != null && image_file.toString().length() > 0) {
                    imageuploadAsyncTask = new UploadImageAsyncTask(url, this, image_file, params, values, image_param);
                } else {
                    getAsyncTask = new GetAsyncTask(url, this, builder);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Error");
                builder.setMessage(result);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        progressDialog.show();
                        customProgressDialog.show();
                        if (image_file != null && image_file.toString().length() > 0) {
                            imageuploadAsyncTask.execute();
                        } else {
                            getAsyncTask.execute();
                        }
                    }
                });
                onResultListner.onResult(false, "Error");
                builder.setNegativeButton("No", null);
                builder.show();
            }
        };

        if (image_file != null && image_file.toString().length() > 0) {
            imageuploadAsyncTask = new UploadImageAsyncTask(url, onAsyncResult, image_file, params, values, image_param);
            imageuploadAsyncTask.execute();
        } else {
            MyLogs.e("TAG", "----------------------------without file------------>");
            getAsyncTask = new GetAsyncTask(url, onAsyncResult, builder);
            getAsyncTask.execute();
        }
    }
}
