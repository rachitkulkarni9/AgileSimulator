package com.groupesan.project.java.scrumsimulator.mainpackage.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserStoryTest {
    private UserStory myUserStory;

    @BeforeEach
    public void setup() {
        myUserStory = UserStoryFactory.getInstance()
                .createNewUserStory("predefinedUS1", "description1", 1.0, 1);
    }

    @Test
    public void testUserStoryInitialized() {
        Assertions.assertEquals(myUserStory.getStatus(), UserStory.UserStoryStatus.UNSELECTED);
    }
}
