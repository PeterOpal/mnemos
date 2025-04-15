package com.example.bc_praca_x;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bc_praca_x.database.entity.Activity;
import com.example.bc_praca_x.database.enums.ActivityType;
import com.example.bc_praca_x.database.viewmodel.ActivityViewModel;

import org.w3c.dom.Text;

import java.util.Date;


public class LearningSessionOverviewFragment extends FragmentSetup {
    private ActivityViewModel activityViewModel;
    private int totalSpentTime;
    private long packageId;
    private ActivityType activityType;
    private String packageName, categoryName, reviewedCards;
    private double successRateValue;

    public LearningSessionOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        if (floatingButtonController != null) {

            Fragment fragment = new PackageOverview();
            Bundle bundle = new Bundle();
            bundle.putLong("package_id", packageId);
            bundle.putString("package_name", packageName);
            bundle.putString("category_name", categoryName);
            fragment.setArguments(bundle);

            floatingButtonController.updateFloatingButton(
                    null,
                    fragment,
                    null,
                    true,
                    R.drawable.baseline_restart_24);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalSpentTime = getArguments().getInt("totalSpentTime");
            packageId = getArguments().getLong("packId");
            packageName = getArguments().getString("packageName");
            categoryName = getArguments().getString("categoryName");
            activityType = (ActivityType) getArguments().getSerializable("mode");
            reviewedCards = getArguments().getString("reviewedCards");
            successRateValue = getArguments().getDouble("successRate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_learning_session_overview, container, false);

        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
        saveSessionToActivity();

        TextView infoBar = view.findViewById(R.id.infoBar);
        infoBar.setText(categoryName + " -> " + packageName);

        TextView timeSpent = view.findViewById(R.id.frequentCatAndPack);
        String convertedTime = converTimeToHourMinuteSeonds(totalSpentTime);
        timeSpent.setText(convertedTime);

        TextView successRate = view.findViewById(R.id.totalTimeByWeek);
        successRate.setText((int) successRateValue + "%");

        TextView reviewedCards = view.findViewById(R.id.reviewedCardsData);
        reviewedCards.setText(this.reviewedCards);

        TextView successRateInfo = view.findViewById(R.id.successRateInfo);
        if(activityType == ActivityType.FREE_MODE) {
            successRateInfo.setText(getString(R.string.info_session_verview_free_mode));
        } else {
            successRateInfo.setText(getString(R.string.info_session_verview_other_mode));
        }

        TextView mode = view.findViewById(R.id.modeData);

        String activityTypeKey = activityType.toString();
        int resId = getResources().getIdentifier(activityTypeKey, "string", getContext().getPackageName());
        String activityTypeString = getString(resId);

        mode.setText(activityTypeString);

        return view;
    }

    private void saveSessionToActivity() {
        //Date date, int timeSpentInSeconds, int revisedCardsFromAll, ActivityType activityType, int successRate, long cardId)
        activityViewModel.insertActivity(new Activity(new Date(), totalSpentTime, reviewedCards, activityType, (int)successRateValue, packageId));
    }

    private String converTimeToHourMinuteSeonds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        if(hours > 0) {
            return hours + "h " + minutes + "m " + seconds + "s";
        }

        return minutes + "m " + seconds + "s";
    }
}