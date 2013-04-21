package edu.neu.madcourse.zhongjiemao.exerpacman.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.neu.madcourse.zhongjiemao.R;
import edu.neu.madcourse.zhongjiemao.exerpacman.game.Constants;
import edu.neu.madcourse.zhongjiemao.exerpacman.views.SeekBarPreference;

public class ExerPacmanPrefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = "ExerPacmanPrefs";
	
	private static final String OPT_MUSIC = "exer_pacman_key_music";
	private static final boolean OPT_MUSIC_DEFAULT = true;
	
	private static final String OPT_HINTS = "exer_pacman_key_hints";
	private static final boolean OPT_HINTS_DEFAULT = true;
	
	private static final String OPT_SQUAT = "exer_pacman_key_squat";
	private static final boolean OPT_SQUAT_DEFAULT = true;
	
	private static final String OPT_GAME_PACE = "exer_pacman_key_game_pace";
	private static final int OPT_GAME_PACE_DEFAULT = 45;
	
	private static final String OPT_GHOST_AGRESSIVITY = "exer_pacman_key_ghost_agressivity";
	private static final int OPT_GHOST_AGRESSIVITY_DEFAULT = 5;
	
	private static final String OPT_BEANS_LV_PREFIX = "exer_pacman_squat_lv";
	private static final String[] OPT_BEANS_LV = {
		OPT_BEANS_LV_PREFIX + "1",
		OPT_BEANS_LV_PREFIX + "2",
		OPT_BEANS_LV_PREFIX + "3",
		OPT_BEANS_LV_PREFIX + "4"
		};
	
	private CheckBoxPreference mHints;
	private CheckBoxPreference mMusic;
	private CheckBoxPreference mSquat;
	private SeekBarPreference mGamePacePrefs;
	private SeekBarPreference mGhostAgrPrefs;
	private EditTextPreference[] mBeansLv;
	
	public static boolean getMusic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEFAULT);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.exer_pacman_settings);
		getListView().setBackgroundResource(R.drawable.bg_texture);
		mHints = (CheckBoxPreference) getPreferenceScreen().findPreference(OPT_HINTS);
		mMusic = (CheckBoxPreference) getPreferenceScreen().findPreference(OPT_MUSIC);
		mSquat = (CheckBoxPreference) getPreferenceScreen().findPreference(OPT_SQUAT);
		mGamePacePrefs = (SeekBarPreference) getPreferenceScreen().findPreference(OPT_GAME_PACE);
		mGhostAgrPrefs = (SeekBarPreference) getPreferenceScreen().findPreference(OPT_GHOST_AGRESSIVITY);
		mBeansLv = new EditTextPreference[4];
		for (int i = 0; i < OPT_BEANS_LV.length; i++) {
			mBeansLv[i] = (EditTextPreference) getPreferenceScreen().findPreference(OPT_BEANS_LV[i]);
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        mMusic.setChecked(Constants.PLAY_MUSIC);
        mHints.setChecked(Constants.USE_ROTATION_HINT);
        mSquat.setChecked(Constants.SQUAT_ENABLED);
        mGamePacePrefs.setValue(100 - Constants.DELAY);
        mGhostAgrPrefs.setSummary("Ghost is " + getStringAgressivity(Constants.GHOST_AGRESSIVITY));
        mGhostAgrPrefs.setValue((int) (Constants.GHOST_AGRESSIVITY * 100f));
        for (int i = 0; i < mBeansLv.length; i++) {
        	mBeansLv[i].setSummary("You need to squat " + Constants.NUM_PILLS[i] + " times in level " + (i + 1));
        	mBeansLv[i].setText(Constants.NUM_PILLS[i] + "");
        }
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(OPT_GAME_PACE)) {
        	Constants.DELAY = 100 - mGamePacePrefs.getValue();
        } else if (key.equals(OPT_GHOST_AGRESSIVITY)) {
        	Constants.GHOST_AGRESSIVITY = ((float) mGhostAgrPrefs.getValue()) / 100f;
        	mGhostAgrPrefs.setSummary("Ghost is " + getStringAgressivity(Constants.GHOST_AGRESSIVITY));
        	Log.d(TAG, "Constants.GHOST_AGRESSIVITY: " + Constants.GHOST_AGRESSIVITY);
        } else if (key.startsWith(OPT_BEANS_LV_PREFIX)) {
        	for (int i = 0; i < OPT_BEANS_LV.length; i++) {
        		if (OPT_BEANS_LV[i].equals(key)) {
        			String val = mBeansLv[i].getText();
        			if (val == null || "".equals(val))
        				val = "0";
        			Constants.NUM_PILLS[i] = Integer.parseInt(val);
        			mBeansLv[i].setSummary("You need to squat " + Constants.NUM_PILLS[i] + " times in level " + (i + 1));
        		}
        	}
        } else if (key.equals(OPT_HINTS)) {
        	Constants.USE_ROTATION_HINT = mHints.isChecked();
        } else if (key.equals(OPT_MUSIC)) {
        	Constants.PLAY_MUSIC = mMusic.isChecked();
        } else if (key.equals(OPT_SQUAT)) {
        	Constants.SQUAT_ENABLED = mSquat.isChecked();
        }
	}
	
	private String getStringAgressivity(float agr) {
		if (agr <= 0.2f)
			return "very mild";
		else if (agr > 0.2f && agr <= 0.5f)
			return "not friendly";
		else if (agr > 0.5f && agr <= 0.7f)
			return "agressive";
		else
			return "highly agressive";
	}

}
