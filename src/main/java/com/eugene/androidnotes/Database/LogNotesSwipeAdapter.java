package com.eugene.androidnotes.Database;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.eugene.androidnotes.R;
import com.eugene.androidnotes.UI.MainActivity;

import java.util.List;


public class LogNotesSwipeAdapter extends BaseSwipeAdapter {
    private Context context;
    public static List<LogNotes> mLog;
    private LogNotes logNotes;

    public LogNotesSwipeAdapter(Context context, List<LogNotes> mylist) {
        this.context = context;
        this.mLog = mylist;
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public int getCount() {
        return mLog.size();
    }

    @Override
    public LogNotes getItem(int position) {
        return mLog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View generateView(final int i, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_note, null);
    }

    @Override
    public void fillValues(final int i, View view) {
        logNotes = getItem(i);
        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(i));
        final RelativeLayout clicking = (RelativeLayout) view.findViewById(R.id.clicking);
        clicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logNotes = getItem(i);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.noteClicked(logNotes, i);
            }
        });
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
            }

            @Override
            public void onOpen(SwipeLayout swipeLayo) {
                clicking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        swipeLayout.close();
                    }
                });
            }

            @Override
            public void onStartClose(SwipeLayout swipepyout) {
            }

            @Override
            public void onClose(SwipeLayout swipeLayout) {
                clicking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logNotes = getItem(i);
                        MainActivity mainActivity = (MainActivity) context;
                        mainActivity.noteClicked(logNotes, i);
                    }
                });
            }

            @Override
            public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {
            }

            @Override
            public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {
            }
        });
        TextView text = (TextView) view.findViewById(R.id.txtNote);
        text.setText(logNotes.getNote());
        TextView date = (TextView) view.findViewById(R.id.txtDate);
        date.setText(DateFormat.format("MMM dd, yyy", logNotes.getDate()));
        ImageView trash = (ImageView) view.findViewById(R.id.trash);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logNotes = getItem(i);
                logNotes.delete();
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.deleteListViaSwipe();
            }
        });
    }

}
