package com.raihanul.classroutine;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageListViewAdapter  extends BaseAdapter{

    private List<Pair<String, String>> items;
    private Context context;
    private static LayoutInflater inflater = null;

    public MessageListViewAdapter(Context context, List<Pair<String, String>> items) {
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.message_list_row, null);
        }
        TextView txtTime = (TextView) view.findViewById(R.id.txtMessageTime);
        TextView txtText = (TextView) view.findViewById(R.id.txtMessageText);
        txtTime.setText(items.get(position).first);
        txtText.setText(items.get(position).second);
        return view;
    }
}
