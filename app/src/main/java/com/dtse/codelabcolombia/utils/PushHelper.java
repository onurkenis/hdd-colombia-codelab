package com.dtse.codelabcolombia.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dtse.codelabcolombia.model.Android;
import com.dtse.codelabcolombia.model.ClickAction;
import com.dtse.codelabcolombia.model.Message;
import com.dtse.codelabcolombia.model.Notification;
import com.dtse.codelabcolombia.model.PushModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushHelper {

    private static PushHelper pushHelper;

    private static final String TAG = "PushHelper";
    private String tokenRequestUrl = "https://oauth-login.cloud.huawei.com/oauth2/v2/token";
    private String clientId = "101850887";
    private String clientSecret = "849cac2865403e4590150def5ba8a43452ce6ceff8c279f5b15a8abee55cb5cc";
    private String pushRequestUrl = "https://push-api.cloud.huawei.com/v1/" + clientId + "/messages:send";
    private String pushtoken;

    private RequestQueue queue;

    private Context context;

    public static PushHelper getInstance(Context context) {
        if (pushHelper == null) pushHelper = new PushHelper(context);
        return pushHelper;
    }

    private PushHelper(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        requestPushToken();
    }


    /**
     * This method sends a push request. When this method called, a fresh access token is generated
     *
     * @param title of the push notification
     * @param body  of the push notification
     */
    public void pushRequest(final String title, final String body) {

        StringRequest tokenRequest = new StringRequest(Request.Method.POST, tokenRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JsonElement element = JsonParser.parseString(response);

                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    //read response here
                } else if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    //read response here
                    Log.d("", "");
                    try {
                        sendPushRequest(title, body, object.get("access_token").getAsString());
                        Log.d("TOKEN", object.get("access_token").getAsString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("grant_type", "client_credentials");
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);

                return params;
            }
        };
        queue.add(tokenRequest);

    }


    /**
     * This method only requests and returns the access token
     */
    private void getAccessToken() {
        StringRequest tokenRequest = new StringRequest(Request.Method.POST, tokenRequestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("TOKEN RESPONSE", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("TOKEN REQUEST FAIL", error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("grant_type", "client_credentials");
                params.put("client_id", clientId);
                params.put("client_secret", clientSecret);

                return params;
            }
        };
        queue.add(tokenRequest);
    }


    /**
     * This method sends a push request. You need to generate accessToken beforehand to use this method
     *
     * @param title
     * @param body
     * @param accessToken
     * @throws JSONException
     * @throws IllegalAccessException
     */
    private void sendPushRequest(String title, String body, final String accessToken) throws JSONException, IllegalAccessException {
        PushModel pushModel = new PushModel();
        String clientToken = getPushToken();
        pushModel.setValidate_only(false);

        Message message = new Message();
        Notification notification = new Notification();

        Android android = new Android();
        Notification androidNotification = new Notification();
        androidNotification.setBody(body);
        androidNotification.setTitle(title);

        ClickAction clickAction = new ClickAction();
        clickAction.setType(3);
        androidNotification.setClick_action(clickAction);
        android.setNotification(androidNotification);
        message.setNotification(notification);

        message.setAndroid(android);
        message.addToken(clientToken);
        pushModel.setMessage(message);

        JSONObject pushJson = null;

        Gson gson = new Gson();
        String jsonString = gson.toJson(pushModel);
        try {
            pushJson = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest pushRequest = new JsonObjectRequest(pushRequestUrl, pushJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("JSON Response", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("JSON FAIL", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }
        };
        queue.add(pushRequest);

    }

    /**
     * get push token
     */
    private void requestPushToken() {
        Log.i(TAG, "get token: begin");

        // get token
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
                    pushtoken = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
                    if (!TextUtils.isEmpty(pushtoken)) {
                        Log.i(TAG, "get token:" + pushtoken);

                    }
                } catch (Exception e) {
                    Log.i(TAG, "getToken failed, " + e);

                }
            }
        }.start();
    }

    private String getPushToken() {
        return TextUtils.isEmpty(pushtoken) ? PushService.pushToken : pushtoken;
    }

}
