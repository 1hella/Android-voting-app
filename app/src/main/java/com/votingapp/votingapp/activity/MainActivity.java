package com.votingapp.votingapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.votingapp.votingapp.R;
import com.votingapp.votingapp.manager.ServerManager;
import com.votingapp.votingapp.model.Poll;
import com.votingapp.votingapp.model.PollCollection;
import com.votingapp.votingapp.server.ProxyBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupServerManagerProxy();
        setupListView();

        setup();
    }

    private void setupServerManagerProxy() {
        ServerManager.setProxy(ProxyBuilder.getProxy());
    }

    private void setup() {
        ServerManager.getAllPolls(
                this,
                this::responseSetup
        );
    }

    private void responseSetup(List<Poll> polls) {
        PollCollection.getInstance().setPolls(polls);
        populateListView();
    }

    private void setupListView() {
        listView = findViewById(R.id.listView);
    }

    private void populateListView(){
        ArrayAdapter adapter = new ArrayAdapter<String>(
                this,
                R.layout.listview_text,
                getMessageDescriptions(PollCollection.getInstance().getPolls()));

        listView.setAdapter(adapter);
    }

    public String[] getMessageDescriptions(List<Poll> polls) {
        String[] descriptions = new String[polls.size()];
        int i = 0;

        for (Poll poll: polls) {
            if (poll.getAuthor() == null) {
                descriptions[i] = "NONE";
            }
            else {
                descriptions[i] = poll.getAuthor();
            }

            i++;
        }

        return descriptions;
    }
}
