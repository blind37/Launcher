package com.yao_guet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.blahti.example.drag.DragController;

/**
 */
public class AllAppList extends Activity implements AllAppsGridView.AllView {
	private static final String TAG = "ScrollLayoutTest";
    private static final String WHERE = "";
	private ScrollLayout mScrollLayout;
    private Indicator dot;
	private static final float APP_PAGE_SIZE = 16.0f;
	private Context mContext;
    private AppAdapter adapter;
    private List<ResolveInfo> apps;
    private List<ContentValues> contentlist = new ArrayList<ContentValues>();
    private AllAppsGridView apppage;
    private AllAppProvider provider;

    //    private GridView mView;
    private int pageCount;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                getPageCount();
                Log.e(TAG,"intent= "+intent);
                String packgename = intent.getDataString().substring(8);

                if(apps.size()==((pageCount-1)*APP_PAGE_SIZE+1)){
                 addChildView();
                 mScrollLayout.setToScreen(pageCount);
                 Log.e(TAG,"addchildview");
                }
                adapter.setDataChanged(packgename,provider);
//                String packgename_insert = intent.getDataString().substring(8);
//                final PackageManager packageManager = getPackageManager();
//                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                apps = packageManager.queryIntentActivities(mainIntent, 0);
//                ResolveInfo info_insert = apps.get(apps.size()-1);
//                insertData(info_insert);
                mScrollLayout.invalidate();//log6
            }

            if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
//                int mCurrentPage = mScrollLayout.getCurScreen();
//                adapter.setDataDelete(mCurrentPage);
//                if(apps.size()==((pageCount-1)*APP_PAGE_SIZE))
//                    dot.updateDotImageView(false);
                try {
                    for(int i = 0;i<mScrollLayout.getChildCount();i++){
                        AllAppsGridView gridView = (AllAppsGridView)mScrollLayout.getChildAt(i);
                        AppAdapter m_adpater = (AppAdapter)gridView.getAdapter();
                        Log.e(TAG,"getView---001");
                        m_adpater.stopAnimation();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dot.set(mScrollLayout);
                dot.removeAllViews();
                mScrollLayout.removeAllViews();
//                provider.delete(AllAppProvider.CONTENT_URI,"");
                String packgename = intent.getDataString().substring(8);
                String[] strs = new String[]{
                        packgename
                };
//                provider.delete(AllAppProvider.CONTENT_URI,"package=?",strs);
                getContentResolver().delete(AllAppProvider.CONTENT_URI,"package=?",strs);
                Log.e(TAG, "remove = " + mScrollLayout.getChildCount());
//                loadAllapp();
                initViews();
                mScrollLayout.setToScreen(pageCount);
                dot.setdotImageView();


            }

        }

    };
    private IntentFilter filter = new IntentFilter();
    private ContentObserver mObserver = new ChangeObserver();
    private PackageManager pm;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pm = getPackageManager();
        provider = new AllAppProvider();

//       loadAllapp();
       	mContext = this;
		setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        mScrollLayout = (ScrollLayout)findViewById(R.id.ScrollLayoutTest);
        dot = (Indicator)findViewById(R.id.dot);
        loadAllapp();
		initViews();
//        dot.set(mScrollLayout,pageCount);
        mScrollLayout.set(dot);
        registerContentObservers();

	}

    private void loadAllapp(){
        Cursor mCursor = getContentResolver().query(AllAppProvider.CONTENT_URI,null,null,null,null);
        if(mCursor != null&&mCursor.getCount()==0)
            insert();
        mCursor.close();
        Cursor cursor = getContentResolver().query(AllAppProvider.CONTENT_URI,null,null,null,null);
        Log.e(TAG,"cursor.count = "+cursor.getCount());
        if(cursor != null&&cursor.getCount()>0){
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String packageName = cursor.getString(cursor.getColumnIndexOrThrow("package"));
                int id  = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow("icon"));
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length,null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("title",name);
                contentValues.put("id",id);
                contentValues.put("icon",data);
                contentValues.put("package",packageName);
                contentlist.add(contentValues);
            }
            Log.e(TAG,"contentlist= "+contentlist);

        }
