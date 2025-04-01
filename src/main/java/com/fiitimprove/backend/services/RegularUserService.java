package com.fiitimprove.backend.services;


import com.fiitimprove.backend.models.RegularUser;
import com.fiitimprove.backend.repositories.RegularUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegularUserService {

    @Autowired
    private RegularUserRepository regularUserRepository;

    public RegularUser createRegularUser(RegularUser regularUser) {
        regularUser.setJoinedAt(LocalDate.now());
        regularUser.setVerified(false);
        return regularUserRepository.save(regularUser);
    }

    public List<RegularUser> findAllRegularUsers() {
        return regularUserRepository.findAll();
    }


}
