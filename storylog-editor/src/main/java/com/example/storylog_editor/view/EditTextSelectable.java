package com.example.storylog_editor.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

public class EditTextSelectable extends AppCompatEditText {

    public interface onSelectionChangedListener {
        public void onSelectionChanged(int selStart, int selEnd, AppCompatEditText editText);
    }

    private List<onSelectionChangedListener> listeners;

    public EditTextSelectable(Context context) {
        super(context);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextSelectable(Context context, AttributeSet attrs) {
        super(context, attrs);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public EditTextSelectable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        listeners = new ArrayList<onSelectionChangedListener>();
    }

    public void addOnSelectionChangedListener(onSelectionChangedListener o) {
        listeners.add(o);
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        if (listeners != null && listeners.size() > 0) {
            for (onSelectionChangedListener l : listeners)
                l.onSelectionChanged(selStart, selEnd, this);
        }
    }
}