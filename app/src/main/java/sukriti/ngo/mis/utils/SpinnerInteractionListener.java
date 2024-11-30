package sukriti.ngo.mis.utils;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    boolean userSelect = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        userSelect = false;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (userSelect) {
            // Your selection handling code here
            userSelect = false;
        }else {
            Log.i("_durationSelection", " userSelect - blocked! ");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (userSelect) {
            // Your selection handling code here
            userSelect = false;
        }else {
            Log.i("_durationSelection", " userSelect - blocked! ");
        }
    }

}
