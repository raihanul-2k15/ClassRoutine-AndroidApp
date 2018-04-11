package com.raihanul.classroutine;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String filename = "preferences";
    private static final String notifySetting = "notificationEnabled";
    private static final String routineSoundSetting = "routineSoundEnabled";
    private static final String messageSoundSetting = "messageSoundEnabled";
    private static final String batchName = "currentBatch";
    private static final String departmentName = "currentDepartment";
    private static final String groupName = "currentGroup";
    private static final String messagePostPass = "messagePostPassword";
    private static final String messageBoardAddress = "boardAddress";
    private static final String messageCheckInterval = "checkingInterval";
    private static final String messageCount = "currentMessageCount";

    public PreferencesHelper(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // getters
    public boolean getNotifySetting(boolean def) {
        return pref.getBoolean(notifySetting, def);
    }
    public boolean getRoutineSoundSetting(boolean def) {
        return pref.getBoolean(routineSoundSetting, def);
    }
    public boolean getMessageSoundSetting(boolean def) {
        return pref.getBoolean(messageSoundSetting, def);
    }
    public String getBatchName(String def) {
        return pref.getString(batchName, def);
    }
    public String getDepartmentName(String def) {
        return pref.getString(departmentName, def);
    }
    public String getGroupName(String def) {
        return pref.getString(groupName, def);
    }
    public String getMessagePostPass(String def) {
        return pref.getString(messagePostPass, def);
    }
    public String getMessageBoardAddress(String def) {
        return pref.getString(messageBoardAddress, def);
    }
    public int getMessageCheckInterval(int def) {
        return pref.getInt(messageCheckInterval, def);
    }
    public int getMessageCount(int def) {
        return pref.getInt(messageCount, def);
    }

    // setters
    public void setNotifySetting(boolean val) {
        editor.putBoolean(notifySetting, val);
    }
    public void setRoutineSoundSetting(boolean val) {
        editor.putBoolean(routineSoundSetting, val);
    }
    public void setMessageSoundSetting(boolean val) {
        editor.putBoolean(messageSoundSetting, val);
    }
    public void setBatchName(String val) {
        editor.putString(batchName, val);
    }
    public void setDepartmentName(String val) {
        editor.putString(departmentName, val);
    }
    public void setGroupName(String val) {
        editor.putString(groupName, val);
    }
    public void setMessagePostPass(String val) {
        editor.putString(messagePostPass, val);
    }
    public void setMessageBoardAddress(String val) {
        editor.putString(messageBoardAddress, val);
    }
    public void setMessageCheckInterval(int val) {
        editor.putInt(messageCheckInterval, val);
    }
    public void setMessageCount(int val) {
        editor.putInt(messageCount, val);
    }


    public void applyChanges() {
        editor.apply();
    }
}
