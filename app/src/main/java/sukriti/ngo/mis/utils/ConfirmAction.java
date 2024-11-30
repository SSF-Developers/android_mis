package sukriti.ngo.mis.utils;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.communication.SimpleHandler;


public class ConfirmAction extends DialogFragment implements OnClickListener {

    private TextView label, description;
    private RelativeLayout close;
    private FloatingActionButton fab;
    private EditText action;

    private String Title, Description, ActionName;
    private SimpleHandler callback;

    public ConfirmAction() {
        // Required empty public constructor
    }

    public static ConfirmAction newInstance() {
        ConfirmAction fragment = new ConfirmAction();
        return fragment;
    }

    public void setUp(String Title, String Description, String ActionName) {
        this.Title = Title;
        this.Description = Description;
        this.ActionName = ActionName;
    }

    public void setListener(SimpleHandler callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentStyle_SM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_confirm_action, container, false);

        label = v.findViewById(R.id.popupLabel);
        close = v.findViewById(R.id.closeContainer);
        fab = v.findViewById(R.id.fab);
        description = v.findViewById(R.id.description);
        action = v.findViewById(R.id.action);

        close.setOnClickListener(this);
        fab.setOnClickListener(this);
        action.addTextChangedListener(TextChangeWatcher);

        label.setText(Title);
        description.setText(Description);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams)params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeContainer:
                dismiss();
                break;

            case R.id.fab:
                dismiss();
                Utilities.hideKeypad(getContext(), action);
                callback.onSuccess();
                break;
        }
    }

    private TextWatcher TextChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().compareToIgnoreCase(ActionName) == 0) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.INVISIBLE);
            }
        }
    };
}