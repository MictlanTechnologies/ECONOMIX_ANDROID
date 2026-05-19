package com.example.economix_android.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.view.ViewOutlineProvider;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.example.economix_android.R;
import com.example.economix_android.auth.SessionManager;

public final class ProfileImageUtils {

    private ProfileImageUtils() {
    }

    public static void applyProfileImage(Context context, ImageView imageView) {
        applyProfileImage(context, imageView, R.drawable.usuario);
    }

    public static void applyProfileImage(Context context, ImageView imageView, @DrawableRes int fallbackResId) {
        if (context == null || imageView == null) {
            return;
        }
        String uriString = SessionManager.getProfilePhotoUri(context);
        if (uriString != null && !uriString.trim().isEmpty()) {
            imageView.setImageURI(Uri.parse(uriString));
            imageView.setImageTintList(null);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setBackgroundResource(R.drawable.bg_avatar_circle);
            imageView.setClipToOutline(true);
            imageView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        } else {
            imageView.setImageResource(fallbackResId);
            imageView.setImageTintList(ContextCompat.getColorStateList(context, R.color.white));
            imageView.setBackgroundResource(R.drawable.bg_avatar_circle);
            imageView.setClipToOutline(true);
            imageView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        }
    }
}
