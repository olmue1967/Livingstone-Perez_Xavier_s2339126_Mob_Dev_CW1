package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

import kotlin.streams.jdk8.StreamsKt;

public class LatestIncidentsFragment extends Fragment {

    private ListView rawDataDisplay;
    private SearchView searchView;
    private Button searchButton;

    public LatestIncidentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.latest_incidents_frag, container, false);

        rawDataDisplay = view.findViewById(R.id.rawDataDisplay);
        searchView = view.findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);

        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = searchView.getQuery().toString();
                performSearch(query);
            });
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            ArrayList<Situation> situations = main.getAllSituations();
            if (situations != null && !situations.isEmpty()) {
                updateData(situations);
            } else {
                main.startProgress();
            }
        }

        return view;
    }

    private void performSearch(String query) {
        if (getActivity() instanceof MainActivity && query != null && !query.trim().isEmpty()) {
            ArrayList<Situation> situations = ((MainActivity) getActivity()).SearchByRoad(query);
            if (situations != null && !situations.isEmpty()) {
                ArrayList<String> list = new ArrayList<>();
                for (Situation s : situations) {

                    String roadName = s.getRoadName() != null ? s.getRoadName().toUpperCase() : "";
                    String publicComment = s.getPublicComment();
                    String startTime = s.getStartTime();
                    String trafficType = s.getTrafficType();

                    String display = "* " + roadName + "\n\n" +
                            publicComment + "\n\n" +
                            " @ " + startTime + "\n";

                    list.add(display);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
                rawDataDisplay.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "No Roads found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Please enter a road name", Toast.LENGTH_SHORT).show();
        }
    }

    //public void updateData(String data) {
    public void updateData(ArrayList<Situation> data) {
        if (rawDataDisplay != null && data != null && getContext() != null) {
            ArrayList<String> list = new ArrayList<>();

            for (Situation s : data) {
                String roadName = s.getRoadName() != null ? s.getRoadName().toUpperCase() : "";
                String publicComment = s.getPublicComment();
                String startTime = s.getStartTime();
                String trafficType = s.getTrafficType();

                String display = "* " + roadName + "\n\n" +
                        publicComment + "\n\n" +
                        " @ " + startTime + "\n";

                list.add(display);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
            rawDataDisplay.setAdapter(adapter);

            // Set SearchView query text listener
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }
}
