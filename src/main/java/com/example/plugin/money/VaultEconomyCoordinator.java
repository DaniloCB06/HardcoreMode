package com.example.plugin.money;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class VaultEconomyCoordinator {
    private static final String VAULT_CLASS_NAME = "net.cfh.vault.VaultUnlocked";
    private final String pluginName;

    public VaultEconomyCoordinator(String pluginName) {
        this.pluginName = pluginName == null || pluginName.isBlank() ? "HardcoreMode" : pluginName;
    }

    public DepositResult deposit(PlayerRef playerRef, double amount) {
        if (playerRef == null || amount <= 0.0d) {
            return DepositResult.skipped();
        }

        try {
            Class<?> vaultClass = Class.forName(VAULT_CLASS_NAME);
            Method economyObjMethod = vaultClass.getMethod("economyObj");
            Object economy = economyObjMethod.invoke(null);
            if (economy == null) {
                return DepositResult.unavailable("VaultUnlocked is installed, but no economy provider is active.");
            }

            UUID uuid = playerRef.getUuid();
            String username = playerRef.getUsername();
            ensureAccountExists(economy, uuid, username);

            Method depositMethod = economy.getClass().getMethod("deposit", String.class, UUID.class, BigDecimal.class);
            BigDecimal value = BigDecimal.valueOf(amount);
            Object response = depositMethod.invoke(economy, pluginName, uuid, value);

            Method successMethod = response.getClass().getMethod("transactionSuccess");
            boolean success = Boolean.TRUE.equals(successMethod.invoke(response));
            if (!success) {
                return DepositResult.failure(readErrorMessage(response), formatAmount(economy, value));
            }

            return DepositResult.success(formatAmount(economy, value), getProviderName(economy));
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return DepositResult.failure(ex.getMessage(), formatAmountFallback(amount));
        }
    }

    public String getStatusMessage() {
        try {
            Class<?> vaultClass = Class.forName(VAULT_CLASS_NAME);
            Method economyObjMethod = vaultClass.getMethod("economyObj");
            Object economy = economyObjMethod.invoke(null);
            if (economy == null) {
                return "VaultUnlocked detected, but no compatible economy provider is currently active.";
            }
            return "VaultUnlocked detected. Active economy provider: " + getProviderName(economy) + ".";
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return "VaultUnlocked bridge was not detected. Money rewards require VaultUnlocked plus a compatible economy provider.";
        }
    }

    public boolean isAvailable() {
        try {
            Class<?> vaultClass = Class.forName(VAULT_CLASS_NAME);
            Method economyObjMethod = vaultClass.getMethod("economyObj");
            return economyObjMethod.invoke(null) != null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return false;
        }
    }

    private void ensureAccountExists(Object economy, UUID uuid, String username) {
        try {
            Method hasAccount = economy.getClass().getMethod("hasAccount", UUID.class);
            Object result = hasAccount.invoke(economy, uuid);
            if (Boolean.TRUE.equals(result)) {
                return;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }

        try {
            Method createAccount = economy.getClass().getMethod("createAccount", UUID.class, String.class, boolean.class);
            createAccount.invoke(economy, uuid, safeAccountName(username, uuid), true);
            return;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }

        try {
            Method createAccount = economy.getClass().getMethod("createAccount", UUID.class, String.class);
            createAccount.invoke(economy, uuid, safeAccountName(username, uuid));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }

    private String getProviderName(Object economy) {
        try {
            Method getName = economy.getClass().getMethod("getName");
            Object value = getName.invoke(economy);
            if (value instanceof String name && !name.isBlank()) {
                return name;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return "Unknown";
    }

    private String formatAmount(Object economy, BigDecimal amount) {
        try {
            Method format = economy.getClass().getMethod("format", BigDecimal.class);
            Object value = format.invoke(economy, amount);
            if (value instanceof String text && !text.isBlank()) {
                return text;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return formatAmountFallback(amount.doubleValue());
    }

    private String formatAmountFallback(double amount) {
        return String.format(Locale.US, "%.2f", Math.max(0.0d, amount));
    }

    private String safeAccountName(String username, UUID uuid) {
        if (username != null && !username.isBlank()) {
            return username;
        }
        return uuid != null ? uuid.toString() : "unknown";
    }

    private String readErrorMessage(Object response) {
        if (response == null) {
            return "Unknown economy error.";
        }

        try {
            Field errorField = response.getClass().getField("errorMessage");
            Object value = errorField.get(response);
            if (value instanceof String text && !text.isBlank()) {
                return text;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }

        return "The economy provider rejected the transaction.";
    }

    public static final class DepositResult {
        public final boolean success;
        public final boolean available;
        public final String formattedAmount;
        public final String providerName;
        public final String errorMessage;

        private DepositResult(boolean success, boolean available, String formattedAmount, String providerName, String errorMessage) {
            this.success = success;
            this.available = available;
            this.formattedAmount = formattedAmount;
            this.providerName = providerName;
            this.errorMessage = errorMessage;
        }

        public static DepositResult success(String formattedAmount, String providerName) {
            return new DepositResult(true, true, formattedAmount, providerName, null);
        }

        public static DepositResult failure(String errorMessage, String formattedAmount) {
            return new DepositResult(false, true, formattedAmount, null, errorMessage);
        }

        public static DepositResult unavailable(String errorMessage) {
            return new DepositResult(false, false, null, null, errorMessage);
        }

        public static DepositResult skipped() {
            return new DepositResult(false, true, null, null, null);
        }
    }
}
