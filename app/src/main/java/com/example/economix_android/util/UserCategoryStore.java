package com.example.economix_android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.economix_android.auth.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class UserCategoryStore {
    public static final String TYPE_INGRESO = "ingreso";
    public static final String TYPE_GASTO = "gasto";

    private static final String PREFS_NAME = "economix_user_categories";
    private static final String KEY_PREFIX = "categories_";

    private UserCategoryStore() {
    }

    public static List<String> getCategories(Context context, String type) {
        if (context == null || type == null) {
            return Collections.emptyList();
        }
        String json = preferences(context).getString(keyFor(context, type), "[]");
        List<String> categories = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                String value = array.optString(i, "").trim();
                if (!value.isEmpty() && !containsIgnoreCase(categories, value)) {
                    categories.add(value);
                }
            }
        } catch (JSONException ignored) {
        }
        return categories;
    }

    public static boolean saveCategory(Context context, String type, String category) {
        if (context == null || type == null || category == null) {
            return false;
        }
        String normalized = category.trim();
        if (normalized.isEmpty()) {
            return false;
        }
        List<String> categories = getCategories(context, type);
        if (containsIgnoreCase(categories, normalized)) {
            return false;
        }
        categories.add(normalized);
        JSONArray array = new JSONArray();
        for (String value : categories) {
            array.put(value);
        }
        preferences(context).edit()
                .putString(keyFor(context, type), array.toString())
                .apply();
        return true;
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String keyFor(Context context, String type) {
        Integer userId = SessionManager.getUserId(context);
        String owner = userId != null ? String.valueOf(userId) : "anonymous";
        return KEY_PREFIX + type.toLowerCase(Locale.ROOT) + "_" + owner;
    }

    private static boolean containsIgnoreCase(List<String> values, String target) {
        for (String value : values) {
            if (value.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
}
