package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;

public class UserStoryTestState extends UserStoryState {


    public UserStoryTestState(UserStory userStory) {
        super(userStory);
    }

    @Override
    public String onSelect() {
        return "Deleted";
    }

    @Override
    public String onComplete() {
        return "Deleted";
    }

    @Override
    public String onDelete() {
        return "Deleted";
    }
}