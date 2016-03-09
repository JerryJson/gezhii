package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.UserStepDTO;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.xianrui.lite_common.litesuits.android.log.Log;

/**
 * Created by fantasy on 16/1/22.
 */
public class UserStepModel {

    private static final String TAG_USER_STEP = "tag_user_step";

    private UserStepDTO mUserStepDTO;

    private static class UserStepModelHolder {
        public final static UserStepModel sington = new UserStepModel();
    }

    public static UserStepModel getInstance() {
        return UserStepModelHolder.sington;
    }

    private UserStepModel() {
        tryLoadLocal();
    }

    private boolean tryLoadLocal() {
        mUserStepDTO = (UserStepDTO) DataKeeperHelper.getInstance().getUserStepCache().get(TAG_USER_STEP);
        return mUserStepDTO != null;
    }

    public int getUserTodayStep() {
        int stepNum = 0;
        if (tryLoadLocal()) {
            if (TimeHelper.getInstance().getTodayString().equals(mUserStepDTO.getDate())) {//是今天
                stepNum = mUserStepDTO.getStepNums();
            }
        }
        return stepNum;
    }

    public void updateTodayStep(int stepNum) {
        Log.i("darren", "updateStep:" + stepNum);
        UserStepDTO userStepDTO = new UserStepDTO();
        userStepDTO.setDate(TimeHelper.getInstance().getTodayString());
        userStepDTO.setStepNums(stepNum);
        DataKeeperHelper.getInstance().getUserStepCache().put(TAG_USER_STEP, userStepDTO);
    }
}
