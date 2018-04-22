package com.votingapp.votingapp.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.votingapp.votingapp.R;
import com.votingapp.votingapp.manager.ServerManager;
import com.votingapp.votingapp.model.Poll;
import com.votingapp.votingapp.model.PollCollection;

import java.util.List;

public class PollFragment extends Fragment {
    private final String TAG = "PollFragment";

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poll_fragment_view, container, false);

        setupListView(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setup();
    }

    private void setup() {
        ServerManager.getAllPolls(
                getActivity(),
                this::responseSetup
        );
    }

    private void responseSetup(List<Poll> polls) {
        PollCollection.getInstance().setPolls(polls);
        populateListView();
    }

    private void setupListView(View view) {
        listView = view.findViewById(R.id.listView);
    }

    private void populateListView(){
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.listview_text,
                getMessageDescriptions(PollCollection.getInstance().getPolls()));

        listView.setAdapter(adapter);
    }

    public String[] getMessageDescriptions(List<Poll> polls) {
        String[] descriptions = new String[polls.size()];
        int i = 0;

        for (Poll poll: polls) {
            if (poll.getAuthor() == null) {
                descriptions[i] = getString(R.string.error_msg);
            }
            else {
                descriptions[i] = poll.getAuthor();
            }

            i++;
        }

        return descriptions;
    }
}
