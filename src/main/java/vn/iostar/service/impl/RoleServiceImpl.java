package vn.iostar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iostar.contants.RoleName;
import vn.iostar.entity.Role;
import vn.iostar.repository.RoleRepository;
import vn.iostar.service.RoleService;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository  ;
    @Override
    public <S extends Role> S save(S entity) {
        return roleRepository.save(entity);
    }

    @Override
    public Optional<Role> findByRoleName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
