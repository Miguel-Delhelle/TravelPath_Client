package com.security.detector;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Classe de détection des vecteurs d'attaque Android
 * Détecte: débogueur, mode développeur, root, émulateur
 */
public class SecurityDetector {
    
    /**
     * Détecte si un débogueur est attaché à l'application
     */
    public static boolean isDebuggerAttached() {
        return android.os.Debug.isDebuggerConnected();
    }
    
    /**
     * Détecte si le mode développeur est activé
     */
    public static boolean isDeveloperModeEnabled(Context context) {
        try {
            int developerMode = android.provider.Settings.Global.getInt(
                context.getContentResolver(),
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            );
            return developerMode == 1;
        } catch (Exception e) {
            // Si l'accès est refusé, on considère que le mode développeur pourrait être activé
            return true;
        }
    }
    
    /**
     * Détecte si l'appareil est rooté
     */
    public static boolean isRooted() {
        // Vérification des fichiers système communs pour root
        String[] rootPaths = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        };
        
        for (String path : rootPaths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        
        // Vérification via la commande "which su"
        try {
            java.lang.Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            process.destroy();
            if (line != null && !line.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            // Ignorer les exceptions
        }
        
        // Vérification via les variables d'environnement
        String[] envPaths = System.getenv("PATH").split(":");
        for (String envPath : envPaths) {
            if (new File(envPath + "/su").exists()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Détecte si l'appareil est un émulateur
     */
    public static boolean isEmulator() {
        // Vérification du modèle et du fabricant
        String model = Build.MODEL.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
        String product = Build.PRODUCT.toLowerCase();
        String device = Build.DEVICE.toLowerCase();
        String hardware = Build.HARDWARE.toLowerCase();
        String brand = Build.BRAND.toLowerCase();
        String fingerprint = Build.FINGERPRINT.toLowerCase();
        
        // Liste des indicateurs d'émulateur
        String[] emulatorIndicators = {
            "generic", "unknown", "emulator", "simulator", "sdk", "google_sdk",
            "droid4x", "genymotion", "vbox", "virtualbox", "andy", "nox",
            "bluestacks", "memu", "ldplayer", "mumu"
        };
        
        String[] checkStrings = {model, manufacturer, product, device, hardware, brand, fingerprint};
        
        for (String checkString : checkStrings) {
            for (String indicator : emulatorIndicators) {
                if (checkString.contains(indicator)) {
                    return true;
                }
            }
        }
        
        // Vérification des propriétés système spécifiques aux émulateurs
        try {
            // Vérifier si le téléphone est réel (émulateurs n'ont souvent pas de téléphone)
            Class<?> telephonyClass = Class.forName("android.os.SystemProperties");
            java.lang.reflect.Method getMethod = telephonyClass.getMethod("get", String.class);
            String phoneModel = (String) getMethod.invoke(null, "ro.product.model");
            if (phoneModel != null && phoneModel.toLowerCase().contains("sdk")) {
                return true;
            }
        } catch (Exception e) {
            // Ignorer les exceptions
        }
        
        // Vérification du nombre de processeurs (émulateurs ont souvent 1 ou 2)
        // Note: Cette vérification seule n'est pas suffisante, elle est combinée avec d'autres indicateurs
        
        return false;
    }
    
    /**
     * Exécute tous les détecteurs et applique la remédiation si nécessaire
     * @param activity L'activité courante pour pouvoir la fermer
     */
    public static void checkSecurityAndRemediate(Activity activity) {
        boolean threatDetected = false;
        
        if (isDebuggerAttached()) {
            threatDetected = true;
            android.util.Log.e("SecurityDetector", "Débogueur détecté!");
        }
        
        if (isDeveloperModeEnabled(activity)) {
            threatDetected = true;
            android.util.Log.e("SecurityDetector", "Mode développeur détecté!");
        }
        
        if (isRooted()) {
            threatDetected = true;
            android.util.Log.e("SecurityDetector", "Appareil rooté détecté!");
        }
        
        if (isEmulator()) {
            threatDetected = true;
            android.util.Log.e("SecurityDetector", "Émulateur détecté!");
        }
        
        if (threatDetected) {
            // Remédiation: fermer l'application
            android.util.Log.e("SecurityDetector", "Menace détectée, fermeture de l'application");
            activity.finish();
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }
}
