package com.fiitimprove.backend.controllers;

import com.fiitimprove.backend.services.TrainingUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-users")
public class TrainingUserController {

    @Autowired
    private TrainingUserService trainingUserService;


}