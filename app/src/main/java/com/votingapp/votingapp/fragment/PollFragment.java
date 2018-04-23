package com.votingapp.votingapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.votingapp.votingapp.R;
import com.votingapp.votingapp.manager.ServerManager;
import com.votingapp.votingapp.model.Poll;
import com.votingapp.votingapp.model.PollCollection;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

public class PollFragment extends Fragment {
    private final String TAG = "PollFragment";
    private final int REFRESH_DELAY = 1;

    private LinearLayout pollContainer;
    private PullToRefreshView mPullToRefreshView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poll_fragment_view, container, false);

        initPollContainer(view);
        initPullToRefresh(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setup();
    }

    private void initPullToRefresh(View view) {
        mPullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPollsFromServer();

                        mPullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });
    }

    private void setup() {
        if (PollCollection.getInstance().getPolls() != null) {
            populatePollContainer();
        } else {
            getPollsFromServer();
        }
    }

    private void getPollsFromServer() {
        ServerManager.getAllPolls(
                getActivity(),
                this::responseSetup
        );
    }

    private void responseSetup(List<Poll> polls) {
        PollCollection.getInstance().setPolls(polls);
        populatePollContainer();
    }

    private void initPollContainer(View view) {
       pollContainer = view.findViewById(R.id.list_of_polls_container);
    }

    private void populatePollContainer(){
        List<Poll> polls = PollCollection.getInstance().getPolls();

        for (Poll poll : polls) {
            PieChart chart = buildPieChart(poll);

            PieDataSet dataSet = buildPieDataSet(poll, "");

            PieData pieData = buildPieData(dataSet);

            editPieLegend(chart);

            chart.setData(pieData);

            displayPieChart(chart);
        }
    }

    private void displayPieChart(PieChart chart) {
        chart.invalidate();

        pollContainer.addView(
                chart,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1050);
    }

    private void editPieLegend(PieChart chart) {
        Legend legend = chart.getLegend();

        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setWordWrapEnabled(true);
        legend.setYEntrySpace(5f);
        legend.setXEntrySpace(5f);
    }

    private PieData buildPieData(PieDataSet dataSet) {
        PieData pieData = new PieData(dataSet);

        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.BLACK);

        return pieData;
    }

    private PieDataSet buildPieDataSet(Poll poll, String label) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < poll.getOptions().size() ; i++) {
            entries.add(new PieEntry(
                    poll.getVotes().get(i),
                    poll.getOptions().get(i)));
        }

        PieDataSet dataSet = new PieDataSet(entries, label);

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        return dataSet;
    }

    private PieChart buildPieChart(Poll poll) {
        PieChart chart = new PieChart(getActivity());

        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setRotationEnabled(true);
        chart.setDrawHoleEnabled(true);
        chart.setDrawSliceText(false);
        chart.setCenterText(poll.getName());
        chart.setHoleColor(Color.WHITE);
        chart.setHoleRadius(50f);
        chart.setTransparentCircleRadius(30f);
        chart.setDragDecelerationFrictionCoef(0.99f);

        return chart;
    }
}
