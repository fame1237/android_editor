package com.example.storylog_editor.view.ediitext;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;

public class GoEditText extends AppCompatEditText {
    ArrayList<GoEditTextListener> listeners;

    public GoEditText(Context context) {
        super(context);
        listeners = new ArrayList<>();
    }

    public GoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<>();
    }

    public GoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<>();
    }

    public void addListener(GoEditTextListener listener) {
        try {
            listeners.add(listener);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here you can catch paste, copy and cut events
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        boolean consumed = super.onTextContextMenuItem(id);
        switch (id) {
            case android.R.id.cut:
                onTextCut();
                return consumed;
            case android.R.id.paste:
                onTextPaste();
                return false;
            case android.R.id.copy:
                onTextCopy();
                return consumed;
        }
        return consumed;
    }

    public void onTextCut() {
    }

    public void onTextCopy() {
    }

    /**
     * adding listener for Paste for example
     */
    public void onTextPaste() {
        for (GoEditTextListener listener : listeners) {
            listener.onUpdate();
        }
    }

    public interface GoEditTextListener {
        void onUpdate();
    }
}