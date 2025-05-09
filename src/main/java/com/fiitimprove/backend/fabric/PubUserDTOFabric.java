package com.fiitimprove.backend.fabric;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fiitimprove.backend.dto.PubUserDTO;
import com.fiitimprove.backend.models.TrainingUser;
import com.fiitimprove.backend.repositories.ImageRepository;

@Component
public class PubUserDTOFabric {
    @Autowired
    private ImageRepository imageRepository;

    public PubUserDTO create(TrainingUser t) {
        var user = t.getUser();
        var pathOp = imageRepository.findTopByUserIdOrderByIdDesc(user.getId());
        String path = null;
        if (pathOp.isPresent()) {
            path = pathOp.get().getPath();
        }

        return new PubUserDTO(
            user.getId(), 
            "%s %s".formatted(user.getName(), user.getSurname()), 
            t.getTraining().getId(), 
            t.getStatus(), 
            path
        );
    }

    public List<PubUserDTO> createList(List<TrainingUser> ts) {
        List<PubUserDTO> arr = new ArrayList<>(ts.size());
        for (var t : ts)
            arr.add(this.create(t));
        return arr;
    }
}
