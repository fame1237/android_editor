package com.example.editer_library.view.textview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.editer_library.R;


/**
 * Created by fame on 15/3/2018 AD.
 */

public class FictionLogThongTermTextView extends AppCompatTextView {
    private int[] attributes = new int[]{android.R.attr.textStyle};

    public FictionLogThongTermTextView(Context context) {
        super(context);
        setUp(context, null);
    }

    public FictionLogThongTermTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setUp(context, attrs);
    }

    public FictionLogThongTermTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.setTextAppearance(R.style.TextAppearance4);
            } else
                this.setTextAppearance(getContext(), R.style.TextAppearance4);
        }
    }
}



