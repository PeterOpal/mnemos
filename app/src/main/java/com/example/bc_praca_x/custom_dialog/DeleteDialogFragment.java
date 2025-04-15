package com.example.bc_praca_x.custom_dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bc_praca_x.R;
import com.example.bc_praca_x.database.entity.Media;
import com.example.bc_praca_x.database.viewmodel.CardPackViewModal;
import com.example.bc_praca_x.database.viewmodel.CategoryViewModel;
import com.example.bc_praca_x.database.viewmodel.MediaViewModel;
import com.example.bc_praca_x.helpers.ImageSaver;

public class DeleteDialogFragment extends Fragment {
    // NOTE: This fragment can delete categories and packages and also reset the app - it is solved with a paramter "type"
    private String name;
    private long id;
    private String type;

    public static DeleteDialogFragment newInstance(String name, long id, String type) {
        DeleteDialogFragment fragment = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putLong("id", id);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            id = getArguments().getLong("id");
            type = getArguments().getString("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_dialog, container, false);

        Button deleteButton = view.findViewById(R.id.resumeButton);
        TextView deleteType = view.findViewById(R.id.deleteType);

        TextView deleteDesc = view.findViewById(R.id.deleteDescription);

        if (type.equals("category")) {
            CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
            deleteType.setText(getString(R.string.Delete) + " " +getString(R.string.delete_category_dialog_title));
            deleteDesc.setText(getString(R.string.delete_category_message, name));
            deleteButton.setOnClickListener(v -> {
                categoryViewModel.delete(id);
                closeFragment();
            });
        } else if (type.equals("package")) {
            CardPackViewModal packageViewModel = new ViewModelProvider(this).get(CardPackViewModal.class);
            deleteDesc.setText(getString(R.string.delete_package_message, name));
            deleteType.setText(getString(R.string.Delete) + " " +getString(R.string.delete_package_dialog_title));
            deleteButton.setOnClickListener(v -> {
                packageViewModel.deleteCardPackWithTasks(id);
                closeFragment();
            });
        } else if (type.equals("reset")) {
            deleteDesc.setText(getString(R.string.reset_app_description));
            deleteType.setText(getString(R.string.reset_app_title));
            deleteButton.setOnClickListener(v -> {
                resetApp();
                closeFragment();
            });
        } else if (type.equals("deleteFlashcards")){
            deleteDesc.setText(getString(R.string.delete_flashcards_description));
            deleteType.setText(getString(R.string.reset_app_title));
            deleteButton.setOnClickListener(v -> {
                deleteAllFlashcards();
                closeFragment();
            });
        }

        Button cancel = view.findViewById(R.id.finishLearningSessionButton);
        cancel.setOnClickListener( v -> closeFragment() );

        return view;
    }

    public void resetApp() { // including DB, cache, prefs
        try {
            Log.d("ResetApp", "Clearing app data");
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + requireContext().getPackageName());
        } catch (Exception e) {
            Log.e("ResetApp", "Failed to clear app data", e);
            e.printStackTrace();
        }
    }

    private void closeFragment(){
        DialogFragment parentDialog = (DialogFragment) getParentFragment();
        if (parentDialog != null) parentDialog.dismiss();
    }

    private void deleteAllFlashcards() {
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.deleteAll();

        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.deleteAllMedia();

        ImageSaver imageSaver = new ImageSaver(requireContext());
        imageSaver.deleteAllSavedImages();

        Toast.makeText(requireContext(), getString(R.string.flashcards_deleted), Toast.LENGTH_SHORT).show();
    }
}