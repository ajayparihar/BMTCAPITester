package com.example.bmtcapitester;

import android.os.AsyncTask;

import com.example.bmtcapitester.ApiCallback;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ApiService {

    public void makeApiCall(String endpoint, Map<String, Object> payload, ApiCallback callback) {
        new ApiTask(callback).execute(endpoint, payload);
    }

    private static class ApiTask extends AsyncTask<Object, Void, String> {
        private ApiCallback callback;
        private boolean isError = false;

        public ApiTask(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                String endpoint = (String) params[0];
                Map<String, Object> payload = (Map<String, Object>) params[1];

                URL url = new URL(endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json, text/plain, */*");
                connection.setRequestProperty("deviceType", "WEB");
                connection.setRequestProperty("lan", "en");
                connection.setRequestProperty("User-Agent", "BMTC-API-Tester/1.0");
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);

                // Send payload
                JSONObject jsonPayload = new JSONObject();
                for (Map.Entry<String, Object> entry : payload.entrySet()) {
                    jsonPayload.put(entry.getKey(), entry.getValue());
                }

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(jsonPayload.toString().getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // Read response
                int responseCode = connection.getResponseCode();
                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    isError = true;
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (isError) {
                    return "HTTP " + responseCode + ": " + response.toString();
                }

                // Format JSON response
                try {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.toString(2);
                } catch (Exception e) {
                    return response.toString();
                }

            } catch (Exception e) {
                isError = true;
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (isError) {
                callback.onError(result);
            } else {
                callback.onSuccess(result);
            }
        }
    }
}
