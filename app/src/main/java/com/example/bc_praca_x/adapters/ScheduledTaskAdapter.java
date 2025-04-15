package com.example.bc_praca_x.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.models.ScheduledTask;

import java.util.List;

public class ScheduledTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE_HEADER = 0;
    private static final int VIEW_TYPE_CATEGORY_CONTAINER = 1;
    private List<ScheduledTask> tasks;
    private OnItemClickListener listener;
    public ScheduledTaskAdapter(List<ScheduledTask> tasks) { this.tasks = tasks; }

    public interface OnItemClickListener { void onItemClick(View v,ScheduledTask item);}

    @Override
    public int getItemViewType(int position) {
        ScheduledTask task = tasks.get(position);

        if (task.isSectionHeader()) {
            return VIEW_TYPE_DATE_HEADER;
        } else {
            return VIEW_TYPE_CATEGORY_CONTAINER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_DATE_HEADER) {
            View view = inflater.inflate(R.layout.task_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.task_category_container, parent, false);
            return new CategoryContainerViewHolder(view);
        }
    }

    @Override
    public int getItemCount() { return tasks.size(); }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ScheduledTask task = tasks.get(position);

        if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).dateTextView.setText("\uD83D\uDDD3\uFE0F " + task.displayDate);
        } else if (holder instanceof CategoryContainerViewHolder) {
            CategoryContainerViewHolder categoryHolder = (CategoryContainerViewHolder) holder;
            categoryHolder.categoryTitle.setText(categoryHolder.itemView.getContext().getString(R.string.Category_dots) + task.getTaskName());
            categoryHolder.taskContainer.removeAllViews();

            // Adding subtasks to the category = packages
            for (ScheduledTask subTask : task.getSubTasks()) {
                View taskView = LayoutInflater.from(categoryHolder.taskContainer.getContext()).inflate(R.layout.task_item, categoryHolder.taskContainer, false);

                TextView taskText = taskView.findViewById(R.id.cardPackageTitle);
                taskText.setText(subTask.getTaskName());

                TextView taskDescription = taskView.findViewById(R.id.infoBox);
                taskDescription.setText(subTask.getCardsToReview());

                if(subTask.isUserTask() && listener != null) {
                    categoryHolder.taskContainer.setOnLongClickListener(v -> {
                        listener.onItemClick(v,subTask);
                        return true;
                    });
                }

                categoryHolder.taskContainer.addView(taskView);
            }
        }
    }

    public void setOnItemClickListener(ScheduledTaskAdapter.OnItemClickListener listener) { this.listener = listener; }

    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateHeaderViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.groupped_date);
        }
    }

    public static class CategoryContainerViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        LinearLayout taskContainer;

        public CategoryContainerViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryHeaderText);
            taskContainer = itemView.findViewById(R.id.taskContainer);
        }
    }
}
