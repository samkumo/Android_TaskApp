package com.samkumo.etp4700_projekti;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Samuli on 29.3.2017.
 */

public class Overview extends Fragment {
    private Context context;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview,container,false);
        Overview_TaskFragment fragment1 = new Overview_TaskFragment();
        getFragmentManager().beginTransaction().replace(R.id.overview_content_frame1,fragment1).commit();

        return view;
    }


}
