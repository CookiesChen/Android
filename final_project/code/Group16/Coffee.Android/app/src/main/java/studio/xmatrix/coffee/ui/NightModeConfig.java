package studio.xmatrix.coffee.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class NightModeConfig {
    private SharedPreferences mSharedPreference;
    private  static final String IS_Night_AUTO = "Is_Night_Auto";
    private static final String NIGHT_MODE = "Night_Mode";
    private static final String IS_NIGHT_MODE = "Is_Night_Mode";

    private  SharedPreferences.Editor mEditor;

    private static NightModeConfig sModeConfig;

    public static NightModeConfig getInstance(){
        if(sModeConfig == null) {
            sModeConfig = new NightModeConfig();
        }
        return sModeConfig;
    }

    public boolean getNightMode(Context context){
        if (mSharedPreference == null) {
            mSharedPreference = context.getSharedPreferences(NIGHT_MODE, Context.MODE_PRIVATE);
        }
        return mSharedPreference.getBoolean(IS_NIGHT_MODE, false);
    }

    public boolean isNightAuto(Context context) {
        if (mSharedPreference == null) {
            mSharedPreference = context.getSharedPreferences(NIGHT_MODE, Context.MODE_PRIVATE);
        }
        return mSharedPreference.getBoolean(IS_Night_AUTO, false);
    }

    public void setNightAuto(Context context, boolean isNightAuto) {
        if (mSharedPreference == null) {
            mSharedPreference = context.getSharedPreferences(NIGHT_MODE, Context.MODE_PRIVATE);
        }
        mEditor = mSharedPreference.edit();
        mEditor.putBoolean(IS_Night_AUTO, isNightAuto);
        mEditor.apply();
    }

    public void setNightMode(Context context, boolean isNightMode){
        if (mSharedPreference == null) {
            mSharedPreference = context.getSharedPreferences(NIGHT_MODE, Context.MODE_PRIVATE);
        }
        mEditor = mSharedPreference.edit();
        mEditor.putBoolean(IS_NIGHT_MODE,isNightMode);
        mEditor.apply();
    }
}