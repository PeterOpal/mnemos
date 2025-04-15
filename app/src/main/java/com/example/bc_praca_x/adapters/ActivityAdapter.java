package com.example.bc_praca_x.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.database.POJO.ActivityWithDetails;
import com.example.bc_praca_x.database.enums.ActivityType;

import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder>{

    private List<ActivityWithDetails> activityList;
    private Context context;
    private String timeSpentLang, timeLang, successLang, modeLang, reviewedCardsLang;
    private SimpleDateFormat sdf;

    public ActivityAdapter(Context context, List<ActivityWithDetails> activityList) {
        this.activityList = activityList;
        this.context = context;

        sdf = new SimpleDateFormat("HH:mm", context.getResources().getConfiguration().locale);

        timeSpentLang = "⌛ " + context.getResources().getString(R.string.spent_time) + ": ";
        timeLang = "\uD83D\uDD52 " + context.getResources().getString(R.string.Time) + ": ";
        successLang =  "\uD83E\uDDE0 " + context.getResources().getString(R.string.success_rate) + ": ";
        modeLang = "⚙\uFE0F " + context.getResources().getString(R.string.mode).toLowerCase() + ": ";
        reviewedCardsLang = "\uD83D\uDCD6 " +context.getResources().getString(R.string.reviewed_cards) + ": ";
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ActivityAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        ActivityWithDetails activityItem = activityList.get(position);
        holder.cardPackage.setText(activityItem.categoryName + "->" + activityItem.packageName);

        String formattedDate = sdf.format(activityItem.activity.getDate());

        holder.details.setText(
                timeLang + formattedDate + "\n" +
                //timeSpentLang + activityItem.activity.getTimeSpentInSeconds() + "s\n" +
                timeSpentLang + formatTimeWithSeconds(activityItem.activity.getTimeSpentInSeconds()) + "\n" +
                modeLang + getModeType(activityItem.activity.getActivityType()) + "\n" +
                successLang + activityItem.activity.getSuccessRate() + "%" + "\n" +
                reviewedCardsLang + activityItem.activity.getRevisedCardsFromAll()
        );
    }

    @Override
    public int getItemCount() { return activityList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardPackage, details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPackage = itemView.findViewById(R.id.cardPackageTitle);
            details = itemView.findViewById(R.id.infoBox);
        }
    }

    private String getModeType(ActivityType type){
        switch (type){
            case FREE_MODE:
                return context.getString(R.string.FREE_MODE);
            case ALGORITHM:
                return context.getString(R.string.ALGORITHM);
            case FREE_MODE_WITH_LEARNING:
                return context.getString(R.string.FREE_MODE_WITH_LEARNING);
            default:
                return "-";
        }
    }

    private String formatTimeWithSeconds(Integer totalSeconds) {
        int seconds = (totalSeconds != null) ? totalSeconds : 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        if (hours > 0)
            return String.format("%02dh %02dm %02ds", hours, minutes, remainingSeconds);
        else if (minutes > 0)
            return String.format("%02dm %02ds", minutes, remainingSeconds);
        else
            return remainingSeconds + " s";
    }

}
