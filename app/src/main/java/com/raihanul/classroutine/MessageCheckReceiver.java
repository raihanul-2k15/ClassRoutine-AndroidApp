package com.raihanul.classroutine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageCheckReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Utilities.loadMessages(context,null,true);
    }
}
