package com.akhil.appstreet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.akhil.appstreet.R;
import com.akhil.appstreet.adapters.ViewPagerAdapter;
import com.akhil.appstreet.constants.Constants;
import com.akhil.appstreet.model.FlickrImage;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity {

    final String TAG = this.getClass().getSimpleName();
    ArrayList<FlickrImage> dataList;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        try {
            Bundle bundle = getIntent().getBundleExtra("bundle");

            dataList =  bundle.getParcelableArrayList("dataList");
            Log.d(TAG, "intent data dataList.size() = "+dataList.size() );
            int position = bundle.getInt("position");

            if( dataList!=null && dataList.size()>0 ){
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, dataList);
                viewPager.setAdapter(viewPagerAdapter);
                viewPager.setCurrentItem(position);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.CURRENT_IMAGE_POSITION, viewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        finish();
    }
}
