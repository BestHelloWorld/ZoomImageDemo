package com.example.mitnick.zoomimageviewdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
    private ZoomImageView mZoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZoomImageView = findViewById(R.id.zoom_image);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mZoomImageView.setBitmap(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher_round));
    }
}
