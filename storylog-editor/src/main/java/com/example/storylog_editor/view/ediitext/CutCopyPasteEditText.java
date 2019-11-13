package com.example.storylog_editor.view.ediitext;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class CutCopyPasteEditText extends AppCompatEditText {

    public interface OnCutCopyPasteListener {
        void onCut();

        void onCopy();

        void onPaste();
    }

    private OnCutCopyPasteListener mOnCutCopyPasteListener;

    public void setOnCutCopyPasteListener(OnCutCopyPasteListener listener) {
        mOnCutCopyPasteListener = listener;
    }

    /*
        Just the constructors to create a new EditText...
     */
    public CutCopyPasteEditText(Context context) {
        super(context);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CutCopyPasteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // Do your thing:
//        boolean consumed = super.onTextContextMenuItem(id);
        // React:
        switch (id) {
            case android.R.id.cut: {
                onCut();
                return super.onTextContextMenuItem(id);
            }
            case android.R.id.copy: {
                onCopy();
                return super.onTextContextMenuItem(id);
            }
            case android.R.id.paste: {
                onPaste();
                return false;
            }
        }
        return super.onTextContextMenuItem(id);
    }

    /**
     * Text was cut from this EditText.
     */
    public void onCut() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCut();
    }

    /**
     * Text was copied from this EditText.
     */
    public void onCopy() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onCopy();
    }

    /**
     * Text was pasted into the EditText.
     */
    public void onPaste() {
        if (mOnCutCopyPasteListener != null)
            mOnCutCopyPasteListener.onPaste();
    }
}