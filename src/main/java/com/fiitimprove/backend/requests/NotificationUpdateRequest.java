package com.fiitimprove.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationUpdateRequest {

    private boolean notificationsEnabled;

    private String expoPushToken;
}