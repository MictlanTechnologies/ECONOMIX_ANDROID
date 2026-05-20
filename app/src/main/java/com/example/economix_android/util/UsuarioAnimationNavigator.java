package com.example.economix_android.util;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;

public final class UsuarioAnimationNavigator {
    private static boolean animationRunning;

    private UsuarioAnimationNavigator() {}

    public static void playAndNavigate(View sourceView, int destinationId, @RawRes int animationRes,
                                       @Nullable Float startMs, @Nullable Float endMs) {
        if (animationRunning) return;
        NavController navController = Navigation.findNavController(sourceView);
        NavDestination destination = navController.getCurrentDestination();
        if (destination != null && destination.getId() == destinationId) return;

        playOnly(sourceView, animationRes, startMs, endMs, () -> {
            try { navController.navigate(destinationId); } catch (Exception ignored) {}
        });
    }

    public static void playOnly(View sourceView, @RawRes int animationRes,
                                @Nullable Float startMs, @Nullable Float endMs,
                                @Nullable Runnable onFinished) {
        if (animationRunning) return;
        Context context = sourceView.getContext();
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(0x88000000);

        LottieAnimationView animView = new LottieAnimationView(context);
        int size = (int) (220f * Resources.getSystem().getDisplayMetrics().density);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size, Gravity.CENTER);
        root.addView(animView, lp);
        dialog.setContentView(root);
        dialog.setCancelable(false);

        animationRunning = true;
        try {
            animView.setAnimation(animationRes);
            animView.setRepeatCount(0);
            animView.addLottieOnCompositionLoadedListener(composition -> {
                float start = startMs != null && startMs >= 0 ? startMs : 0f;
                float duration = composition.getDuration();
                float end = endMs != null ? endMs : duration;
                if (end > duration) end = duration;
                if (start >= end) start = 0f;
                if (end - start > 2500f) end = start + 1500f;
                if (end > duration) end = duration;

                float minFrame = composition.getFrameForProgress(start / duration);
                float maxFrame = composition.getFrameForProgress(end / duration);
                animView.setMinAndMaxFrame((int) minFrame, (int) maxFrame);
                animView.playAnimation();
            });
            animView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) { }
                @Override public void onAnimationCancel(Animator animation) { finish(); }
                @Override public void onAnimationEnd(Animator animation) { finish(); }
                private void finish() {
                    if (dialog.isShowing()) dialog.dismiss();
                    animationRunning = false;
                    if (onFinished != null) onFinished.run();
                }
            });
            dialog.show();
        } catch (Exception ignored) {
            if (dialog.isShowing()) dialog.dismiss();
            animationRunning = false;
            if (onFinished != null) onFinished.run();
        }
    }
}
