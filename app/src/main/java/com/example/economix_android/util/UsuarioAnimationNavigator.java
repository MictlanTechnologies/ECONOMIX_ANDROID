package com.example.economix_android.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.example.economix_android.R;

import java.util.concurrent.atomic.AtomicBoolean;

public final class UsuarioAnimationNavigator {

    private UsuarioAnimationNavigator() {
    }

    public static void playAndNavigate(@NonNull View originView, @IdRes int navId) {
        NavController navController = Navigation.findNavController(originView);
        Context context = originView.getContext();

        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(0x88000000);

        LottieAnimationView lottie = new LottieAnimationView(context);
        int size = (int) (220 * context.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        lottie.setLayoutParams(params);
        lottie.setAnimation(R.raw.usuario);
        lottie.setRepeatCount(0);
        lottie.setRepeatMode(LottieDrawable.RESTART);

        root.addView(lottie);
        dialog.setContentView(root);
        dialog.setCancelable(false);

        AtomicBoolean handled = new AtomicBoolean(false);

        lottie.addLottieOnCompositionLoadedListener(composition -> playSegment(lottie, composition));
        lottie.setFailureListener(error -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            navigateIfNeeded(navController, navId, handled);
        });
        lottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                navigateIfNeeded(navController, navId, handled);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                navigateIfNeeded(navController, navId, handled);
            }
        });

        dialog.show();
        LottieComposition composition = lottie.getComposition();
        if (composition != null) {
            playSegment(lottie, composition);
        }
    }

    private static void playSegment(@NonNull LottieAnimationView lottie, @NonNull LottieComposition composition) {
        float durationMs = composition.getDuration();
        if (durationMs <= 0f) {
            lottie.playAnimation();
            return;
        }

        float startMs = 6000f;
        float endMs = 9000f;

        float startFrame;
        float endFrame;

        if (startMs >= durationMs) {
            startFrame = composition.getStartFrame();
            endFrame = composition.getEndFrame();
        } else {
            float endAdjustedMs = Math.min(endMs, durationMs);
            startFrame = composition.getFrameForProgress(startMs / durationMs);
            endFrame = composition.getFrameForProgress(endAdjustedMs / durationMs);
        }

        lottie.setMinAndMaxFrame(Math.round(startFrame), Math.round(endFrame));
        lottie.setFrame(Math.round(startFrame));
        lottie.playAnimation();
    }

    private static void navigateIfNeeded(@NonNull NavController navController, @IdRes int navId,
                                         @NonNull AtomicBoolean handled) {
        if (!handled.compareAndSet(false, true)) {
            return;
        }
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == navId) {
            return;
        }
        navController.navigate(navId);
    }
}
