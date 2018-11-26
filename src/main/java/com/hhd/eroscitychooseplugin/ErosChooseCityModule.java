package com.hhd.eroscitychooseplugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.reflect.TypeToken;
import com.hhd.eroscitychooseplugin.bean.JsonBean;
import com.google.gson.Gson;
import com.taobao.weex.bridge.JSCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErosChooseCityModule{
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private Context context;
    private String setSubmitText;
    private String setCancelText;
    private String setTitleText;
    private int setSubCalSize;
    private int setTitleSize;
    private int setTitleColor;
    private int setSubmitColor;
    private int setCancelColor;
    private int setTitleBgColor;
    private int setBgColor;
    private int setContentTextSize;
    private String addressType;
    private JSONArray address;

    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private static final String KEY_SETSUBMITTEXT = "setSubmitText";//确定按钮文字
    private static final String KEY_SETCANCELTEXT = "setCancelText";//取消按钮文字
    private static final String KEY_SETTITLETEXT = "setTitleText";//标题
    private static final String KEY_SETSUBCALSIZE = "setSubCalSize";//确定和取消文字大小
    private static final String KEY_SETTITLESIZE = "setTitleSize";//标题文字大小
    private static final String KEY_SETTITLECOLOR = "setTitleColor";//标题文字颜色
    private static final String KEY_SETSUBMITCOLOR = "setSubmitColor";//确定按钮文字颜色
    private static final String KEY_SETCANCELCOLOR = "setCancelColor";//取消按钮文字颜色
    private static final String KEY_SETTITLEBGCOLOR = "setTitleBgColor";//标题背景颜色 Night mode
    private static final String KEY_SETBGCOLOR = "setBgColor";//滚轮背景颜色 Night mode
    private static final String KEY_SETCONTENTTEXTSIZE = "setContentTextSize";//滚轮文字大小
    private static final String KEY_ADDRESSTYPE = "addressType";//是否使用默认数据源
    private static final String KEY_ADDRESS = "address";//自定义数据源
    private boolean isLoaded = false;

    public ErosChooseCityModule setContext(Context context) {
        this.context = context;
        return this;
    }

    public ErosChooseCityModule setParams(Map<String, Object> params) {
        this.setSubmitText = getOption(params, KEY_SETSUBMITTEXT, "确定");
        this.setCancelText = getOption(params, KEY_SETCANCELTEXT, "取消");
        this.setTitleText = getOption(params, KEY_SETTITLETEXT, "选择地区");
        this.setSubCalSize = getOption(params, KEY_SETSUBCALSIZE, 14);
        this.setTitleSize = getOption(params, KEY_SETTITLESIZE, 16);
        this.setTitleColor = stringToInt(getOption(params, KEY_SETTITLECOLOR, "#000000"));
        this.setSubmitColor = stringToInt(getOption(params, KEY_SETSUBMITCOLOR, "#FF2D4B"));
        this.setCancelColor = stringToInt(getOption(params, KEY_SETCANCELCOLOR, "#FF2D4B"));
        this.setTitleBgColor = stringToInt(getOption(params, KEY_SETTITLEBGCOLOR, "#ffffff"));
        this.setBgColor = stringToInt(getOption(params, KEY_SETBGCOLOR, "#ffffff"));
        this.setContentTextSize = getOption(params, KEY_SETCONTENTTEXTSIZE, 16);
        this.addressType = getOption(params, KEY_ADDRESSTYPE, "default");
        this.address = (JSONArray) params.get(KEY_ADDRESS);
        return this;
    }
    private int stringToInt(String str){
        int tempInt;
        tempInt = Color.parseColor(str);
        return tempInt;
    }
    private <T> T getOption(Map<String, Object> options, String key, T defValue) {
        Object value = options.get(key);
        if (value == null || value.equals("")) {
            return defValue;
        } else {
            try {
                return (T) value;
            } catch (Exception e) {
                e.printStackTrace();
                return defValue;
            }
        }
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonDataS;
        JSONArray JsonDataJ;
        ArrayList<JsonBean> jsonBean;
        if(this.addressType.equals("custom")){
            JsonDataJ = this.address;
            jsonBean = parseData(JsonDataJ);//用Gson 转成实体
        }else{
            JsonDataS = new GetJsonDataUtil().getJson(context, "province.json");//获取assets目录下的json文件数据
            jsonBean = parseData(JsonDataS);//用Gson 转成实体
        }


        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getAreaList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getAreaList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getAreaList().get(c).getCity() == null
                        || jsonBean.get(i).getAreaList().get(c).getCity().size() == 0) {
                    City_AreaList.add("");
                } else {
                    City_AreaList.addAll(jsonBean.get(i).getAreaList().get(c).getCity());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }


    }
    public void showPickerView( final JSCallback callback) {// 弹出选择器
        initJsonData();
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText() + "," +
//                        options2Items.get(options1).get(options2) + "," +
//                        options3Items.get(options1).get(options2).get(options3)+","+
//                        options1Items.get(options1).getProvinceCode()+","+
//                        options1Items.get(options1).getCityList().get(options2).getAreaCode()+","+
//                        options1Items.get(options1).getCityList().get(options2).getCityCode(options3);
                HashMap<String, Object> backObj = new HashMap();
                backObj.put("provinceStr",options1Items.get(options1).getPickerViewText());
                backObj.put("provinceCode",options1Items.get(options1).getProvinceCode());
                backObj.put("cityStr", options2Items.get(options1).get(options2));
                backObj.put("cityCode",options1Items.get(options1).getAreaList().get(options2).getCityCode());
                backObj.put("areaStr",options3Items.get(options1).get(options2).get(options3));
                backObj.put("areaCode",options1Items.get(options1).getAreaList().get(options2).getAreaCode(options3));
                callback.invoke(backObj);
            }
        })

                .setTitleText(this.setTitleText)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(this.setContentTextSize)
                .setSubmitText(this.setSubmitText)
                .setCancelText(this.setCancelText)
                .setSubCalSize(this.setSubCalSize)
                .setTitleSize(this.setTitleSize)
                .setTitleColor(this.setTitleColor)
                .setSubmitColor(this.setSubmitColor)
                .setCancelColor(this.setCancelColor)
                .setTitleBgColor(this.setTitleBgColor)
                .setBgColor(this.setBgColor)
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.DKGRAY)
                .setLineSpacingMultiplier((float) 2.0)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }
    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        detail = GsonUtil.fromJson(result, new TypeToken<List<JsonBean>>() {
        }.getType());
        return detail;
    }
    public ArrayList<JsonBean> parseData(JSONArray result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        detail = GsonUtil.fromJson(result.toJSONString(), new TypeToken<List<JsonBean>>() {
        }.getType());
        return detail;
    }
}
