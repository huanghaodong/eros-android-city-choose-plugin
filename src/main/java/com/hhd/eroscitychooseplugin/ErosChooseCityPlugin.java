package com.hhd.eroscitychooseplugin;

import android.widget.Toast;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.Map;

/**
 * Created by liuyuanxiao on 2018/5/4.
 */
@WeexModule(name = "choosecity", lazyLoad = true)
public class ErosChooseCityPlugin extends WXModule {

    @JSMethod(uiThread = true)
    public void open(Map<String, Object> params, JSCallback resultCallback) {
        new ErosChooseCityModule().setContext(mWXSDKInstance.getContext()).setParams(params).showPickerView(resultCallback);
    }

}
