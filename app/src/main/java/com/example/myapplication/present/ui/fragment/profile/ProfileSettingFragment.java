package com.example.myapplication.present.ui.fragment.profile;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProfileEditBinding;
import com.example.myapplication.domain.model.Result;
import com.example.myapplication.domain.model.User;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ProfileSettingFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    private static final int CAMERA_STORAGE_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int AVATAR_SIZE = 300;
    private User u;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private MyViewModel myViewModel;
    private FragmentProfileEditBinding binding;
    private  NavController navController;

    public ProfileSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileEditBinding.inflate(inflater,container,false);
        registerActivityLaunchers();
        // init data
        myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);

        myViewModel.getProfileUseCase().loadUserData(getViewLifecycleOwner()).observe(getViewLifecycleOwner(), new Observer<Result<User>>() {
            @Override
            public void onChanged(Result<User> userResult) {
                if(userResult.status == Result.Status.SUCCESS){
                    u = userResult.data;
                    initUI();
                }else{
                    Toast.makeText(getContext(),userResult.message,Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.image_edit) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.edit_image_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.camera_menu) {
            openCamera();
            return true;
        } else if (item.getItemId() == R.id.gallery_menu) {
            openGallery();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }


    private void initUI(){
        binding.setUser(u);
        navController = NavHostFragment.findNavController(this);
        // check permission for camera and gallery
        checkPermissions();
        // set action for button
        binding.cancelButtonEdit.setOnClickListener(v ->{
            finish();
        });
        binding.doneButtonEdit.findViewById(R.id.done_button_edit).setOnClickListener(v -> {
            updateUserProfile();
        });
        // on change avatar
        registerForContextMenu(binding.imageEdit);
        binding.imageEdit.setOnLongClickListener(v -> {
            requireActivity().openContextMenu(binding.imageEdit);
            return true;
        });

        // on change dob
        Calendar calendar = Calendar.getInstance();
        binding.dateOfBirthEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, yearSelected, monthSelected, daySelected) -> {
                        calendar.set(Calendar.YEAR, yearSelected);
                        calendar.set(Calendar.MONTH, monthSelected);
                        calendar.set(Calendar.DAY_OF_MONTH, daySelected);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        binding.dateOfBirthEditText.setText(selectedDate);
                        u.setDob(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),  calendar.get(Calendar.MONTH),  calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
        // on change sex
        binding.sexRadioEdit.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male_radio_edit) {
                u.setSex(1); // Male
            } else if (checkedId == R.id.female_radio_edit) {
                u.setSex(0); // Female
            }
        });
        // add call back for back press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack(R.id.listMovieFragment,false);
                myViewModel.setToolBarForHome(true);
            }
        });

    }
    private void registerActivityLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        assert bitmap != null;
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, AVATAR_SIZE, AVATAR_SIZE, true);
                        binding.imageEdit.setImageBitmap(resizedBitmap);
                        u.setImage(resizedBitmap);

                    } else {
                        Log.d(TAG, "Camera result not OK");
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, AVATAR_SIZE, AVATAR_SIZE, true);
                            binding.imageEdit.setImageBitmap(resizedBitmap);
                            u.setImage(resizedBitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    private void updateUserProfile() {
        if (u.getImage() == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
            u.setImage(bitmap);
        }

        try {
            // Show ProgressBar
            binding.progressBar.setVisibility(View.VISIBLE);
            User resultUser = binding.getUser();
            resultUser.setImage(u.getImage());
            resultUser.setEmail(u.getEmail());
            resultUser.validateUserData();
            if(!resultUser.equals(u)){
                myViewModel.getProfileUseCase().saveUserData(u,getViewLifecycleOwner()).observe(getViewLifecycleOwner(), new Observer<Result<Boolean>>() {
                    @Override
                    public void onChanged(Result<Boolean> booleanResult) {
                        Toast.makeText(getContext(),booleanResult.message,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else{
                Toast.makeText(getContext(),"Not Change",Toast.LENGTH_SHORT).show();
                finish();
            }


        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("LogLog", e.toString());
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    public void finish(){
        myViewModel.setToolBarForHome(true);
        navController.popBackStack(R.id.listMovieFragment,false);
        binding.progressBar.setVisibility(View.GONE);
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void checkPermissions() {
        String cameraPermission = Manifest.permission.CAMERA;
        String storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{cameraPermission, storagePermission}, CAMERA_STORAGE_PERMISSION_CODE);
        }
    }


}