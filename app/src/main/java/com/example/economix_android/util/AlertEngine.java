package com.example.economix_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.economix_android.Model.data.DataRepository;
import com.example.economix_android.Model.data.Gasto;
import com.example.economix_android.Model.data.RegistroFinanciero;
import com.example.economix_android.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AlertEngine {

    public enum ExpenseEventType { CREATED, UPDATED, DELETED }

    public static final class AlertData {
        public final String title;
        public final String message;
        public final String primaryButton;
        @Nullable public final String secondaryButton;
        public final int secondaryDestination;
        public final boolean dismissOnOutside;

        public AlertData(String title,
                         String message,
                         String primaryButton,
                         @Nullable String secondaryButton,
                         int secondaryDestination,
                         boolean dismissOnOutside) {
            this.title = title;
            this.message = message;
            this.primaryButton = primaryButton;
            this.secondaryButton = secondaryButton;
            this.secondaryDestination = secondaryDestination;
            this.dismissOnOutside = dismissOnOutside;
        }
    }

    private static final String PREFS = "alert_engine_prefs";
    private static final String KEY_GENERAL_BUDGET = "general_budget";
    private static final String KEY_LARGE_THRESHOLD = "large_expense_threshold";
    private static final String KEY_STREAK_THRESHOLD = "streak_days_threshold";
    private static final String KEY_DISMISS_OUTSIDE = "dismiss_outside";
    private static final String KEY_STREAK_CATEGORIES = "streak_categories";

    private static final BigDecimal DEFAULT_GENERAL_BUDGET = new BigDecimal("3000");
    private static final BigDecimal DEFAULT_LARGE_THRESHOLD = new BigDecimal("800");
    private static final int DEFAULT_STREAK_THRESHOLD = 5;
    private static final long LARGE_ALERT_COOLDOWN_MS = 30_000L;

    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());

    private AlertEngine() {}

    public static List<AlertData> evaluateExpenseAlerts(Context context, @Nullable Gasto gasto, ExpenseEventType eventType) {
        List<AlertData> alerts = new ArrayList<>();
        if (gasto == null) {
            return alerts;
        }

        AlertData overBudget = evaluateOverBudget(context, gasto);
        if (overBudget != null) {
            alerts.add(overBudget);
        }

        if (eventType == ExpenseEventType.CREATED) {
            AlertData large = evaluateLargeExpense(context, gasto);
            if (large != null) {
                alerts.add(large);
            }
        }

        AlertData streak = evaluateStreak(context, gasto);
        if (streak != null) {
            alerts.add(streak);
        }
        return alerts;
    }

    @Nullable
    public static AlertData evaluateSavingsGoalAlert(Context context,
                                                     String metaNombre,
                                                     BigDecimal totalAntes,
                                                     BigDecimal totalDespues,
                                                     BigDecimal objetivo) {
        if (TextUtils.isEmpty(metaNombre) || objetivo == null || objetivo.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (totalAntes == null) {
            totalAntes = BigDecimal.ZERO;
        }
        if (totalDespues == null) {
            totalDespues = BigDecimal.ZERO;
        }

        if (totalAntes.compareTo(objetivo) < 0 && totalDespues.compareTo(objetivo) >= 0) {
            SharedPreferences prefs = prefs(context);
            String monthKey = YearMonth.now().toString();
            String key = "savings_goal_shown_" + monthKey + "_" + normalize(metaNombre);
            if (prefs.getBoolean(key, false)) {
                return null;
            }
            prefs.edit().putBoolean(key, true).apply();
            return new AlertData(
                    context.getString(R.string.alert_title_saving_goal_reached),
                    context.getString(R.string.alert_message_saving_goal_reached,
                            formatMoney(objetivo),
                            formatMoney(totalDespues)),
                    context.getString(R.string.alert_acknowledge),
                    context.getString(R.string.alert_view_savings),
                    R.id.action_navigation_ahorro_to_ahorroInfo,
                    dismissOnOutside(context)
            );
        }
        return null;
    }

    @Nullable
    private static AlertData evaluateOverBudget(Context context, Gasto gasto) {
        SharedPreferences prefs = prefs(context);
        String categoria = safeCategory(gasto.getArticulo());
        BigDecimal budget = getCategoryBudget(context, categoria);
        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        BigDecimal gastado = BigDecimal.ZERO;
        YearMonth month = YearMonth.now();
        for (Gasto item : DataRepository.getGastos()) {
            if (sameCategory(item.getArticulo(), categoria) && isInMonth(item, month)) {
                gastado = gastado.add(parseMonto(item.getDescripcion()));
            }
        }

        String shownKey = "over_budget_" + month + "_" + normalize(categoria);
        if (gastado.compareTo(budget) > 0) {
            if (prefs.getBoolean(shownKey, false)) {
                return null;
            }
            prefs.edit().putBoolean(shownKey, true).apply();
            BigDecimal exceso = gastado.subtract(budget);
            return new AlertData(
                    context.getString(R.string.alert_title_budget_exceeded),
                    context.getString(R.string.alert_message_budget_exceeded,
                            categoria,
                            formatMoney(exceso),
                            formatMoney(budget),
                            formatMoney(gastado)),
                    context.getString(R.string.alert_acknowledge),
                    context.getString(R.string.alert_view_details),
                    R.id.action_navigation_gastos_to_gastosInfo,
                    dismissOnOutside(context)
            );
        }

        prefs.edit().remove(shownKey).apply();
        return null;
    }

    @Nullable
    private static AlertData evaluateLargeExpense(Context context, Gasto gasto) {
        BigDecimal monto = parseMonto(gasto.getDescripcion());
        BigDecimal threshold = getBigDecimalPref(context, KEY_LARGE_THRESHOLD, DEFAULT_LARGE_THRESHOLD);
        if (monto.compareTo(threshold) < 0) {
            return null;
        }

        SharedPreferences prefs = prefs(context);
        String signature = "large_signature_" + buildExpenseSignature(gasto);
        long now = System.currentTimeMillis();
        long last = prefs.getLong(signature, 0L);
        if (now - last < LARGE_ALERT_COOLDOWN_MS) {
            return null;
        }
        prefs.edit().putLong(signature, now).apply();

        return new AlertData(
                context.getString(R.string.alert_title_large_expense),
                context.getString(R.string.alert_message_large_expense,
                        formatMoney(monto),
                        safeCategory(gasto.getArticulo())),
                context.getString(R.string.alert_acknowledge),
                context.getString(R.string.alert_edit_expense),
                R.id.navigation_gastos,
                dismissOnOutside(context)
        );
    }

    @Nullable
    private static AlertData evaluateStreak(Context context, Gasto gasto) {
        String categoria = safeCategory(gasto.getArticulo());
        Set<String> triggerCategories = getTriggerCategories(context);
        if (!containsIgnoreCase(triggerCategories, categoria)) {
            return null;
        }

        int streak = calculateStreakDays(categoria);
        int threshold = Math.max(2, prefs(context).getInt(KEY_STREAK_THRESHOLD, DEFAULT_STREAK_THRESHOLD));
        if (streak < threshold) {
            return null;
        }

        String dayKey = "streak_" + LocalDate.now() + "_" + normalize(categoria);
        SharedPreferences prefs = prefs(context);
        if (prefs.getBoolean(dayKey, false)) {
            return null;
        }
        prefs.edit().putBoolean(dayKey, true).apply();

        return new AlertData(
                context.getString(R.string.alert_title_spending_streak),
                context.getString(R.string.alert_message_spending_streak, streak, categoria),
                context.getString(R.string.alert_acknowledge),
                context.getString(R.string.alert_go_budget),
                R.id.action_navigation_gastos_to_gastosInfo,
                dismissOnOutside(context)
        );
    }

    private static int calculateStreakDays(String categoria) {
        Set<LocalDate> fechas = new HashSet<>();
        for (Gasto item : DataRepository.getGastos()) {
            if (!sameCategory(item.getArticulo(), categoria)) {
                continue;
            }
            LocalDate date = parseDate(item.getFecha());
            if (date != null) {
                fechas.add(date);
            }
        }
        int streak = 0;
        LocalDate cursor = LocalDate.now();
        while (fechas.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private static boolean isInMonth(Gasto gasto, YearMonth month) {
        LocalDate date = parseDate(gasto.getFecha());
        return date != null && YearMonth.from(date).equals(month);
    }

    @Nullable
    private static LocalDate parseDate(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value, INPUT_DATE);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static BigDecimal getCategoryBudget(Context context, String categoria) {
        SharedPreferences prefs = prefs(context);
        String key = "budget_category_" + normalize(categoria);
        String saved = prefs.getString(key, null);
        if (!TextUtils.isEmpty(saved) && RegistroFinanciero.esMontoValido(saved)) {
            return parseMonto(saved);
        }
        return getBigDecimalPref(context, KEY_GENERAL_BUDGET, DEFAULT_GENERAL_BUDGET);
    }

    private static Set<String> getTriggerCategories(Context context) {
        String csv = prefs(context).getString(KEY_STREAK_CATEGORIES,
                "antojitos,entretenimiento,comida rápida,comida rapida");
        Set<String> out = new HashSet<>();
        if (!TextUtils.isEmpty(csv)) {
            String[] parts = csv.split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    out.add(trimmed.toLowerCase(Locale.ROOT));
                }
            }
        }
        return out;
    }

    private static BigDecimal getBigDecimalPref(Context context, String key, BigDecimal fallback) {
        SharedPreferences prefs = prefs(context);
        String value = prefs.getString(key, fallback.toPlainString());
        if (RegistroFinanciero.esMontoValido(value)) {
            return parseMonto(value);
        }
        return fallback;
    }

    private static BigDecimal parseMonto(String value) {
        if (!RegistroFinanciero.esMontoValido(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(RegistroFinanciero.normalizarMonto(value));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private static boolean dismissOnOutside(Context context) {
        return prefs(context).getBoolean(KEY_DISMISS_OUTSIDE, true);
    }

    private static String formatMoney(BigDecimal value) {
        return "$" + value.stripTrailingZeros().toPlainString();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String safeCategory(String raw) {
        return TextUtils.isEmpty(raw) ? "General" : raw.trim();
    }

    private static boolean sameCategory(String left, String right) {
        return safeCategory(left).equalsIgnoreCase(safeCategory(right));
    }

    private static String normalize(String raw) {
        return safeCategory(raw).toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    private static boolean containsIgnoreCase(Set<String> data, String needle) {
        String normalized = safeCategory(needle).toLowerCase(Locale.ROOT);
        for (String item : data) {
            if (normalized.equals(item.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static String buildExpenseSignature(Gasto gasto) {
        String id = gasto.getId() != null ? String.valueOf(gasto.getId()) : "noid";
        return id + "|" + normalize(gasto.getArticulo()) + "|" + RegistroFinanciero.normalizarMonto(gasto.getDescripcion()) + "|" + gasto.getFecha();
    }
}
