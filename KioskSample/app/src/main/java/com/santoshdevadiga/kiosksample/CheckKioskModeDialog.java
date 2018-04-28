package com.santoshdevadiga.kiosksample;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;



/**
 * Created by A695905 on 26-04-2018.
 */


public class CheckKioskModeDialog extends DialogFragment
{

    Dialog dialog;
    Button btnStartKioskMode;
    private Button btn_cancel;
    KioskInterface callback;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        init();
        callback=(KioskInterface) getActivity();
        dialog=new Dialog(getActivity(),getTheme());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_kiosk_mode);
        dialog.setTitle("KioskMode");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        btnStartKioskMode= (Button) dialog.findViewById(R.id.start_kioskmode);
        btn_cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        btnStartKioskMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDeviceOwner();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,android.view.KeyEvent event) {

                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK))
                {
                    return true; // pretend we've processed it
                }
                else
                    return false; // pass on to be processed as normal
            }
        });
        return dialog;
    }

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    private PackageManager mPackageManager;
    public void init(){
        mDevicePolicyManager = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = DeviceAdminReceiver.getComponentName(getActivity());
        mPackageManager = getActivity().getPackageManager();


    }


    public void checkDeviceOwner()
    {
        if ( mDevicePolicyManager.isDeviceOwnerApp(getActivity().getApplicationContext().getPackageName()))
        {
            setupUserPolicy();

        } else
        {
            Toast.makeText(getActivity(),
                   "This app has not been given Device Owner privileges to manage this device and start lock task mode",Toast.LENGTH_SHORT)
                    .show();
            dismiss();
        }
    }

    public void setupUserPolicy(){
        if(mDevicePolicyManager.isDeviceOwnerApp(getActivity().getPackageName())){
            setDefaultCosuPolicies(true);
        }
        else {
            Toast.makeText(getActivity(),
                   "This app is not set as device owner and cannot start lock task mode",Toast.LENGTH_SHORT)
                    .show();
            dismiss();
        }
    }
    private void setDefaultCosuPolicies(boolean active){
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);

        // disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // set system update policy
        if (active){
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }

        // set this Activity as a lock task package

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{getActivity().getPackageName()} : new String[]{});

            ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE)
            {
                getActivity().startLockTask();
            }


        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            getActivity().getPackageName(), DashboardActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, getActivity().getPackageName());
        }

        callback.KioskSetupFinish();
        getDialog().dismiss();
    }

    private void setUserRestriction(String restriction, boolean disallow){
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    private void enableStayOnWhilePluggedIn(boolean enabled){
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
                            | BatteryManager.BATTERY_PLUGGED_USB
                            | BatteryManager.BATTERY_PLUGGED_WIRELESS));
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    "0"
            );
        }
    }

}
