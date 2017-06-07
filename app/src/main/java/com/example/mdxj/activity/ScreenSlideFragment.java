package com.example.mdxj.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.mdxj.R;

/**
 * Created by ypgt-007 on 2017/6/7.
 */
public class ScreenSlideFragment extends Fragment {


    private WelcomeActivity welcomeActivity;

    public ScreenSlideFragment(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        int position = args.getInt("position");
        int layoutId = getLayoutId(position);


        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);
        if (position == 0) {

            welcomeActivity.initFirstScreenViews(rootView, savedInstanceState);
        }
        if (position == 1) {

            welcomeActivity.initSecondScreenViews(rootView, savedInstanceState);
        }
        if (position == 2) {

            welcomeActivity.initThirdScreenViews(rootView, savedInstanceState);
        }

        return rootView;
    }

    private int getLayoutId(int position) {

        int id = 0;
        if (position == 0) {

            id = R.layout.first_screen;

        } else if (position == 1) {

            id = R.layout.second_screen;
        } else if (position == 2) {

            id = R.layout.third_screen;
        }
        return id;
    }


}
