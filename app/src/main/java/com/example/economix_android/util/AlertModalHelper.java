package com.example.economix_android.util;

import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class AlertModalHelper {

    public interface SecondaryActionListener {
        void onSecondaryAction(int destinationId);
    }

    public interface DismissListener {
        void onDismiss();
    }

    private AlertModalHelper() {}

    public static void show(Fragment fragment,
                            AlertEngine.AlertData alert,
                            SecondaryActionListener listener,
                            DismissListener dismissListener) {
        if (fragment.getContext() == null) {
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireContext())
                .setTitle(alert.title)
                .setMessage(alert.message)
                .setPositiveButton(alert.primaryButton, (dialog, which) -> dialog.dismiss());

        if (alert.secondaryButton != null) {
            builder.setNeutralButton(alert.secondaryButton,
                    (dialog, which) -> listener.onSecondaryAction(alert.secondaryDestination));
        }

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(d -> dismissListener.onDismiss());
        dialog.setCanceledOnTouchOutside(alert.dismissOnOutside);
        dialog.setCancelable(true);
        dialog.show();
    }
}
