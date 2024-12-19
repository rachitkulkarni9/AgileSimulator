package com.groupesan.project.java.scrumsimulator.mainpackage.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserRolePermissions {
    private static final Map<UserRole, Set<UserAction>> roleToAllowedActions = new HashMap<>(Map.of(
            UserRole.SCRUM_MASTER, new HashSet<>(
                    Set.of(
                            UserAction.CONTROL_SPRINT_PARAMETERS,
                            UserAction.POPULATE_SPRINT_BACKLOG,
                            UserAction.RESOLVE_ISSUES,
                            UserAction.CONDUCT_SPIKE_ACTIVITIES
                    )
            ),
            UserRole.DEVELOPER, new HashSet<>(
                    Set.of(
                            UserAction.ESTIMATE_WORK_EFFORT,
                            UserAction.RESOLVE_ISSUES,
                            UserAction.CONDUCT_SPIKE_ACTIVITIES
                    )
            ),
            UserRole.PRODUCT_OWNER, new HashSet<>(
                    Set.of(
                            UserAction.MANAGE_USER_STORES,
                            UserAction.RESOLVE_ISSUES
                    )
            ),
            UserRole.SCRUM_ADMIN, new HashSet<>(
                    Set.of(
                            UserAction.MANAGE_USER_STORES,
                            UserAction.CONTROL_SPRINT_PARAMETERS,
                            UserAction.POPULATE_SPRINT_BACKLOG,
                            UserAction.ESTIMATE_WORK_EFFORT,
                            UserAction.RESOLVE_ISSUES,
                            UserAction.CONDUCT_SPIKE_ACTIVITIES,
                            UserAction.FINE_TUNE_PROBABILITIES,
                            UserAction.MANAGE_SIMULATION_STATE
                    )
            )
    ));

    public static boolean actionAllowed(UserRole role, UserAction action) {
        if (roleToAllowedActions.containsKey(role)) {
            return roleToAllowedActions.get(role).contains(action);
        }
        return false;
    }
}
