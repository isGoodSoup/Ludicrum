package org.lud.engine.enums;

import org.lud.engine.service.Localization;

import java.util.Locale;

public enum Lang {
    EN("en"),
    ES("es"),
    FR("fr");

    private final String langKey;

    Lang(String langKey) {
        this.langKey = langKey;
    }

    public static void nextLang() {
        Lang[] languages = Lang.values();
        Lang current = null;
        for (Lang l : languages) {
            if (l.langKey.equals(Localization.lang.getLocale().getLanguage())) {
                current = l;
                break;
            }
        }
        int currentIndex = current != null ? current.ordinal() : 0;
        int nextIndex = (currentIndex + 1) % languages.length;
        Localization.lang.setLocale(Locale.forLanguageTag(languages[nextIndex].langKey));
    }
}
