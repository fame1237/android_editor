package chawan.fame.editerbook.view;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class CustomItemAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        if (getSupportsChangeAnimations()) {
            return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
        } else {
            if (oldHolder == newHolder) {
                if (oldHolder != null) {
                    //if the two holders are equal, call dispatch change only once
                    dispatchChangeFinished(oldHolder, /*ignored*/true);
                }
            } else {
                //else call dispatch change once for every non-null holder
                if (oldHolder != null) {
                    dispatchChangeFinished(oldHolder, true);
                }
                if (newHolder != null) {
                    dispatchChangeFinished(newHolder, false);
                }
            }
            //we don't need a call to requestPendingTransactions after this, return false.
            return false;
        }
    }
}
