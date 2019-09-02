package chawan.fame.editerbook.view.textview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import chawan.fame.editerbook.R;


/**
 * Created by fame on 15/3/2018 AD.
 */

public class FictionLogDbHeavenTextView extends AppCompatTextView {
    private int[] attributes = new int[]{android.R.attr.textStyle};

    public FictionLogDbHeavenTextView(Context context) {
        super(context);
        setUp(context, null);
    }

    public FictionLogDbHeavenTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setUp(context, attrs);
    }

    public FictionLogDbHeavenTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.setTextAppearance(R.style.TextAppearance3);
            } else  {
                this.setTextAppearance(getContext(), R.style.TextAppearance3);
            }
        }
    }

    public void setOnClickListener() {

    }
}
