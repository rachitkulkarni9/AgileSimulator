package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;

public class UserStoryNewState extends UserStoryState {
    public UserStoryNewState(UserStory userStory) {
        super(userStory);
    }

    @Override
    public String onSelect() {
        return "Completed";
    }

    @Override
    public String onComplete() {
        return "Completed";
    }

    @Override
    public String onDelete() {
        userStory.changeState(new UserStoryDeletedState(userStory));
        return "Deleted";
    }
}
