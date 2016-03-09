package com.gezhii.fitgroup.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

/**
 * Created by xianrui on 15/8/18.
 */
public class BaseFragment extends Fragment implements View.OnTouchListener {


    HashMap<String, Object> newInstanceParams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("xianrui", this + " onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("xianrui", this + " onViewCreated");
        view.setOnTouchListener(this);
    }

    public HashMap<String, Object> getNewInstanceParams() {
        if (newInstanceParams == null) {
            newInstanceParams = new HashMap<>();
        }
        return newInstanceParams;
    }

    public void setNewInstanceParams(HashMap<String, Object> newInstanceParams) {
        this.newInstanceParams = newInstanceParams;
    }

    public void clearNewInstanceParams() {
        if (newInstanceParams != null) {
            newInstanceParams.clear();
        }
    }

    public void finish() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void finish(Class<? extends BaseFragment> c) {
        getActivity().getSupportFragmentManager().popBackStack(c.getName(), 0);
    }

    public void finishAll() {
//        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getActivity().getSupportFragmentManager().popBackStack(getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getName()
//                    , FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        }
        ((MainActivity)getActivity()).finishAll();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("xianrui", this + " onResume");
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("xianrui", this + " onPause");
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }


    public void onFragmentResume() {
        Log.i("xianrui", this + " onFragmentResume");
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i("xianrui", this + " onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.i("xianrui", this + " onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.i("xianrui", this + " onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("xianrui", this + " onDestroyView");
    }

    public void showToast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }


}
