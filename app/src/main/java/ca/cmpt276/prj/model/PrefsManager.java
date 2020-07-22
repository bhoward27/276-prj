package ca.cmpt276.prj.model;

import android.content.SharedPreferences;
import android.util.Log;

import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET;
import static ca.cmpt276.prj.model.Constants.DEFAULT_IMAGE_SET_PREFIX;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_NAME_PREF;
import static ca.cmpt276.prj.model.Constants.NAME_PREF;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_PREFIX_PREF;
import static ca.cmpt276.prj.model.Constants.IMAGE_SET_INT_PREF;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PrefsManager {
	private SharedPreferences prefs;
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

	public void saveImageSetSelected(int imageSet, String imageSetName) {
		SharedPreferences.Editor editor = prefs.edit();//Write the value

		editor.putInt(IMAGE_SET_INT_PREF, imageSet);
		editor.putString(IMAGE_SET_PREFIX_PREF, getAlpha(imageSet));
		editor.putString(IMAGE_SET_NAME_PREF, imageSetName);

		editor.apply();
	}

	public int getImageSetSelected() {
		return prefs.getInt(IMAGE_SET_INT_PREF, DEFAULT_IMAGE_SET);
	}

	public String getImageSetSelectedPrefix() {
		return prefs.getString(IMAGE_SET_PREFIX_PREF, DEFAULT_IMAGE_SET_PREFIX);
	}

	public String getImageSetSelectedName(String defaultName) {
		return prefs.getString(IMAGE_SET_NAME_PREF, defaultName);
	}

	public SharedPreferences getPrefs() {
		return prefs;
	}

	public String getName(String defaultName) {
		return prefs.getString(NAME_PREF, defaultName);
	}

	public void saveName(String Name) {
		SharedPreferences.Editor editor = prefs.edit();// Write the value
		editor.putString(NAME_PREF, Name);
		editor.apply();
	}

	public static String getAlpha(int index) {
		return index >= 0 && index < 26 ? String.valueOf((char)(index + 97)) : null;
	}
}