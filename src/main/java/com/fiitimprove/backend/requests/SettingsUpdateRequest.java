package com.fiitimprove.backend.requests;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fiitimprove.backend.models.Settings;
import com.fiitimprove.backend.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsUpdateRequest {

    private Settings.Theme theme;

    private int fontSize;

    private Boolean notifications;
}
