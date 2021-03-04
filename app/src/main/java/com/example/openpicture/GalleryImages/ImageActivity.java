package com.example.openpicture.GalleryImages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.openpicture.R;
import com.example.openpicture.ViewPageAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList<String> images;
    ProgressBar progressBar;

    private TextView txtProgress;
    private int pStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        viewPager = findViewById(R.id.viewPager);
       /* progressBar = findViewById(R.id.progressBar);
        Drawable draw= getDrawable(R.drawable.custom_progressbar);
        progressBar.setProgressDrawable(draw);*/
        getBundle();

    }

    private void getBundle() {


        if (getIntent().hasExtra("picture")) {
            images = getIntent().getStringArrayListExtra("picture");
/*
            txtProgress = (TextView) findViewById(R.id.txtProgress);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pStatus <= 100) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(pStatus);
                                txtProgress.setText(pStatus + " %");
                            }
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        pStatus++;
                    }
                }
            }).start();*/
            viewPager.setOffscreenPageLimit(images.size());
            viewPager.setAdapter(new ViewPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, images));
            viewPager.setPageTransformer(true, new FadePageTransformer());

        }
    }


    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setAlpha(1 - Math.abs(position));
            if (position < 0) {
                view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
            } else if (position > 0) {
                view.setScrollX((int) ((float) (view.getWidth()) * position));
            } else {
                view.setScrollX(0);
            }
        }
    }

    }









