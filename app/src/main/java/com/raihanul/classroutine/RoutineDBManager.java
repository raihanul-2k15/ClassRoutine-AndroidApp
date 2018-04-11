package com.raihanul.classroutine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.DoubleBuffer;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineDBManager extends SQLiteOpenHelper{

    private static final String DEFAULT_DB = "{\n" +
            "  \"routines\": [\n" +
            "    {\n" +
            "      \"batch\": \"2k15\",\n" +
            "      \"dept\": \"cse\",\n" +
            "      \"group\": \"a2\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"batch\": \"2k15\",\n" +
            "      \"dept\": \"eee\",\n" +
            "      \"group\": \"a1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"a2cse2k15\": {\n" +
            "    \"sun\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2200\",\n" +
            "        \"slot\": 0\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"CSE 2203\",\n" +
            "        \"slot\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Math 2207\",\n" +
            "        \"slot\": 4\n" +
            "      }\n" +
            "    ],\n" +
            "    \"mon\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2203\",\n" +
            "        \"slot\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"tue\": [\n" +
            "      {\n" +
            "        \"name\": \"Math 2207\",\n" +
            "        \"slot\": 2\n" +
            "      }\n" +
            "    ],\n" +
            "    \"wed\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2207\",\n" +
            "        \"slot\": 5\n" +
            "      }\n" +
            "    ],\n" +
            "    \"thu\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2200\",\n" +
            "        \"slot\": 8\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"a1eee2k15\": {\n" +
            "    \"sun\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2200\",\n" +
            "        \"slot\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"CSE 2203\",\n" +
            "        \"slot\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Math 2207\",\n" +
            "        \"slot\": 0\n" +
            "      }\n" +
            "    ],\n" +
            "    \"mon\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2203\",\n" +
            "        \"slot\": 5\n" +
            "      }\n" +
            "    ],\n" +
            "    \"tue\": [\n" +
            "      {\n" +
            "        \"name\": \"Math 2207\",\n" +
            "        \"slot\": 3\n" +
            "      }\n" +
            "    ],\n" +
            "    \"wed\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2207\",\n" +
            "        \"slot\": 0\n" +
            "      }\n" +
            "    ],\n" +
            "    \"thu\": [\n" +
            "      {\n" +
            "        \"name\": \"CSE 2200\",\n" +
            "        \"slot\": 4\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "};";

    public RoutineDBManager(Context context) {
        super(context, "nothing", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }

    public List<String> getBatches() {
        List<String> batches = new ArrayList<>();
        try {
            JSONArray routines = new JSONObject(DEFAULT_DB).getJSONArray("routines");
            for (int i=0;i<routines.length();i++) {
                String batch = routines.getJSONObject(i).getString("batch");
                if (!batches.contains(batch)) {
                    batches.add(batch);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return batches;
    }

    public List<String> getDepartments(String batch) {
        List<String> departments = new ArrayList<>();
        try {
            JSONArray routines = new JSONObject(DEFAULT_DB).getJSONArray("routines");
            for (int i=0;i<routines.length();i++) {
                String b = routines.getJSONObject(i).getString("batch");
                String dept = routines.getJSONObject(i).getString("dept");
                if (!departments.contains(dept) && b.equals(batch)) {
                    departments.add(dept);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public List<String> getGroups(String batch, String department) {
        List<String> groups = new ArrayList<>();
        try {
            JSONArray routines = new JSONObject(DEFAULT_DB).getJSONArray("routines");
            for (int i=0;i<routines.length();i++) {
                String b = routines.getJSONObject(i).getString("batch");
                String d = routines.getJSONObject(i).getString("dept");
                String group = routines.getJSONObject(i).getString("group");
                if (!groups.contains(group) && b.equals(batch) && d.equals(department)) {
                    groups.add(group);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public Map<String, List<Pair<String, Integer>>> getRoutine(String which) {
        Map<String, List<Pair<String, Integer>>> returnData = new HashMap<>();
        try {
            JSONObject routine = new JSONObject(DEFAULT_DB).getJSONObject(which);
            JSONArray inOneDay = routine.getJSONArray("sun");
            List<Pair<String, Integer>> oneDayList = new ArrayList<>();
            for (int i=0;i<inOneDay.length();i++) {
                JSONObject single = inOneDay.getJSONObject(i);
                oneDayList.add(new Pair<String, Integer>(single.getString("name"), single.getInt("slot")));
            }
            returnData.put("sun", oneDayList);

            inOneDay = routine.getJSONArray("mon");
            oneDayList = new ArrayList<>();
            for (int i=0;i<inOneDay.length();i++) {
                JSONObject single = inOneDay.getJSONObject(i);
                oneDayList.add(new Pair<String, Integer>(single.getString("name"), single.getInt("slot")));
            }
            returnData.put("mon", oneDayList);

            inOneDay = routine.getJSONArray("tue");
            oneDayList = new ArrayList<>();
            for (int i=0;i<inOneDay.length();i++) {
                JSONObject single = inOneDay.getJSONObject(i);
                oneDayList.add(new Pair<String, Integer>(single.getString("name"), single.getInt("slot")));
            }
            returnData.put("tue", oneDayList);

            inOneDay = routine.getJSONArray("wed");
            oneDayList = new ArrayList<>();
            for (int i=0;i<inOneDay.length();i++) {
                JSONObject single = inOneDay.getJSONObject(i);
                oneDayList.add(new Pair<String, Integer>(single.getString("name"), single.getInt("slot")));
            }
            returnData.put("wed", oneDayList);

            inOneDay = routine.getJSONArray("thu");
            oneDayList = new ArrayList<>();
            for (int i=0;i<inOneDay.length();i++) {
                JSONObject single = inOneDay.getJSONObject(i);
                oneDayList.add(new Pair<String, Integer>(single.getString("name"), single.getInt("slot")));
            }
            returnData.put("thu", oneDayList);


        } catch (JSONException e) {
            Log.i("bhul", "Error in parsing");
            e.printStackTrace();
        }

        return returnData;
    }
}
