package com.yao_guet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixiang on 13-6-27.
 */
public class DockView extends LinearLayout {
    private static final String TAG = "DockView";
    private DockAppItem appItem;

    private int[] DockId = {R.drawable.app__browser,R.drawable.app__dialer,R.drawable.app__mail,R.drawable.app__music};
    private String[] DockText = {"浏览器","拨号","邮件","音乐"};
    private List<View> mList = new ArrayList<View>();;


    public DockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    private void initView(Context context){
//        setBackgroundResource(R.drawable.dock_tablet_portrait);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1;



        for(int i = 0;i<4;i++){
            View v = LayoutInflater.from(context).inflate(R.layout.app_item_dock, null);
            appItem = new DockAppItem();
            appItem.mAppIcon = (ImageView)v.findViewById(R.id.ivAppIcon);
            appItem.mAppName = (TextView)v.findViewById(R.id.tvAppName);
            appItem.mDeleteIcon = (ImageView)v.findViewById(R.id.delete);
//            appItem.mBackView = (ImageView)v.findViewById(R.id.back);
            v.setTag(appItem);
            appItem.mAppIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG,"appIcon");
                }
            });
            Bitmap bm = BitmapFactory.decodeResource(getResources(),DockId[i]);
            Bitmap bm1 = setShadow(bm);
            bm1 = setAlpha(bm1,50);
            Log.e(TAG,"bm ="+bm.getWidth());
//            appItem.mBackView.setImageBitmap(bm1);
            appItem.mAppIcon.setImageResource(DockId[i]);
            appItem.mAppName.setText(DockText[i]);
            mList.add(v);
        }
        for (int j =0;j<4;j++)
            addView(mList.get(j),lp);



    }

    public static Bitmap setAlpha(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());
        number = number * 255 / 100;
        double round = (double)number/(double)(argb.length);
        System.out.println(round+ "  l="+argb.length +" n="+number);
        for (int i = 0; i < argb.length; i++) {
            if(number-i*round>10){
                argb[i] = ((int)(number-i*round) << 24) | (argb[i] & 0x00FFFFFF);
                continue;
            }
            else{
                argb[i] = (10 << 24) | (argb[i] & 0x00FFFFFF);
                continue;
            }

        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    public static Bitmap setShadow(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap shadowImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);
        return shadowImage;
    }

    private class DockAppItem {
        ImageView mAppIcon;
        TextView mAppName;
        ImageView mDeleteIcon;
        ImageView mBackView;
    }
}
