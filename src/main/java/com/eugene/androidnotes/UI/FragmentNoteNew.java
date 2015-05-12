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
import com.eugene.androidnotes.R;
import com.eugene.androidnotes.Utilities.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentNoteNew extends Fragment {

    private EditText mEtNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        int voiceSearch = getArguments().getInt(MainActivity.VOICE);
        if (voiceSearch == 1)
            promptSpeechInput();
        mEtNote = (EditText) v.findViewById(R.id.etNote);
        mEtNote.setText("");
        mEtNote.requestFocus();

        Toolbar mToolbarNote = (Toolbar) v.findViewById(R.id.toolbar_note);
        mToolbarNote.setNavigationIcon(R.mipmap.ic_arrow_back);
        mToolbarNote.inflateMenu(R.menu.menu_new);
        mToolbarNote.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_voice) {
                    promptSpeechInput();
                }
                return false;
            }
        });
        mToolbarNote.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                Runnable mMyRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mEtNote.getText().toString().trim().length() > 0) {
                            LogNotes logNotes = new LogNotes();
                            logNotes.setNote(mEtNote.getText().toString());
                            logNotes.setDate(new Date());
                            logNotes.save();
                        }
                        mCallbacks.saveNewNote();
                        mEtNote.setText("");
                    }
                };
                Handler myHandler = new Handler();
                myHandler.postDelayed(mMyRunnable, 100);
            }
        });

        TextView mTxtDate = (TextView) v.findViewById(R.id.txtDate);
        mTxtDate.setText(DateFormat.format("MMM dd, yyy", new Date()));
        return v;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Callbacks
     */

    private MainFragment mCallbacks;

    public interface MainFragment {
        void saveNewNote();
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
