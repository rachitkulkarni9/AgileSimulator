package com.groupesan.project.java.scrumsimulator.mainpackage.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserRoleTest {
    private UserRoleSingleton instance;

    @BeforeEach
    public void setup() {
        instance = UserRoleSingleton.getInstance();
    }

    @Test
    public void testChangingRoleToScrumMaster() {
        instance.setUserRole(UserRole.SCRUM_MASTER);
        Assertions.assertEquals(instance.getUserRole(), UserRole.SCRUM_MASTER, "User role should be SCRUM_MASTER");
    }

    @Test
    public void testChangingRoleToDeveloper() {
        instance.setUserRole(UserRole.DEVELOPER);
        Assertions.assertEquals(instance.getUserRole(), UserRole.DEVELOPER, "User role should be DEVELOPER");
    }

    @Test
    public void testChangingRoleToProductOwner() {
        instance.setUserRole(UserRole.PRODUCT_OWNER);
        Assertions.assertEquals(instance.getUserRole(), UserRole.PRODUCT_OWNER, "User role should be PRODUCT_OWNER");
    }

    @Test
    public void testChangingRoleToScrumAdmin() {
        instance.setUserRole(UserRole.SCRUM_ADMIN);
        Assertions.assertEquals(instance.getUserRole(), UserRole.SCRUM_ADMIN, "User role should be SCRUM_ADMIN");
    }
}
