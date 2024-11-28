package com.example.smsprojx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    private static final String PREFS_NAME = "SMSForwarderPrefs";
    private static final String API_URL_KEY = "api_url";
    private static final String API_METHOD_KEY = "api_method";
    private static final String REGEX_PATTERN_KEY = "regex_pattern";
    private static final String DEFAULT_API_URL = "https://sms-listener-api.onrender.com/api/data";
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    StringBuilder messageBuilder = new StringBuilder();
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = smsMessage.getMessageBody();
                        messageBuilder.append(messageBody);
                    }
                    String fullMessage = messageBuilder.toString();
                    processAndSendMessage(context, fullMessage);
                }
            }
        }
    }

    private void processAndSendMessage(Context context, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String regexPattern = prefs.getString(REGEX_PATTERN_KEY, "");
        
        String processedMessage = message;
        
        // Apply regex pattern if it exists
        if (!regexPattern.isEmpty()) {
            try {
                Pattern pattern = Pattern.compile(regexPattern);
                Matcher matcher = pattern.matcher(message);
                
                if (matcher.find()) {
                    // If there's a capturing group, use that
                    if (matcher.groupCount() > 0) {
                        processedMessage = matcher.group(1);
                    } else {
                        // Otherwise use the entire match
                        processedMessage = matcher.group();
                    }
                    Log.d(TAG, "Regex applied successfully. Extracted: " + processedMessage);
                } else {
                    Log.w(TAG, "Regex pattern didn't match any content in the message");
                    // If pattern doesn't match, we'll send the original message
                }
            } catch (Exception e) {
                Log.e(TAG, "Error applying regex pattern: " + e.getMessage());
                // On error, we'll send the original message
            }
        }

        sendToApi(context, processedMessage);
    }

    private void sendToApi(Context context, String message) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String apiUrl = prefs.getString(API_URL_KEY, DEFAULT_API_URL);
            String method = prefs.getString(API_METHOD_KEY, "POST");

            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("message", message);
            String jsonBody = jsonPayload.toString();

            // Create request body with JSON
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Content-Type", "application/json");

            switch (method.toUpperCase()) {
                case "POST":
                    requestBuilder.post(body);
                    break;
                case "PUT":
                    requestBuilder.put(body);
                    break;
                case "PATCH":
                    requestBuilder.patch(body);
                    break;
                default:
                    requestBuilder.post(body);
                    break;
            }

            Request request = requestBuilder.build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API call failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "SMS successfully sent to API using " + method + " method");
                        Log.d(TAG, "Sent payload: " + jsonBody);
                    } else {
                        Log.e(TAG, "API call failed with code: " + response.code());
                        Log.e(TAG, "Response body: " + response.body().string());
                    }
                    response.close();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating or sending request: " + e.getMessage());
        }
    }
}
