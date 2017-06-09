package com.example.mdxj;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.mdxj.model.CatagoryOne;
import com.example.mdxj.model.CatagoryThree;
import com.example.mdxj.model.CatagoryTwo;
import com.example.mdxj.model.SettingData;
import com.example.mdxj.util.DBCopyUtil;
import com.example.mdxj.util.StorageUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

public class DwpcApplication extends Application {

    public static Context applicationContext;
    private static DwpcApplication instance;

    private SettingData sd = new SettingData();
    private List<CatagoryOne> cgList = new ArrayList<CatagoryOne>();
    private List<CatagoryOne> cgList2 = new ArrayList<CatagoryOne>();
    private FinalDb mDb = null;
    private String voltage;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        DBCopyUtil.getInstance().copyToPackage("sycx", R.raw.sycx);

        sd.init(applicationContext);

        initImageLoader(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    public static DwpcApplication getInstance() {
        return instance;
    }

    public SettingData getSettingData() {
        return sd;
    }

    public FinalDb getDb() {
        if (mDb == null) {
            mDb = FinalDb.create(DwpcApplication.getInstance(), StorageUtil.getOutputDbPath(), "sycx.db", true);
        }
        return mDb;
    }

    private void initImageLoader(Context context) {

        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.onloading)
                .showImageForEmptyUri(R.drawable.onfail)
                .showImageOnFail(R.drawable.onfail)
                .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(mOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    public void initData(String voltage) {
        this.voltage = voltage;
        cgList = getDb().findAll(CatagoryOne.class);

        String strWhere = "";
        for (CatagoryOne c1 : cgList) {
            strWhere = "parentId = '" + c1.getId() + "'";

            List<CatagoryTwo> c2List = getDb().findAllByWhere(CatagoryTwo.class, strWhere);
            c1.setChildList((ArrayList<CatagoryTwo>) c2List);

            for (CatagoryTwo c2 : c2List) {
                strWhere = "parentId = '" + c2.getId() + "'";

                List<CatagoryThree> c3List = getDb().findAllByWhere(CatagoryThree.class, strWhere);
                c2.setChildList((ArrayList<CatagoryThree>) c3List);
            }

        }
    }

    public List<CatagoryOne> getCatagoryOneList() {
//        cgList2.clear();
//        for (int i = 0; i < cgList.size(); i++) {
//            String m = cgList.get(i).getName();
//            String name = m.substring(0, 2);
//            String code = m.substring(m.lastIndexOf("_") + 1);
//            String time=cgList.get(i).getDate();
//            CatagoryOne co = new CatagoryOne();
//            if (voltage.equals(cgList.get(i).getType())) {
//
//                co.setType(name);
//                co.setCode(code);
//                co.setDate(time);
//                cgList2.add(co);
//
//            } else {
//               // return null;
//            }
//        }

        return cgList;
    }


    public void setCatagoryOneList(List<CatagoryOne> list) {
        cgList = list;
    }

    public String createCsvFile() {
        String result = null;

        for (CatagoryOne co : cgList) {
            result = co.createCsv();
            if (!"".equals(result)) {
                break;
            }
        }

        return result;
    }


}
