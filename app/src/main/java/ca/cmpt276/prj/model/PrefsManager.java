package ca.cmpt276.prj.model;

import android.content.SharedPreferences;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrefsManager {
	private SharedPreferences prefs;

	// Singleton setup
	private static PrefsManager instance;

	private PrefsManager(SharedPreferences sharedPrefs) {
		this.prefs = sharedPrefs;
	}

	public static PrefsManager getInstance() {
		assertNotNull(instance);
		return instance;
	}

	public static void instantiate(SharedPreferences prefs) {
		if (instance == null) {
			instance = new PrefsManager(prefs);
		}
	}
	// End singleton setup

	public SharedPreferences getPrefs() {
		return prefs;
	}
}
