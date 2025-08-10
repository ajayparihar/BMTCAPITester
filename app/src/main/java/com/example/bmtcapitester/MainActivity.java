package com.example.bmtcapitester;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView statusBar;
    private ApiTabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        initViews();
        setupViewPager();
        setupWindowInsets();
    }

    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        statusBar = findViewById(R.id.status_bar);
    }

    private void setupViewPager() {
        adapter = new ApiTabAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getTabTitle(position));
        }).attach();
    }

    private void setupWindowInsets() {
        // Handle system window insets properly
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (view, insets) -> {

            // Get system bars insets
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Apply padding to TabLayout to avoid status bar overlap
            ViewGroup.MarginLayoutParams tabLayoutParams = (ViewGroup.MarginLayoutParams) tabLayout.getLayoutParams();
            tabLayoutParams.topMargin = statusBarHeight;
            tabLayout.setLayoutParams(tabLayoutParams);

            // Adjust ViewPager2 margins - Fixed LayoutParams casting
            CoordinatorLayout.LayoutParams vpParams = (CoordinatorLayout.LayoutParams) viewPager.getLayoutParams();
            vpParams.topMargin = statusBarHeight + 72; // TabLayout height + status bar
            vpParams.bottomMargin = navigationBarHeight + 56; // Status bar height + padding
            viewPager.setLayoutParams(vpParams);

            return insets;
        });
    }

    public void updateStatus(String message) {
        runOnUiThread(() -> statusBar.setText(message));
    }

    private static class ApiTabAdapter extends FragmentStateAdapter {
        private final List<ApiTabData> tabs;

        public ApiTabAdapter(FragmentActivity fa) {
            super(fa);
            tabs = createTabs();
        }

        private List<ApiTabData> createTabs() {
            List<ApiTabData> tabList = new ArrayList<>();

            // Vehicle Trip Details
            List<ApiParameter> vehicleTripParams = new ArrayList<>();
            vehicleTripParams.add(new ApiParameter("vehicleId", "number", "22991"));
            tabList.add(new ApiTabData("Vehicle Trip",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/VehicleTripDetails_v2",
                    vehicleTripParams));

            // Find Nearby Stops
            List<ApiParameter> nearbyParams = new ArrayList<>();
            nearbyParams.add(new ApiParameter("stationName", "text", "yelahan"));
            tabList.add(new ApiTabData("Nearby Stops",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/FindNearByBusStop_v2",
                    nearbyParams));

            // List Vehicles
            List<ApiParameter> listVehiclesParams = new ArrayList<>();
            listVehiclesParams.add(new ApiParameter("vehicleRegNo", "text", "KA57F5036"));
            tabList.add(new ApiTabData("List Vehicles",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/ListVehicles",
                    listVehiclesParams));

            // Search Route
            List<ApiParameter> searchRouteParams = new ArrayList<>();
            searchRouteParams.add(new ApiParameter("routetext", "text", "401"));
            tabList.add(new ApiTabData("Search Route",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/SearchRoute_v2",
                    searchRouteParams));

            // Trip Planner
            List<ApiParameter> tripPlannerParams = new ArrayList<>();
            tripPlannerParams.add(new ApiParameter("fromStationId", "number", "34203"));
            tripPlannerParams.add(new ApiParameter("toStationId", "number", "34330"));
            tripPlannerParams.add(new ApiParameter("filterBy", "select", "0"));
            tripPlannerParams.add(new ApiParameter("serviceTypeId", "number", ""));
            tabList.add(new ApiTabData("Trip Planner",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/TripPlannerMSMD",
                    tripPlannerParams));

            // Route Details
            List<ApiParameter> routeDetailsParams = new ArrayList<>();
            routeDetailsParams.add(new ApiParameter("routeid", "number", "1367"));
            routeDetailsParams.add(new ApiParameter("servicetypeid", "select", "0"));
            tabList.add(new ApiTabData("Route Details",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/SearchByRouteDetails_v4",
                    routeDetailsParams));

            // Timetable Station
            List<ApiParameter> timetableStationParams = new ArrayList<>();
            timetableStationParams.add(new ApiParameter("fromStationId", "number", "34203"));
            timetableStationParams.add(new ApiParameter("toStationId", "number", "34330"));
            timetableStationParams.add(new ApiParameter("p_startdate", "datetime", "2024-01-01 00:00:00"));
            timetableStationParams.add(new ApiParameter("p_enddate", "datetime", "2024-12-31 23:59:59"));
            timetableStationParams.add(new ApiParameter("p_isshortesttime", "select", "0"));
            tabList.add(new ApiTabData("Timetable Station",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/GetTimetableByStation_v4",
                    timetableStationParams));

            // Fare Data
            List<ApiParameter> fareDataParams = new ArrayList<>();
            fareDataParams.add(new ApiParameter("routeno", "text", "298-M KBS-BMVG-MYS"));
            fareDataParams.add(new ApiParameter("routeid", "number", "14825"));
            fareDataParams.add(new ApiParameter("route_direction", "select", "Up"));
            fareDataParams.add(new ApiParameter("source_code", "text", "PLG"));
            fareDataParams.add(new ApiParameter("destination_code", "text", "RTRS"));
            tabList.add(new ApiTabData("Fare Data",
                    "https://bmtcmobileapi.karnataka.gov.in/WebAPI/GetMobileFareData_v2",
                    fareDataParams));

            return tabList;
        }

        @Override
        public Fragment createFragment(int position) {
            return ApiTabFragment.newInstance(tabs.get(position));
        }

        @Override
        public int getItemCount() {
            return tabs.size();
        }

        public String getTabTitle(int position) {
            return tabs.get(position).getTitle();
        }
    }
}
