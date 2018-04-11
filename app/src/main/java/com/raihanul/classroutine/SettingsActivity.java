package com.raihanul.classroutine;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spnBatch;
    private Spinner spnDepartment;
    private Spinner spnGroup;
    private EditText edtPostPassword;
    private ToggleButton tglNotification;
    private ToggleButton tglRoutineSound;
    private ToggleButton tglMessageSound;
    private EditText edtBoardAddress;
    private Spinner spnCheckInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spnBatch = (Spinner) findViewById(R.id.spnBatch);
        spnDepartment = (Spinner) findViewById(R.id.spnDepartment);
        spnGroup = (Spinner) findViewById(R.id.spnGroup);
        edtPostPassword = (EditText) findViewById(R.id.edtPostPassword);
        tglNotification = (ToggleButton) findViewById(R.id.tglNotification);
        tglRoutineSound = (ToggleButton) findViewById(R.id.tglRoutineSound);
        tglMessageSound = (ToggleButton) findViewById(R.id.tglMessageSound);
        edtBoardAddress = (EditText) findViewById(R.id.edtBoardAddress);
        spnCheckInterval = (Spinner) findViewById(R.id.spnCheckInterval);

        PreferencesHelper pref = new PreferencesHelper(this);
        edtPostPassword.setText(pref.getMessagePostPass("pwd"));
        tglNotification.setChecked(pref.getNotifySetting(true));
        tglRoutineSound.setChecked(pref.getRoutineSoundSetting(true));
        tglMessageSound.setChecked(pref.getMessageSoundSetting(true));
        edtBoardAddress.setText(pref.getMessageBoardAddress("192.168.1.2"));

        final RoutineDBManager rdbMan = new RoutineDBManager(getApplicationContext());
        List<String> batches = rdbMan.getBatches();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, batches);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBatch.setAdapter(adapter);
        spnBatch.setSelection(adapter.getPosition(pref.getBatchName("2k15")));
        List<String> depts = rdbMan.getDepartments(spnBatch.getSelectedItem().toString());
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, depts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDepartment.setAdapter(adapter);
        spnDepartment.setSelection(adapter.getPosition(pref.getDepartmentName("cse")));
        List<String> groups = rdbMan.getGroups(spnBatch.getSelectedItem().toString(), spnDepartment.getSelectedItem().toString());
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGroup.setAdapter(adapter);
        spnGroup.setSelection(adapter.getPosition(pref.getGroupName("a2")));
        spnCheckInterval.setSelection(pref.getMessageCheckInterval(3));

        spnBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int timesInvoked = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timesInvoked < 1) {
                    timesInvoked++;
                    return;
                }
                List<String> depts = rdbMan.getDepartments(parent.getItemAtPosition(position).toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, depts);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnDepartment.setAdapter(adapter);
                Log.i("bhul", "on item 1");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int timesInvoked = 0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (timesInvoked < 1) {
                    timesInvoked++;
                    return;
                }
                List<String> groups = rdbMan.getGroups(spnBatch.getSelectedItem().toString(), parent.getItemAtPosition(position).toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, groups);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnGroup.setAdapter(adapter);
                Log.i("bhul", "on item 2");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home) :
                saveSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSettings() {
        PreferencesHelper pref = new PreferencesHelper(this);
        String prevBatchName = pref.getBatchName("2k15");
        String prevDeptName = pref.getDepartmentName("cse");
        String prevGroupName = pref.getGroupName("a2");
        String newBatchName = spnBatch.getSelectedItem().toString();
        String newDeptName = spnDepartment.getSelectedItem().toString();
        String newGroupName = spnGroup.getSelectedItem().toString();

        pref.setBatchName(newBatchName);
        pref.setDepartmentName(newDeptName);
        pref.setGroupName(newGroupName);
        pref.setNotifySetting(tglNotification.isChecked());
        pref.setRoutineSoundSetting(tglRoutineSound.isChecked());
        pref.setMessageSoundSetting(tglMessageSound.isChecked());
        pref.setMessagePostPass(edtPostPassword.getText().toString());
        pref.setMessageBoardAddress(edtBoardAddress.getText().toString());;

        if (!prevBatchName.equals(newBatchName) || !prevDeptName.equals(newDeptName) || !prevGroupName.equals(newGroupName)) {
            pref.setMessageCount(0);
            MessageDBManager mdbMan = new MessageDBManager(this);
            mdbMan.clearMessageTable();
            Log.i("bhul", "New");
        }

        // TODO: Handle check interval change
        pref.setMessageCheckInterval(spnCheckInterval.getSelectedItemPosition());

        pref.applyChanges();
        Toast.makeText(this, getResources().getString(R.string.settings_toastSavedSettings), Toast.LENGTH_LONG).show();
    }
}
