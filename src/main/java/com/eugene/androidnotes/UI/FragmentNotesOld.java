package com.eugene.androidnotes.UI;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eugene.androidnotes.Database.LogNotes;
import com.eugene.androidnotes.Database.LogNotesSwipeAdapter;
import com.eugene.androidnotes.R;
import com.eugene.androidnotes.Utilities.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentNotesOld extends Fragment {
    private static String NOTE_LOG = "note_log_position";
    private LogNotes mLogNotes;
    private EditText mEtNote;
    private int LogPosition;
    private Runnable mMyRunnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            LogNotesSwipeAdapter logNotesSwipeAdapter = new LogNotesSwipeAdapter(getActivity(), LogNotes.all());
            if (logNotesSwipeAdapter.getCount() > 0)
                mLogNotes = logNotesSwipeAdapter.getItem(savedInstanceState.getInt(NOTE_LOG));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        Toolbar mToolbarNote = (Toolbar) v.findViewById(R.id.toolbar_note);
        mToolbarNote.setNavigationIcon(R.mipmap.ic_arrow_back);
        mToolbarNote.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                mMyRunnable = new Runnable() {
                    @Override
                    public void run() {
                        LogNotes logNotes = mLogNotes;
                        logNotes.setNote(mEtNote.getText().toString());
                        logNotes.setDate(new Date());
                        logNotes.edit();
                        mCallbacks.updateNote();
                        mEtNote.setText("");
                    }
                };
                /**
                 * Really don't want to run a runbable but view is getting caught up on
                 * the softKeyboard
                 */
                Handler myHandler = new Handler();
                if (!mLogNotes.getNote().equals(mEtNote.getText().toString())) {
                    myHandler.postDelayed(mMyRunnable, 150);
                } else
                    mCallbacks.justViewNote();
            }
        });
        mToolbarNote.inflateMenu(R.menu.menu_delete);
        mToolbarNote.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.action_delete:
                        mLogNotes.delete();
                        mCallbacks.updateNote();
                        mEtNote.setText("");
                        break;
                    case R.id.action_voice:
                        promptSpeechInput();
                        break;
                }
                return false;
            }
        });
        if (mLogNotes != null) {
            TextView mTxtDate = (TextView) v.findViewById(R.id.txtDate);
            mTxtDate.setText(DateFormat.format("MMM dd, yyy", mLogNotes.getDate()));
            mEtNote = (EditText) v.findViewById(R.id.etNote);
            mEtNote.setText(mLogNotes.getNote());
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOTE_LOG, LogPosition);
    }

    /**
     * Sets LogNote from activity
     */
    public void setLogNotes(LogNotes logNotes, int position) {
        mLogNotes = logNotes;
        LogPosition = position;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Speech Input
     * Voice search then implements search method based on result
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(intent, MainActivity.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getApplicationContext(), "Not Supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQ_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mEtNote.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                if (mEtNote.getText().toString().length() == 0)
                    mEtNote.setText(Util.capSentences(result.get(0)));
                else {
                    mEtNote.setText(mEtNote.getText().toString() + " " + result.get(0));
                }
                int position = mEtNote.length();
                Editable etext = mEtNote.getText();
                Selection.setSelection(etext, position);
            }
        }
    }

    /**
     * Interface
     */
    private FragmentNotesOldCallbacks mCallbacks;

    public interface FragmentNotesOldCallbacks {
        void updateNote();

        void justViewNote();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (FragmentNotesOldCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

}
