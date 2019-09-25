package com.example.storylog_editor.view.ediitext;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;
import com.example.storylog_editor.R;



/**
 * Created by fame on 15/3/2018 AD.
 */

public class FictionLogDbHeavenEditText extends AppCompatEditText {
    private int[] attributes = new int[]{android.R.attr.textStyle};

    public FictionLogDbHeavenEditText(Context context) {
        super(context);
        setUp(context, null);
    }

    public FictionLogDbHeavenEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setUp(context, attrs);
    }

    public FictionLogDbHeavenEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.setTextAppearance(R.style.TextAppearance3);
        } else {
            this.setTextAppearance(getContext(), R.style.TextAppearance3);
        }
    }

    public void setOnClickListener() {

    }
}
