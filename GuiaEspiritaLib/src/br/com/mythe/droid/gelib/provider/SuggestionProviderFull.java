package br.com.mythe.droid.gelib.provider;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProviderFull extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "br.com.mythe.droid.gelib.provider.SuggestionProviderFull";
    public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public SuggestionProviderFull() {
        setupSuggestions(AUTHORITY, MODE);
    }
}