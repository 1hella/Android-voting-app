package com.votingapp.votingapp.model;


import java.util.ArrayList;
import java.util.List;

public class PollCollection {
    private static PollCollection instance = null;

    private List<Poll> polls;

    public List<Poll> getPolls() {
        return polls;
    }

    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }

    private PollCollection() {
        // Do nothing
    }

    public static PollCollection getInstance() {
        if (instance == null) {
            instance = new PollCollection();
        }

        return instance;
    }
}
