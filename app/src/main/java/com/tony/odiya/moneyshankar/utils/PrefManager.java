package com.tony.odiya.moneyshankar.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by TONY on 12/20/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "moneyshankar-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_AGREEMENT_ACCEPTED = "IsAgreementAccepted";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsAgreementAccepted(boolean agreementState){
        editor.putBoolean(IS_AGREEMENT_ACCEPTED, agreementState);
        editor.commit();
    }

    public boolean isAgreementAccepted(){ return pref.getBoolean(IS_AGREEMENT_ACCEPTED,false);}
}
