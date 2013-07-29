package com.yao_guet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 13-6-26.
 */
public class Indicator extends LinearLayout implements DotIndicator {
    private final static String TAG = "Indicator";
    private Context mContext;
    private int pageCount;
    private ScrollLayout mScrollLayout;
    private List<ImageView> mImageViews = new ArrayList<ImageView>();
    private List<ResolveInfo> list = new ArrayList<ResolveInfo>();
    private final float APP_PAGE_SIZE = 16.0f;
    private int currPage;


    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getPageCount();
        setdotImageView();
        Log.e(TAG,"Indicator001");
    }

    public void setdotImageView(){
getPageCount();
        for(int k = 0;k<pageCount;k++)
        {
            ImageView dotview = new ImageView(mContext);

            if(mScrollLayout == null)
                currPage = 0;
            else
                currPage = mScrollLayout.getCurScreen();
            if(k == currPage)
                dotview.setImageResource(R.drawable.ic_theme_selected);
            else
                dotview.setImageResource(R.drawable.ic_theme_not_selected);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-2,-2);
            dotview.setPadding(0,0,5,0);
            mImageViews.add(k,dotview);
            this.addView(dotview, k, lp);
            Log.e(TAG,"dotview= "+dotview);

        }


    }

    public void snapDotImageView(int page){
        Log.e(TAG, "snapDotImageView");
        for(int i = 0;i<pageCount;i++){
        if(i == page)
            mImageViews.get(i).setImageResource(R.drawable.ic_theme_selected);
        else
            mImageViews.get(i).setImageResource(R.drawable.ic_theme_not_selected);
        }
        invalidate();
    }


    public void updateDotImageView(boolean addview0){
        Log.e(TAG, "updateDotImageView");
        getPageCount();
        ImageView dotview = new ImageView(mContext);
        dotview.setImageResource(R.drawable.ic_theme_selected);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-2,-2);
        dotview.setPadding(0,0,5,0);
       if(addview0){
           mImageViews.add(pageCount-1,dotview);
           for(int i = 0;i<pageCount-1;i++)
               mImageViews.get(i).setImageResource(R.drawable.ic_theme_not_selected);
           this.addView(dotview,pageCount-1,lp);
       }
        else
           mImageViews.remove(pageCount-1);
//        invalidate();
    }

    public void getPageCount(){
        final PackageManager packageManager = mContext.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        list = packageManager.queryIntentActivities(mainIntent, 0);
        pageCount = (int)Math.ceil(list.size()/APP_PAGE_SIZE);
    }

    public void set(ScrollLayout sl){
        mScrollLayout = sl;
    }
    @Override
    public void update(int page) {
//        updateDotImageView(page);
    }
}
