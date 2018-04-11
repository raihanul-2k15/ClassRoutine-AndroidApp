package com.raihanul.classroutine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public abstract class ServerQuery {
    private final Context context;

    protected ServerQuery(Context context) {
        this.context = context;
    }

    public void makePostQuery(JSONObject input, String page) {
        new AsyncTask<Object, Object, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Object... params) {
                PreferencesHelper pref = new PreferencesHelper(context);
                String addr = pref.getMessageBoardAddress("192.168.1.2");
                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;
                String responseData = "";
                try {
                    URL url = new URL("http://" + addr + "/" + params[0]);

                    String message = params[1].toString();

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(4000 /*milliseconds*/);
                    conn.setConnectTimeout(5000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    //make some HTTP header nicety
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    //open
                    conn.connect();

                    //setup send
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());
                    //clean up
                    os.flush();

                    //do somehting with response
                    is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine())!=null) {
                        responseData += line;
                    }
                } catch (IOException e) {
                    return null;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return responseObj;
            }

            @Override
            protected void onPostExecute(JSONObject output) {
                onComplete(context, output);
            }
        }.execute(page, input);
    }
    public void makeGetQuery(JSONObject input, String page) {
        new AsyncTask<Object, Object, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Object... params) {
                PreferencesHelper pref = new PreferencesHelper(context);
                String addr = pref.getMessageBoardAddress("192.168.1.2");
                InputStream is = null;
                HttpURLConnection conn = null;
                String urlStr = "http://" + addr + "/" + (String)params[0] + "?";

                JSONObject input = (JSONObject)params[1];
                for(Iterator iterator = input.keys(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    urlStr += key + "=" + input.optString(key) +"&";
                }
                urlStr = urlStr.substring(0, urlStr.length()-1);
                String responseData = "";
                try {
                    URL url = new URL(urlStr);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /*milliseconds*/);
                    conn.setConnectTimeout(15000 /* milliseconds */);

                    conn.connect();

                    //do somehting with response
                    is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine())!=null) {
                        responseData += line;
                    }
                } catch (IOException e) {
                    return null;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (conn != null) {
                        conn.disconnect();
                    }
                }

                JSONObject responseObj = null;
                try {
                    responseObj = new JSONObject(responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return responseObj;
            }

            @Override
            protected void onPostExecute(JSONObject output) {
                onComplete(context, output);
            }
        }.execute(page, input);
    }

    public abstract void onComplete(Context context, JSONObject output);
}
