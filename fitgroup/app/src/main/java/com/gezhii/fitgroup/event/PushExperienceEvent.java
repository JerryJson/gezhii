package com.gezhii.fitgroup.event;

import com.gezhii.fitgroup.dto.PushExperienceDto;

/**
 * Created by xianrui on 15/11/16.
 */
public class PushExperienceEvent {
    PushExperienceDto pushExperienceDto;

    public PushExperienceEvent(PushExperienceDto pushExperienceDto) {
        this.pushExperienceDto = pushExperienceDto;
    }

    public PushExperienceDto getPushExperienceDto() {
        return pushExperienceDto;
    }
}
