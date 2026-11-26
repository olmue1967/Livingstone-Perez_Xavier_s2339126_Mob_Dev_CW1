package org.me.gcu.livingstone_perez_xavier_s2339126;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class IncidentMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Situation> situationsList = new ArrayList<>();

    public IncidentMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.incident_map_frag, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            ArrayList<Situation> situations = main.getAllSituations();
            if (situations != null && !situations.isEmpty()) {
                updateData(situations);
            } else {
                main.startProgress();
            }
        }
    }

    public void updateData(ArrayList<Situation> situations) {
        if (situations != null) {
            this.situationsList = situations;
            updateMapMarkers();
        }
    }

    public void updateData(String data) {
        // Fallback for String if passed
    }

    private void updateMapMarkers() {
        if (mMap == null || situationsList == null || situationsList.isEmpty()) {
            return;
        }
        mMap.clear();
        LatLng firstLocation = null;

        for (Situation s : situationsList) {
            LatLng latLng = new LatLng(s.getLatitude(), s.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(s.getRoadName() != null ? s.getRoadName() : "Incident")
                    .snippet(s.getPublicComment() != null ? s.getPublicComment() : "ID: " + s.getID()));
            if (firstLocation == null) {
                firstLocation = latLng;
            }
        }

        if (firstLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 8f));
        }
    }
}
