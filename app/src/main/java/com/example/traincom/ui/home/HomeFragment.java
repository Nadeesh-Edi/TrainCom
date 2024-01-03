package com.example.traincom.ui.home;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.traincom.CreateBooking;
import com.example.traincom.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {
    Button createBtn;
    ImageView mainImg;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createBtn = binding.makeReservbutton;

        mainImg = binding.homeImage;
        AssetManager assetManager = requireActivity().getAssets();

        try {
            InputStream ims = assetManager.open("srilankatrain.jpg");
            Drawable d = Drawable.createFromStream(ims, null);
            mainImg.setImageDrawable(d);
        } catch (IOException e) {
            
        }

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), CreateBooking.class);
                startActivity(i);
            }
        });

//        final TextView textView = binding.searchSection;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}