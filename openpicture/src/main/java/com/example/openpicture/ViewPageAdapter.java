package com.example.openpicture;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.openpicture.GalleryImages.ImageFragment;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentPagerAdapter {
    ArrayList<String> images=new ArrayList<>();
    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior,ArrayList<String> myImage) {
        super(fm, behavior);
        images=myImage;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ImageFragment(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }


}
