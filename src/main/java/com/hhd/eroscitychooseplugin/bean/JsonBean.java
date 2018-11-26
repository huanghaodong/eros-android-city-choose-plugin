package com.hhd.eroscitychooseplugin.bean;


import com.contrarywind.interfaces.IPickerViewData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO<json数据源>
 *
 * @author: 小嵩
 * @date: 2017/3/16 15:36
 */

public class JsonBean implements IPickerViewData {


    /**
     * name : 省份
     * city : [{"name":"北京市","area":[{"name":"东城区","code":"3"}]},"code":"2"]
     * code: "1"
     */

    private String name;
    private List<CityBean> city;
    private String code;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getAreaList() {
        return city;
    }

    public void setCityList(List<CityBean> city) {
        this.city = city;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }
    public String getProvinceCode() {
        return this.code;
    }
    public String getPickerViewCode() {
        return this.name;
    }


    public static class CityBean {
        /**
         * name : 城市
         * area : [{"name":"东城区","code":3}]
         * code : 2
         */

        private String name;
        private List<Map<String, String>> area;
        private String code;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getAreaCode(int i) {
            String code = area.get(i).get("code");
            return code;
        }
        public String getCityCode() {
            return this.code;
        }
        public List<String> getCity() {
            List<String> tempArea = new ArrayList<>();
            for (int i = 0; i < area.size(); i++) {
                tempArea.add(area.get(i).get("name"));

            }

            return tempArea;
        }

        //public void setArea(List<Map<String, Object>> area) {
//            this.area = area;
//        }
    }
}
