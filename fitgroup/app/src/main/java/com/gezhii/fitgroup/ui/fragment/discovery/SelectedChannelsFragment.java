package com.gezhii.fitgroup.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

/**
 * Created by fantasy on 16/2/15.
 */
public class SelectedChannelsFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.selected_channels_fragment, null);
        int user_id = UserModel.getInstance().getUserId();
        API.getAllChannels( 1,user_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {

            }
        });
        return rootView;
    }
}
