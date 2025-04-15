package com.example.bc_praca_x;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DatePickerListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (getArguments() != null && getArguments().containsKey("selectedDate")) {
            String[] date = getArguments().getString("selectedDate").split("-");
            year = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]) - 1;
            day = Integer.parseInt(date[2]);
        }

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (listener != null) {
            listener.onDateSelected(year, month, day);
        }
    }

    public interface DatePickerListener {
        void onDateSelected(int year, int month, int day);
    }
}