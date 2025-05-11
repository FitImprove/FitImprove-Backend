package com.fiitimprove.backend.fabric;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fiitimprove.backend.dto.PubUserForTrainingDTO;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.ImageRepository;

@Component
public class PubUserDTOFabric {
    @Autowired
    private ImageRepository imageRepository;

    public PubUserForTrainingDTO create(TrainingUser t) {
        var user = t.getUser();
        var pathOp = imageRepository.findTopByUserIdOrderByIdDesc(user.getId());
        String path = null;
        if (pathOp.isPresent()) {
            path = pathOp.get().getPath();
        }

        return new PubUserForTrainingDTO(
            user.getId(), 
            "%s %s".formatted(user.getName(), user.getSurname()), 
            t.getTraining().getId(), 
            t.getStatus(), 
            path
        );
    }

    public List<PubUserForTrainingDTO> createList(List<TrainingUser> ts) {
        List<PubUserForTrainingDTO> arr = new ArrayList<>(ts.size());
        for (var t : ts)
            arr.add(this.create(t));
        return arr;
    }
}
