package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sukriti.ngo.mis.R;


public class FragmentSix extends Fragment {

    private static FragmentSix INSTANCE;

    public FragmentSix() {
        // Required empty public constructor
    }


    public static FragmentSix newInstance(String param1, String param2) {
        if(INSTANCE == null) {
            INSTANCE = new  FragmentSix();
        }

        return INSTANCE;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_six, container, false);
    }
}