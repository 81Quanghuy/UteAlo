package vn.iostar.service;

import vn.iostar.contants.RoleName;
import vn.iostar.entity.Role;

import java.util.Optional;

public interface RoleService {

    <S extends Role> S save(S entity);

    Optional<Role> findByRoleName(RoleName roleName);
}
