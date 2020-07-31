package ca.cmpt276.prj.model;

import android.content.SharedPreferences;

/**
 * This class slightly simplifies working with SharedPreferences, just specify a key and pref.
 */

public class PrefsManager {
	SharedPreferences prefs;

	public PrefsManager(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	public int loadInt(String prefKey, int prefDefault) {
		return prefs.getInt(prefKey, prefDefault);
	}

	public String loadString(String prefKey, String prefDefault) {
		return prefs.getString(prefKey, prefDefault);
	}

	public Boolean loadBool(String prefKey, Boolean prefDefault) {
		return prefs.getBoolean(prefKey, prefDefault);
	}

	public void saveInt(String prefKey, int pref) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(prefKey, pref);
		editor.apply();
	}

	public void saveString(String prefKey, String pref) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(prefKey, pref);
		editor.apply();
	}

	public void saveBool(String prefKey, Boolean pref) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(prefKey, pref);
		editor.apply();
	}
}
