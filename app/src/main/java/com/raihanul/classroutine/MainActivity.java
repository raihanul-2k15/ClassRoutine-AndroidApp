package com.raihanul.classroutine;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.UniversalTimeScale;
import android.provider.CalendarContract;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridLayout grdRoutine;
    private TextView[][] grdRoutineCells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting up tab host
        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabRoutine = tabHost.newTabSpec("Routine");
        tabRoutine.setContent(R.id.tabRoutine);
        tabRoutine.setIndicator("Routine");
        tabHost.addTab(tabRoutine);

        TabHost.TabSpec tabMessageBoard = tabHost.newTabSpec("Board");
        tabMessageBoard.setContent(R.id.tabMessageBoard);
        tabMessageBoard.setIndicator("Board");
        tabHost.addTab(tabMessageBoard);

        TabHost.TabSpec tabPostMessage = tabHost.newTabSpec("Post");
        tabPostMessage.setContent(R.id.tabPostMessage);
        tabPostMessage.setIndicator("Post");
        tabHost.addTab(tabPostMessage);

        Bundle bd = getIntent().getExtras();
        if (bd!=null) {
            String changeTab = bd.getString(getResources().getString(R.string.intentDataKey_initialTab));
            if (getResources().getString(R.string.intentDataVal_initialTab_board).equals(changeTab)) {
                tabHost.setCurrentTab(1);
            }
        }
        // tabhost setup complete. now populating routine

        final PreferencesHelper pref = new PreferencesHelper(this);
        String which = pref.getGroupName("a2") + pref.getDepartmentName("cse") + pref.getBatchName("2k15");
        RoutineDBManager rdbMan = new RoutineDBManager(this);

        grdRoutine = (GridLayout) findViewById(R.id.grdRoutine);
        initGridAndCells();
        Utilities.loadRoutineDataInCells(grdRoutineCells, this, which);

        // routine grid setup complete, now set up message board

        final ListView lstMessageList = (ListView) findViewById(R.id.lstMessageList);
        MessageDBManager mdbMan = new MessageDBManager(this);
        MessageListViewAdapter adapter = new MessageListViewAdapter(this, mdbMan.getMessages(30));
        lstMessageList.setAdapter(adapter);
        Utilities.loadMessages(this, lstMessageList, true);

        // message board populated. now setting post message feature

        final EditText edtMessage = (EditText) findViewById(R.id.edtMessage);
        final Button btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMessage.getText().length() < 10) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_toastMsgShort), Toast.LENGTH_LONG).show();
                } else {Log.i("KUTTA","Bal");
                    JSONObject postQueryObj = new JSONObject();
                    try {
                        postQueryObj.put("boardName", pref.getGroupName("a2") + pref.getDepartmentName("cse") + pref.getBatchName("2k15"));
                        postQueryObj.put("password", pref.getMessagePostPass("rat06"));
                        postQueryObj.put("message", edtMessage.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    btnPost.setEnabled(false);
                    new ServerQuery(v.getContext()) {
                        @Override
                        public void onComplete(Context context, JSONObject postResponse) {
                            if (postResponse == null) {
                                Toast.makeText(context, getResources().getString(R.string.main_toastNoConnection), Toast.LENGTH_LONG).show();
                            } else if (postResponse.has("error")) {
                                String error = postResponse.optString("error");
                                if (error.equals("password invalid")) {
                                    Toast.makeText(context, getResources().getString(R.string.main_toastPwdIncorrect), Toast.LENGTH_LONG).show();
                                } else if (error.equals("boardName invalid")) {
                                    Toast.makeText(context, getResources().getString(R.string.main_toastBoardNotFound), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, getResources().getString(R.string.main_toastUnknownError), Toast.LENGTH_LONG).show();
                                }
                            } else if (postResponse.has("success")){
                                Utilities.loadMessages(context, lstMessageList, false);
                                Toast.makeText(context, getResources().getString(R.string.main_toastPostSuccessful), Toast.LENGTH_LONG).show();
                            }
                            btnPost.setEnabled(true);
                        }
                    }.makePostQuery(postQueryObj, "messagePost.php");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_settings) :
                Intent i1 = new Intent(this, SettingsActivity.class);
                startActivity(i1);
                return true;
            case (R.id.menu_about) :
                Intent i2 = new Intent(this, AboutActivity.class);
                startActivity(i2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initGridAndCells() {
        LayoutInflater inflater = LayoutInflater.from(this);
        grdRoutineCells = new TextView[6][10];
        for (int i=0; i<6;i++) {
            for (int j=0; j<10;j++) {
                grdRoutineCells[i][j] = new TextView(this);
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(300,10);
                grdRoutineCells[i][j].setLayoutParams(relativeParams);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(  grdRoutineCells[i][j].getLayoutParams()  );
                params.rowSpec = GridLayout.spec(i, 1, GridLayout.FILL, 1);
                params.columnSpec = GridLayout.spec(j, 1, GridLayout.FILL);
                grdRoutineCells[i][j].setLayoutParams(params);
                grdRoutineCells[i][j].setTextSize(20);

                grdRoutine.addView(grdRoutineCells[i][j]);
            }
        }
    }
}