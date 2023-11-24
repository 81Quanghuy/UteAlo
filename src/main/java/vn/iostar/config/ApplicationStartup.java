package vn.iostar.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.Role;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.RoleService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    String[] groupArray = {
            "201101C", "201102C", "201103C",
            "Công nghệ thông tin 1", "Công nghệ thông tin 2", "Công nghệ thông tin 3", "Công nghệ thông tin 4",
            "Xây dựng 1", "Xây dựng 2", "Xây dựng 3", "Xây dựng 4",
            "Kinh tế 1", "Kinh tế 2", "Kinh tế 3", "Kinh tế 4",
            "Du lịch 1", "Du lịch 2", "Du lịch 3", "Du lịch 4",
            "Ngoại ngữ 1", "Ngoại ngữ 2", "Ngoại ngữ 3", "Ngoại ngữ 4",
            "Ngân hàng 1", "Ngân hàng 2", "Ngân hàng 3", "Ngân hàng 4",
            "Quản trị kinh doanh 1", "Quản trị kinh doanh 2", "Quản trị kinh doanh 3", "Quản trị kinh doanh 4"
    };

    // Chuyển đổi mảng thành List
    List<String> groupList = Arrays.asList(groupArray);
    private final RoleService roleService;
    private final PostGroupService postGroupService;

    public ApplicationStartup(RoleService roleService, PostGroupService postGroupService) {
        this.roleService = roleService;
        this.postGroupService = postGroupService;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        createRole();
        createGroup();
    }

    private void createGroup() {
        for (String groupName : groupList) {
            createGroupIfNotExist(groupName);
        }
    }

    private void createGroupIfNotExist(String groupName) {
        if (postGroupService.findByPostGroupName(groupName).isEmpty()) {
            PostGroup postGroup = new PostGroup();
            postGroup.setPostGroupName(groupName);
            postGroup.setIsPublic(true);
            postGroup.setIsApprovalRequired(true);
            postGroup.setCreateDate(new Date());
            postGroup.setUpdateDate(new Date());
            postGroupService.save(postGroup);
        }
    }

    private void createRole() {
        createRoleIfNotExist(RoleName.SinhVien);
        createRoleIfNotExist(RoleName.GiangVien);
        createRoleIfNotExist(RoleName.Admin);
        createRoleIfNotExist(RoleName.NhanVien);
    }

    private void createRoleIfNotExist(RoleName roleName) {
        if (roleService.findByRoleName(roleName).isEmpty()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleService.save(role);
        }
    }
}
