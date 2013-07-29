package com.yao_guet;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter {
//	private List<ResolveInfo> mList;
    private List<ContentValues> mList;
	private Context mContext;
	public static final int APP_PAGE_SIZE = 16;
	private PackageManager pm;
//    private List<ResolveInfo> lstDate;
    private List<ContentValues> lstDate;
    private boolean ShowItem = true;
    private int holdPosition;
    private boolean isChanged;
    private String packageName;
    private  static String[] specialApp = {"com.android.browser", "com.android.contacts","com.android.email","com.android.music","com.tyd.calculator",
                                           "com.android.deskclock","com.android.gallery3d","com.android.mms","com.android.settings"};
    private static int[] drawableid = {R.drawable.app__browser,R.drawable.app__contacts,R.drawable.app__mail,R.drawable.app__music,R.drawable.app__calculator,
                                       R.drawable.app__clock,R.drawable.app__gallery,R.drawable.app__text,R.drawable.app__settings};
    private int m_startPosition;
//    private final static String DIALER = "com.android.contacts";
//    private final static String MAIL = "com.android.email";
//    private final static String MUSIC = "com.android.music";


//    public AppAdapter(Context context, List<ResolveInfo> list, int page) {
//		mContext = context;
//		pm = context.getPackageManager();
//
//		mList = new ArrayList<ResolveInfo>();
//		int i = page * APP_PAGE_SIZE;
//		int iEnd = i+APP_PAGE_SIZE;
//		while ((i<list.size()) && (i<iEnd)) {
//			mList.add(list.get(i));
//			i++;
//		}
//        lstDate = mList;
//	}

    public AppAdapter(Context context, List<ContentValues> list, int page) {
        mContext = context;
        pm = context.getPackageManager();

        mList = new ArrayList<ContentValues>();
        int i = page * APP_PAGE_SIZE;
        int iEnd = i+APP_PAGE_SIZE;
        while ((i<list.size()) && (i<iEnd)) {
            mList.add(list.get(i));
            i++;
        }
        lstDate = mList;
    }
    public void setDataChanged(String name, AllAppProvider provider){
        final PackageManager packageManager = mContext.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> app = packageManager.queryIntentActivities(mainIntent, 0);
        ResolveInfo info = app.get(app.size()-1);
        Bitmap bmp = (((BitmapDrawable)info.loadIcon(pm)).getBitmap());
        int size = bmp.getWidth()*bmp.getHeight()*4;
        ByteArrayOutputStream os = new ByteArrayOutputStream(size);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        ContentValues values = new ContentValues();
        values.put("title",info.loadLabel(pm).toString());
        values.put("icon",os.toByteArray());
        values.put("package",info.activityInfo.packageName);
        Uri newAddUri =provider.insert(AllAppProvider.CONTENT_URI,values);
//        for(int i = 0;i<app.size();i++){
//            if(name.equals(app.get(i).activityInfo.packageName)){
//               mList.remove(i);
//            }
//        }
        if(mList.size()<APP_PAGE_SIZE)
//        mList.add(app.get(app.size()-1));
        mList.add(values);
        Log.e("TAG","lixiang ="+mList);
        if(mList.size()>1)
//       if(mList.get(mList.size()-1).activityInfo.packageName.equals(mList.get(mList.size()-2).activityInfo.packageName))
//       {
//           mList.remove(mList.size()-1);
//           Log.e("TAG","lixiang");
//       }
        notifyDataSetChanged();

    }

//    public void setDataDelete(int page){
//        final PackageManager packageManager = mContext.getPackageManager();
//        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        final List<ResolveInfo> list = packageManager.queryIntentActivities(mainIntent, 0);
//        if(mList.size()<APP_PAGE_SIZE)
//        mList = new ArrayList<ResolveInfo>();
//        int i = page * APP_PAGE_SIZE;
//        int iEnd = i+APP_PAGE_SIZE;
//        while ((i<list.size()) && (i<iEnd)) {
//            mList.add(list.get(i));
//            i++;
//        }
////        mList.remove(10);
//        notifyDataSetChanged();
//
//    }

    public void exchange(int startPosition, int endPosition, int startposition) {
        m_startPosition = startposition;
        System.out.println(startPosition + "--" + endPosition);
        holdPosition = endPosition;
        Object startObject = getItem(startPosition);

        Log.d("ON","startPostion ==== " + startPosition );
        Log.d("ON","endPosition ==== " + endPosition );
        if(startPosition < endPosition){
            lstDate.add(endPosition + 1,  (ContentValues)startObject);
            lstDate.remove(startPosition);
        }else{
            lstDate.add(endPosition,(ContentValues)startObject);
            lstDate.remove(startPosition + 1);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

    public Object getList() {
        // TODO Auto-generated method stub
        return mList;
    }

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

    public void showDropItem(boolean showItem, int x){
        this.ShowItem = showItem;
        m_startPosition = x;
    }

    public void stopAnimation(){
        AllAppsGridView.LongClickMode = false;
        notifyDataSetChanged();
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ContentValues appInfo = mList.get(position);
//        packageName = appInfo.activityInfo.packageName;
        packageName = appInfo.getAsString("title");
        Log.e("getview","getview---position= "+position);
		AppItem appItem;
        int id;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.app_item, null);
			
			appItem = new AppItem();
			appItem.mAppIcon = (ImageView)v.findViewById(R.id.ivAppIcon);
			appItem.mAppName = (TextView)v.findViewById(R.id.tvAppName);
            appItem.mDeleteIcon = (ImageView)v.findViewById(R.id.delete);
			
			v.setTag(appItem);
			convertView = v;

		} else {
			appItem = (AppItem)convertView.getTag();
		}

        // set the app name
//        TextPaint tp = appItem.mAppName.getPaint();
//        tp.setFakeBoldText(true);
//        appItem.mAppName.setText(appInfo.loadLabel(pm));
        appItem.mAppName.setText(appInfo.getAsString("title"));
        // set the icon
//        id = matchName(appInfo.activityInfo.packageName);
//        if(id != 0)
//            appItem.mAppIcon.setImageResource(id);
//        else
        byte[] data = appInfo.getAsByteArray("icon");
        Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length,null);
        appItem.mAppIcon.setImageBitmap(bmp);
//		    appItem.mAppIcon.setImageDrawable(appInfo.loadIcon(pm));
//        if(appInfo.activityInfo.packageName.equals("com.android.contacts")&&((String)appItem.mAppName.getText()).equals("拨号"))
//            appItem.mAppIcon.setImageResource(R.drawable.app__dialer);


        Log.e("TAG","LongClickMode ="+AllAppsGridView.LongClickMode);
        if(AllAppsGridView.LongClickMode)
        getStartOrStopAnim(true,convertView,appItem);
        else
            getStartOrStopAnim(false,convertView,appItem);
        if (true){
            if (position == m_startPosition){
                if(!ShowItem){
                    Log.e("TAG","ShowItem ="+((AppItem)convertView.getTag()).mAppName.getText());
                    convertView.clearAnimation();
                    convertView.setVisibility(View.INVISIBLE);
//                    ShowItem = true ;
                }


            }
        }

        if( AllAppsGridView.LongClickMode == false&&convertView.getVisibility() == View.INVISIBLE)
            convertView.setVisibility(View.VISIBLE);
		return convertView;
	}

    public void getStartOrStopAnim(boolean start,View view,AppItem appitem){
        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.anim);
        LinearInterpolator lin = new LinearInterpolator();
        shake.setInterpolator(lin);
//        RotateAnimation shake = new RotateAnimation(-2,2,0.5f*view.getWidth(),0.5f*view.getHeight());
//        shake.setDuration((int)Math.random()*100+50);
//        shake.setRepeatCount(RotateAnimation.INFINITE);
//        shake.setRepeatMode(RotateAnimation.REVERSE);
//        shake.reset();
//        shake.setFillAfter(true);
        if(start&&view != null){
        view.startAnimation(shake);
            appitem.mDeleteIcon.setVisibility(View.VISIBLE);
        }
        if(!start&&view != null){
            view.clearAnimation();
            appitem.mDeleteIcon.setVisibility(View.INVISIBLE);
            AllAppsGridView.LongClickMode = false;
        }
    }
    private int matchName(String name){
//        int id;
        for(int i =0;i<specialApp.length;i++){
            if(name.equals(specialApp[i])){
                return drawableid[i];
            }
        }
        return 0;
    }

	class AppItem {
		ImageView mAppIcon;
		TextView mAppName;
        ImageView mDeleteIcon;
	}
}
