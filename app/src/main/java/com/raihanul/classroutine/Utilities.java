package com.raihanul.classroutine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.util.Pair;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {

    private static final int HEADER_COLOR = Color.parseColor("#2ab2a7");
    private static final int EMPTY_COLOR = Color.parseColor("#ffffff");
    private static final int FILLED_COLOR = Color.parseColor("#b3f2e7");

    public static void loadRoutineDataInCells(TextView[][] cells, Context context, String which) {
        RoutineDBManager rdbMan = new RoutineDBManager(context);
        Map<String, List<Pair<String, Integer>>> rawRoutine = rdbMan.getRoutine(which);

        String[] colHeaders = context.getResources().getStringArray(R.array.routine_columnHeaders);
        String[] dayCols = context.getResources().getStringArray(R.array.routine_dayNames);

        for (int i=0;i<10;i++) {
            cells[0][i].setText(colHeaders[i]);
            cells[0][i].setBackgroundColor(HEADER_COLOR);
        }
        for (int i=1; i<6; i++) {
            cells[i][0].setText(dayCols[i-1]);
            cells[i][0].setBackgroundColor(HEADER_COLOR);
        }

        List<List<Pair<String, Integer>>> allDays = new ArrayList<>(5);
        allDays.add(rawRoutine.get("sun"));
        allDays.add(rawRoutine.get("mon"));
        allDays.add(rawRoutine.get("tue"));
        allDays.add(rawRoutine.get("wed"));
        allDays.add(rawRoutine.get("thu"));


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                List<Pair<String, Integer>> singleDay = allDays.get(i);
                int index = getIndexOfSlotInList(singleDay, j);
                if (index != -1) {
                    cells[i+1][j+1].setText(singleDay.get(index).first);
                    cells[i+1][j+1].setBackgroundColor(FILLED_COLOR);
                } else {
                    cells[i+1][j+1].setText("empty");
                    cells[i+1][j+1].setBackgroundColor(EMPTY_COLOR);
                }
            }
        }
        
    }

    private static int getIndexOfSlotInList(List<Pair<String, Integer>> singleDay, int slot) {
        int index = -1;
        for (int i=0;i<singleDay.size();i++) {
            if (singleDay.get(i).second == slot) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static void loadMessages(Context context, final ListView lst, final boolean notify) {
        final PreferencesHelper pref = new PreferencesHelper(context);
        JSONObject countQueryObj = new JSONObject();
        try {
            countQueryObj.put("boardName", pref.getGroupName("a2")+pref.getDepartmentName("cse")+pref.getBatchName("2k15"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ServerQuery(context) {
            @Override
            public void onComplete(Context context, JSONObject countResponse) {
                int gotCount = 0;
                if (countResponse==null) {
                    return;
                } else {
                    gotCount = countResponse.optInt("count");
                    int currentCount = pref.getMessageCount(0);
                    if (gotCount > currentCount) {
                        JSONObject messagesQuery = new JSONObject();
                        try {
                            messagesQuery.put("boardName", pref.getGroupName("a2") + pref.getDepartmentName("cse") + pref.getBatchName("2k15"));
                            messagesQuery.put("count", gotCount - currentCount);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ServerQuery(context) {
                            @Override
                            public void onComplete(Context context, JSONObject messagesResponse) {
                                final MessageDBManager mdbMan = new MessageDBManager(context);
                                if (messagesResponse != null) {
                                    JSONArray all = messagesResponse.optJSONArray("messages");
                                    for (int i = 0; i < all.length(); i++) {
                                        JSONObject single = all.optJSONObject(i);
                                        mdbMan.addMessage(single.optString("datetime"), single.optString("message"));
                                    }

                                    pref.setMessageCount(pref.getMessageCount(0) + all.length());
                                    pref.applyChanges();

                                    if (notify && pref.getNotifySetting(true)) {
                                        boolean soundEnabled = pref.getMessageSoundSetting(true);
                                        Intent i = new Intent(context, MainActivity.class);
                                        i.putExtra(context.getResources().getString(R.string.intentDataKey_initialTab), context.getResources().getString(R.string.intentDataVal_initialTab_board));
                                        PendingIntent clickNotification = PendingIntent.getActivity(context, 1507038, i, PendingIntent.FLAG_UPDATE_CURRENT);

                                        NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
                                        nb.setSmallIcon(R.mipmap.ic_launcher);
                                        nb.setTicker(String.valueOf(all.length()) + context.getResources().getString(R.string.ntfy_xNewMsg));
                                        nb.setContentTitle(String.valueOf(all.length()) + context.getResources().getString(R.string.ntfy_xNewMsg));
                                        nb.setContentText(context.getResources().getString(R.string.ntfy_newMsgContentText));
                                        nb.setContentIntent(clickNotification);
                                        nb.setWhen(System.currentTimeMillis());
                                        nb.setAutoCancel(true);

                                        if (soundEnabled) {
                                            nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                        }

                                        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                                        nm.notify(1507038, nb.build());
                                    }

                                    if (lst != null) {
                                        MessageListViewAdapter adapter = new MessageListViewAdapter(context, mdbMan.getMessages(30));
                                        lst.setAdapter(adapter);
                                    }
                                }
                            }
                        }.makeGetQuery(messagesQuery, "messageRetrieve.php");
                    }
                }
            }
        }.makeGetQuery(countQueryObj, "messageCheck.php");
    }
}
