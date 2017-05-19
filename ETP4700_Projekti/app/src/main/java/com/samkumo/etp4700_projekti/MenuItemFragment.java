package com.samkumo.etp4700_projekti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Locale;

/**
 * Created by Samuli on 31.3.2017.
 */

public class MenuItemFragment extends android.app.Fragment {

        public static final String ARG_ITEM_NUMBER = "item_number";

        public MenuItemFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menuitem, container, false);
            int i = getArguments().getInt(ARG_ITEM_NUMBER);
            String item = getResources().getStringArray(R.array.menu_array)[i];

            int imageId = getResources().getIdentifier(item.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(item);
            return rootView;
        }
    }

