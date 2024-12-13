package com.example.myapplication.present.ui.fragment.setting;

import static com.example.myapplication.app.Constants.BASE_YEAR;
import static com.example.myapplication.app.Constants.KEY_SP_ST_CATEGORY;
import static com.example.myapplication.app.Constants.KEY_SP_ST_YEAR;
import static com.example.myapplication.app.Constants.POPULAR;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.myapplication.R;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragmentCompat {
    private MyViewModel myViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        requireActivity().setTheme(R.style.PreferenceTheme_Light);
        myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
        loadCategorySummary();
        setupYearPickerPreference();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myViewModel.getSettingHasChanged().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Preference categoryPreference = findPreference(KEY_SP_ST_CATEGORY);
                    if (categoryPreference != null) {
                        String category = getPreferenceScreen().getSharedPreferences().getString(KEY_SP_ST_CATEGORY, POPULAR);
                        categoryPreference.setSummary(getDisplayValueForCategory(requireContext(), category));
                    }
                    myViewModel.setSettingHasChanged(false);
                }
            }
        });
    }
    private void loadCategorySummary(){
        Preference categoryPreference = findPreference(KEY_SP_ST_CATEGORY);
        if (categoryPreference != null) {
            String category = getPreferenceScreen().getSharedPreferences().getString(KEY_SP_ST_CATEGORY, POPULAR);
            categoryPreference.setSummary(getDisplayValueForCategory(requireContext(), category));
        }
    }

    private void setupYearPickerPreference() {
        Preference yearPickerPreference = findPreference(KEY_SP_ST_YEAR);

        if (yearPickerPreference != null) {
            String savedYear = getPreferenceScreen().getSharedPreferences().getString(KEY_SP_ST_YEAR, "1970");
            yearPickerPreference.setSummary(savedYear);

            yearPickerPreference.setOnPreferenceClickListener(preference -> {
                showYearPickerDialog(yearPickerPreference, Integer.parseInt(savedYear));
                return true;
            });
        }
    }

    private void showYearPickerDialog(Preference yearPickerPreference, int savedYear) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int startYear = BASE_YEAR;

        Integer[] years = new Integer[currentYear - startYear + 1];
        for (int i = 0; i <= currentYear - startYear; i++) {
            years[i] = startYear + i;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_year_picker, null);
        Spinner spinner = dialogView.findViewById(R.id.year_spinner);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(savedYear - startYear);

        new android.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedYear = (int) spinner.getSelectedItem();
                    yearPickerPreference.getSharedPreferences()
                            .edit()
                            .putString(KEY_SP_ST_YEAR, String.valueOf(selectedYear))
                            .apply();
                    yearPickerPreference.setSummary(String.valueOf(selectedYear));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener =
            (sharedPreferences, key) -> myViewModel.setSettingHasChanged(true);

    public String getDisplayValueForCategory(Context context, String value) {
        String[] options = context.getResources().getStringArray(R.array.option_filter);
        String[] values = context.getResources().getStringArray(R.array.values_filter);

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return options[i];
            }
        }

        return null;
    }
}
