package org.lud.engine.service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localization {
    private ResourceBundle bundle;
    private Locale locale;
    public static Localization lang = new Localization(Locale.forLanguageTag("en"));

    public Localization(Locale locale) {
        setLocale(locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        bundle = ResourceBundle.getBundle("lang", locale);
    }

    public String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public String f(String key, Object... args) {
        String pattern = t(key);
        return java.text.MessageFormat.format(pattern, args);
    }

    public Locale getLocale() {
        return locale;
    }
}