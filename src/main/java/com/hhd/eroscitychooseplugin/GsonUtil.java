package com.hhd.eroscitychooseplugin;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by caipt on 2017/5/10.
 */

public class GsonUtil {
    private static Gson gson;

    private GsonUtil() {
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        return gson.fromJson(new String(bytes), clazz);
    }

    public static <T> T fromObject(Object object, Class<T> clazz){
        return gson.fromJson(toJson(object),clazz);
    }

    public static <T> T fromObject(Object object, Type type){
        return gson.fromJson(toJson(object),type);
    }

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
        builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return json == null ? null : new Date(json.getAsLong() * 1000L);
            }
        });
        gson = builder.create();
    }
}
