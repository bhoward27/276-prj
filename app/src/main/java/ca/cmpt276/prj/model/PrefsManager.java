package ca.cmpt276.prj.model;

import android.content.SharedPreferences;

import static ca.cmpt276.prj.model.Constants.LANDSCAPE_SET;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.PICTURE_TYPE_STRING_PREF;
import static ca.cmpt276.prj.model.Constants.PICTURE_TYPE_INT_PREF;
import static ca.cmpt276.prj.model.Constants.PREDATOR_SET;
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

	public void saveStrTypeInstalled(String strType) {
		SharedPreferences.Editor editor = prefs.edit();//Write the value
		int putValue = 0;
		switch (strType) {
			case "Landscape":
				putValue = LANDSCAPE_SET;
				break;
			case "Predator":
				putValue = PREDATOR_SET;
				break;
			default:
				putValue = LANDSCAPE_SET;
		}
		editor.putString(PICTURE_TYPE_STRING_PREF, strType);
		editor.putInt(PICTURE_TYPE_INT_PREF, putValue);

		editor.apply();
	}

	public int getTypePictureInstalledInt(String defaultValue){
		int defaultInt;
		switch (defaultValue) {
			case "Landscape":
				defaultInt = LANDSCAPE_SET;
				break;
			case "Predator":
				defaultInt = PREDATOR_SET;
				break;
			default:
				defaultInt = LANDSCAPE_SET;
		}
		return prefs.getInt(PICTURE_TYPE_INT_PREF, defaultInt);
	}

	public String getTypePictureInstalledStr(String defaultValue){
		return prefs.getString(PICTURE_TYPE_STRING_PREF, defaultValue);
	}

	public SharedPreferences getPrefs() {
		return prefs;
	}

	public String getName(String defaultName) {
		return prefs.getString(NAME_PREF, defaultName);
	}

	public void saveName(String Name) {
		SharedPreferences.Editor editor = prefs.edit();//Write the value
		editor.putString(NAME_PREF, Name);
		editor.apply();
	}
}
