package com.fiitimprove.backend.fabric;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fiitimprove.backend.dto.SearchCoachDTO;
import com.fiitimprove.backend.models.Coach;
import com.fiitimprove.backend.repositories.ImageRepository;

@Component
public class SearchCoachFabric {
    @Autowired
    private ImageRepository imageRepository;

    public SearchCoachDTO create(Coach c) {
        var pathOp = imageRepository.findTopByUserIdOrderByIdDesc(c.getId());
        String path = null;
        if (pathOp.isPresent()) {
            path = pathOp.get().getPath();
        }
        String address = "";
        if (c.getGym() != null)
            address = c.getGym().getAddress();

        return new SearchCoachDTO(
            c.getId(), 
            "%s %s".formatted(c.getName(), c.getSurname()),
            path,
            address
        );
    }

    public List<SearchCoachDTO> createList(List<Coach> cs) {
        List<SearchCoachDTO> arr = new ArrayList<>(cs.size());
        for (var c : cs)
            arr.add(this.create(c));
        return arr;
    }
}
