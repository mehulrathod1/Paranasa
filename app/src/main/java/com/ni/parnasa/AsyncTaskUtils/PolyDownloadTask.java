package com.ni.parnasa.AsyncTaskUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.ni.parnasa.utils.MyLogs;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PolyDownloadTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    //    private boolean isProgress;
    private OnPolylineCompletionListener listener;

    public interface OnPolylineCompletionListener {
        void onPolylineAdded(String totalDistance, String totalDuration, PolylineOptions polylineOptions);

        void onPolylineAddFailed(String errorMsg);
    }

    public PolyDownloadTask(Context mContext, OnPolylineCompletionListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    private ProgressDialog dialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            data = downloadUrl(url[0]);
            MyLogs.e("Polyline", "url " + data);
        } catch (Exception e) {
            data = "";
            MyLogs.e("Polyline", "Exception while downloading " + e.toString());
            listener.onPolylineAddFailed(e.getLocalizedMessage());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (!result.equals(""))
            new ParserTask().execute(result);

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            MyLogs.e("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                routes = null;
                e.printStackTrace();
                listener.onPolylineAddFailed(e.getLocalizedMessage());
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
//            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result != null) {
                if (result.size() < 1) {
//                    Toast.makeText(mContext, "No Points", Toast.LENGTH_SHORT).show();
                    listener.onPolylineAddFailed("No Points found");
                    return;
                }

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) {
                            duration = (String) point.get("duration");
                            continue;
                        }
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);

                }

//                textView_distance.setText(distance);
//                textView_time.setText(duration);
                //mMap.addPolyline(lineOptions);
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude), 15f));

                listener.onPolylineAdded(distance, duration, lineOptions);

            }
        }
    }
}
