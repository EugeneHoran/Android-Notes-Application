package com.eugene.androidnotes.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.eugene.androidnotes.Database.LogNotes;
import com.eugene.androidnotes.Database.LogNotesSwipeAdapter;
import com.eugene.androidnotes.R;

import github.chenupt.dragtoplayout.DragTopLayout;

public class FragmentMain extends Fragment {
    private View v;
    private LogNotesSwipeAdapter mLogNotesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        Toolbar mToolbarMain = (Toolbar) v.findViewById(R.id.toolbar_journal_main);
        mToolbarMain.inflateMenu(R.menu.menu_main);
        mToolbarMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.action_new:
                        mCallbacks.newNote();
                        break;
                    case R.id.action_voice:
                        mCallbacks.newNoteVoice();
                        break;
                }
                return false;
            }
        });

        ListView mListNotes = (ListView) v.findViewById(R.id.listNotes);
        mLogNotesAdapter = new LogNotesSwipeAdapter(getActivity(), LogNotes.all());
        mListNotes.setAdapter(mLogNotesAdapter);
        mLogNotesAdapter.notifyDataSetChanged();
        updateList();
        /**
         * TODO currently disabled till next update
         */
        DragTopLayout mDragLayout = (DragTopLayout) v.findViewById(R.id.drag_layout);
        mDragLayout.setOverDrag(false);
        mDragLayout.setTouchMode(false);
        mDragLayout.toggleTopView();
        return v;
    }

    private void updateList() {
        TextView noNotes = (TextView) v.findViewById(R.id.noNotes);
        if (mLogNotesAdapter.getCount() == 0) {
            noNotes.setVisibility(View.VISIBLE);
        } else {
            noNotes.setVisibility(View.GONE);
        }
    }

    /**
     * Callbacks
     */
    private MainFragment mCallbacks;

    public interface MainFragment {
        void newNote();

        void newNoteVoice();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (MainFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }
}
