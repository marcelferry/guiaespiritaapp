package br.com.mythe.droid.gelib.provider;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProviderLite extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "br.com.mythe.droid.gelib.provider.SuggestionProviderLite";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public SuggestionProviderLite() {
        setupSuggestions(AUTHORITY, MODE);
    }
}