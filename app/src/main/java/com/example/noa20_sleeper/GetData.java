package com.example.noa20_sleeper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData extends AsyncTask<String, Void, String> {
    private static final String TAG = "LOG_TAG";
    private static final String IP = "http://192.168.0.16" + ":9090/NOA/";
//    private static final String IP = "http://172.30.1.37" + ":9090/NOA/";
    String errorString = null;
    String serverURL;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(String... params) {
        serverURL = IP + params[0];
        String postParameters="";

        for(int i = 1;i < params.length;i+=2){
            postParameters += params[i] + "=" + params[i+1];
            if(i != params.length-2) postParameters += "&";
        }

        Log.d(TAG, "doInBackground: GetData "+postParameters);

        try {

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();


            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            while(responseStatusCode!=200){
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();
            }

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();

            Log.d(TAG, "doInBackground: GetData : "+ sb.toString());

            return sb.toString().trim();
        } catch (Exception e) {

            Log.d(TAG, "InsertData: Error ", e);
            errorString = e.toString();

            return null;
        }
    }
}
