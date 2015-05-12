package com.eugene.androidnotes.UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eugene.androidnotes.Database.LogNotes;
import com.eugene.androidnotes.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends AppCompatActivity implements
    FragmentMain.MainFragment,
    FragmentNoteNew.MainFragment,
    SlidingUpPanelLayout.PanelSlideListener,
    FragmentNotesOld.FragmentNotesOldCallbacks {

    public static int REQ_CODE_SPEECH_INPUT = 100;
    public static String VOICE = "prompt_speech_input";

    private boolean slidindPanelVisibility = false;
    private SlidingUpPanelLayout mSlidingLayout;
    private FragmentNoteNew mFragmentNotes;
    private FragmentNotesOld mFragmentNotesOld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingLayout.setPanelSlideListener(this);
        mSlidingLayout.setTouchEnabled(false);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_list, new FragmentMain()).commit();
    }

    /**
     * Interfaces and communication
     */

    @Override
    public void newNote() {
        mFragmentNotes = new FragmentNoteNew();
        Bundle extras = new Bundle();
        REQ_CODE_SPEECH_INPUT = 100;
        extras.putInt(VOICE, 0);
        mFragmentNotes.setArguments(extras);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_notes, mFragmentNotes).commit();
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void newNoteVoice() {
        mFragmentNotes = new FragmentNoteNew();
        Bundle extras = new Bundle();
        REQ_CODE_SPEECH_INPUT = 100;
        extras.putInt(VOICE, 1);
        mFragmentNotes.setArguments(extras);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_notes, mFragmentNotes).commit();
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void noteClicked(LogNotes logNotes, int position) {
        REQ_CODE_SPEECH_INPUT = 100;
        mFragmentNotes = null;
        mFragmentNotesOld = new FragmentNotesOld();
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        mFragmentNotesOld.setLogNotes(logNotes, position);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_notes, mFragmentNotesOld).commit();
    }

    @Override
    public void saveNewNote() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container_list, new FragmentMain()).commit();
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void updateNote() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        getSupportFragmentManager().beginTransaction().replace(R.id.container_list, new FragmentMain()).commit();
    }

    @Override
    public void justViewNote() {
        mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    public void deleteListViaSwipe() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container_list, new FragmentMain()).commit();
    }

    /**
     * Handle sliding up panel listener
     */
    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {
        slidindPanelVisibility = false;
        REQ_CODE_SPEECH_INPUT = 0;
        if (mFragmentNotes != null) {
            getSupportFragmentManager().beginTransaction().detach(mFragmentNotes).commit();
            Log.e("NewNote", "NewNote");
        }
        if (mFragmentNotesOld != null) {
            getSupportFragmentManager().beginTransaction().detach(mFragmentNotesOld).commit();
            Log.e("OldNote", "OldNote");
        }
    }

    @Override
    public void onPanelExpanded(View view) {
        slidindPanelVisibility = true;
        if (mFragmentNotes != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {
    }

    public void onBackPressed() {
        if (slidindPanelVisibility) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
