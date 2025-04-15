package com.example.bc_praca_x.custom_dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.UserSettingsManager;
import java.util.concurrent.atomic.AtomicReference;

public class FilterDialog extends Fragment {

    private String type;
    private RadioGroup show_radio_group, sort_radio_group;
    private String filter_tag, sort_tag;

    public FilterDialog() {
        // Required empty public constructor
    }

    public static FilterDialog newInstance(String type, String filter_tag, String sort_tag) {
        FilterDialog fragment = new FilterDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("filter_tag", filter_tag);
        args.putString("sort_tag", sort_tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString("type");
            filter_tag = getArguments().getString("filter_tag");
            sort_tag = getArguments().getString("sort_tag");
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_dialog, container, false);

        UserSettingsManager settings = new UserSettingsManager(requireContext());

        TextView filterTypeText = view.findViewById(R.id.filterTypeText);
        int resId = getResources().getIdentifier(type.equals("categories") ? "categories" : "packages", "string", getContext().getPackageName());
        if (resId != 0) filterTypeText.setText(resId);


        show_radio_group = view.findViewById(R.id.show_radio_group);
        sort_radio_group = view.findViewById(R.id.sort_radio_group);
        setTranslations(view);

        AtomicReference<String> filter_show_value = new AtomicReference<>(filter_tag);
        AtomicReference<String> filter_sort_value = new AtomicReference<>(sort_tag);

        if(filter_tag != null) {
            RadioButton show_all = view.findViewById(R.id.show_all);
            RadioButton show_favorite = view.findViewById(R.id.show_favorites);
            RadioButton show_non_empty = view.findViewById(R.id.show_non_empty);

            switch (filter_tag) {
                case "filter_type_favorites":
                    show_radio_group.check(show_favorite.getId());
                    break;
                case "filter_type_non_empty":
                    show_radio_group.check(show_non_empty.getId());
                    break;
                default:
                    show_radio_group.check(show_all.getId());
            }
        } else {
            show_radio_group.check(R.id.show_all);
            filter_show_value.set("filter_type_all");
        }

        if(sort_tag != null) {
            RadioButton sort_by_newest = view.findViewById(R.id.sort_by_newest);
            RadioButton sort_by_oldest = view.findViewById(R.id.sort_by_oldest);
            RadioButton sorc_asc = view.findViewById(R.id.sort_asc);
            RadioButton sort_desc = view.findViewById(R.id.sort_desc);

            switch (sort_tag) {
                case "filter_sort_newest":
                    sort_radio_group.check(sort_by_newest.getId());
                    break;
                case "filter_sort_oldest":
                    sort_radio_group.check(sort_by_oldest.getId());
                    break;
                case "filter_sort_asc":
                    sort_radio_group.check(sorc_asc.getId());
                    break;
                case "filter_sort_desc":
                    sort_radio_group.check(sort_desc.getId());
                    break;
                default:
                    sort_radio_group.check(sort_by_newest.getId());
            }
        } else{
            sort_radio_group.check(R.id.sort_by_newest);
            filter_sort_value.set("filter_sort_newest");
        }

        show_radio_group.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                filter_show_value.set(radioButton.getTag().toString());
            }
        });

        sort_radio_group.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            if (radioButton != null && radioButton.getTag() != null) {
                filter_sort_value.set(radioButton.getTag().toString());
            }
        });

        Button cancel = view.findViewById(R.id.cancelFilter);
        cancel.setOnClickListener(v -> {
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });


        Button applyFilter = view.findViewById(R.id.saveFilter);
        applyFilter.setOnClickListener(v -> {

            Bundle result = new Bundle();

            if(filter_sort_value.get() != null) {
                settings.saveSetting(type + "_sort_type", filter_sort_value.get());
                result.putString("sort", filter_sort_value.get());
            }

            if(filter_show_value.get() != null) {
                settings.saveSetting(type + "_filter_type", filter_show_value.get());
                result.putString("show", filter_show_value.get());
            }

            requireActivity().getSupportFragmentManager().setFragmentResult("categories_filter", result);
            DialogFragment parentDialog = (DialogFragment) getParentFragment();
            if (parentDialog != null) parentDialog.dismiss();
        });


        return view;
    }

    private void setTranslations(View view){
        RadioButton show_all = view.findViewById(R.id.show_all);

        if(type.equals("categories")) {
            show_all.setText(R.string.all_categories);
        } else {
            show_all.setText(R.string.all_packages);
        }

        RadioButton non_empty = view.findViewById(R.id.show_non_empty);
        if(type.equals("categories")) {
            non_empty.setText(R.string.non_empty_categories);
        } else {
            non_empty.setText(R.string.non_empty_packages);
        }
    }
}