//        else
//            insert();

        cursor.close();
    }

    private void insert() {

        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        for(int i = 0 ;i<apps.size();i++){
            ResolveInfo appInfo = apps.get(i);
            insertData(appInfo);
        }
        Log.e(TAG,"cursor insert");

    }

    private void insertData(ResolveInfo appInfo) {
        Bitmap bmp = (((BitmapDrawable)appInfo.loadIcon(pm)).getBitmap());
        int size = bmp.getWidth()*bmp.getHeight()*4;
        ByteArrayOutputStream os = new ByteArrayOutputStream(size);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        ContentValues values = new ContentValues();
        values.put("title",appInfo.loadLabel(pm).toString());
        values.put("icon",os.toByteArray());
        values.put("package",appInfo.activityInfo.packageName);
        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri newAddUri =this.getContentResolver().insert(AllAppProvider.CONTENT_URI,values);
//            bmp.recycle();
    }

    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(AllAppProvider.CONTENT_URI,true,mObserver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(broadcastReceiver,filter);
        Log.e(TAG,"broadcastReceiver= "+broadcastReceiver);
    }

    @Override
    protected void onPause() {
        for(int i = 0;i<mScrollLayout.getChildCount();i++){
            AllAppsGridView gridView = (AllAppsGridView)mScrollLayout.getChildAt(i);
            AppAdapter m_adpater = (AppAdapter)gridView.getAdapter();
            Log.e(TAG,"getView---001");
            m_adpater.stopAnimation();
        }
        super.onPause();
    }

    public void getPageCount(){
        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        pageCount = (int)Math.ceil(apps.size()/APP_PAGE_SIZE);
    }
    public void addChildView(){
        apppage= new AllAppsGridView(this,mScrollLayout);
        final PackageManager packageManager = getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = packageManager.queryIntentActivities(mainIntent, 0);
        List<ContentValues> list = new ArrayList<ContentValues>();
        list.add(contentlist.get(contentlist.size()-1));
        adapter = new AppAdapter(this,list,pageCount);
        apppage.setAdapter(adapter);
        apppage.setNumColumns(4);
//        apppage.setOnItemClickListener(listener);
//        apppage.setOnItemLongClickListener(listener0);
        mScrollLayout.addView(apppage);
        getPageCount();
        dot.updateDotImageView(true);

    }

	public void initViews() {
		final PackageManager packageManager = getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // get all apps 
         apps = packageManager.queryIntentActivities(mainIntent, 0);

        // the total pages
        final int PageCount = (int)Math.ceil(apps.size()/APP_PAGE_SIZE);
        Log.e(TAG, "size:"+apps.size()+" page:"+PageCount);
        for (int i=0; i<PageCount; i++) {
             apppage = new AllAppsGridView(this,mScrollLayout);

        	
        	// get the "i" page data
//            adapter = new AppAdapter(this, apps, i);
            adapter = new AppAdapter(this,contentlist,i);
        	apppage.setAdapter(adapter);

        	apppage.setNumColumns(4);
            apppage.invalidate();
        	mScrollLayout.addView(apppage);
            Log.e(TAG,"adapter = "+adapter);
            Log.e(TAG,"mScrollLayout = "+mScrollLayout.getChildAt(i));
        }

	}



    public OnItemLongClickListener listener0 = new OnItemLongClickListener() {


        public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Log.e("AllAppList", "OnItemLongClickListener");
            // TODO Auto-generated method stub
            ResolveInfo app = (ResolveInfo) parent
                    .getItemAtPosition(position);
            //app = new ApplicationInfo(app);
Log.e("Gomtel","app = "+app);
//            mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
           return true;
        }


    };

	public OnItemClickListener listener = new OnItemClickListener() {
	

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.e("AllAppList", "OnItemClickListener");
			// TODO Auto-generated method stub
			ResolveInfo appInfo = (ResolveInfo)parent.getItemAtPosition(position);
			Intent mainIntent = mContext.getPackageManager()
				.getLaunchIntentForPackage(appInfo.activityInfo.packageName);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				// launcher the package
				mContext.startActivity(mainIntent);
			} catch (ActivityNotFoundException noFound) {
				Toast.makeText(mContext, "Package not found!", Toast.LENGTH_SHORT).show();
			}
		}
		
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
        provider.deletTable(provider);

        for(int i = 0;i<mScrollLayout.getChildCount();i++){
            AllAppsGridView gridView = (AllAppsGridView)mScrollLayout.getChildAt(i);
            AppAdapter m_adpater = (AppAdapter)gridView.getAdapter();
//            Cursor cursor = getContentResolver().query(AllAppProvider.CONTENT_URI,null,null,null,null);
//            Log.e(TAG,"list = "+m_adpater.getList());
            List<ContentValues> list = (List<ContentValues>)m_adpater.getList();
            Log.e(TAG,"list = "+list);

            for(int j = 0;j<list.size();j++){
//                ContentValues values = ((ContentValues)list.get(i));
                String name = ((ContentValues)list.get(j)).getAsString("title");
                byte[] data = ((ContentValues)list.get(j)).getAsByteArray("icon");
                ContentValues values = new ContentValues();


                values.put("title",name);
                values.put("icon",data);
                Log.e(TAG,"values= "+values);
                Uri newAddUri =this.getContentResolver().insert(AllAppProvider.CONTENT_URI,values);
//                Log.e(TAG,"newAddUri ="+newAddUri);
//                Cursor cursor = getContentResolver().query(AllAppProvider.CONTENT_URI,null,null,null,null);
//                Log.e(TAG,"cursor.getCount() = "+cursor.getCount());

//                cursor.getCount();
//               Log.e(TAG," m_adpater = "+m_adpater.getItem(j));
//                cursor.moveToNext();
//                m_adpater.getItem()
//                Bitmap bmp = (((BitmapDrawable)appInfo.loadIcon(pm)).getBitmap());
//                int size = bmp.getWidth()*bmp.getHeight()*4;
//                ByteArrayOutputStream os = new ByteArrayOutputStream(size);
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
//                ContentValues values = new ContentValues();
//                values.put("title",appInfo.loadLabel(pm).toString());
//                values.put("icon",os.toByteArray());


            }

        }
		android.os.Process.killProcess(android.os.Process.myPid());

        unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
            for(int i = 0;i<mScrollLayout.getChildCount();i++){
                AllAppsGridView gridView = (AllAppsGridView)mScrollLayout.getChildAt(i);
                AppAdapter m_adpater = (AppAdapter)gridView.getAdapter();
                m_adpater.getCount();
                Log.e(TAG,"getView---001");
                m_adpater.stopAnimation();
            }
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    public ScrollLayout getAllView() {

        return mScrollLayout;
    }

private class ChangeObserver extends ContentObserver{

    public ChangeObserver() {
        super(new Handler());
    }
    @Override
    public void onChange(boolean selfChange) {
        onChanged();
    }
}

    private void onChanged() {
        Log.e(TAG,"Database has changed !");
    }
}
