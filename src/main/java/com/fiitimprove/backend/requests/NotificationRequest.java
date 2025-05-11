package com.fiitimprove.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    private Long userId;
    private String title;
    private String message;
    private Long trainingId;
}
