package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class NearbyIncidentsFragment extends Fragment {

    private ListView rawDataDisplay;

    public NearbyIncidentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nearby_incidents_frag, container, false);

        rawDataDisplay = view.findViewById(R.id.rawDataDisplay);

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            ArrayList<Situation> situations = main.getAllSituations();
            if (situations != null && !situations.isEmpty()) {
                ArrayList<Situation> closeSituations = main.GetCloseSituations(main.getUserLatitude(), main.getUserLongitude());

                if (closeSituations.isEmpty()) {
                    Situation noIncidents = new Situation();
                    noIncidents.setPublicComment("No incidents found near your location.");
                }

                updateData(closeSituations);
            } else {
                main.startProgress();
            }
        }

        return view;
    }

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

                //list.add(s.toString());
                list.add(display);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
            rawDataDisplay.setAdapter(adapter);
        }
    }
}
