package com.santoshdevadiga.kiosksample;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity implements KioskInterface {

    private TextView mTextMessage;
    private Button btn_call_first_activity;
    private Button btnkioskmode;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    private DevicePolicyManager mDevicePolicyManager;
    private ActivityManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mTextMessage = (TextView) findViewById(R.id.message);
        btnkioskmode=findViewById(R.id.btnkioskmode);

        if(am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE)
        {
            btnkioskmode.setText(R.string.button_txt_enable_kiosk);
        }
        else
        {
            btnkioskmode.setText(R.string.button_txt_disable_kiosk);
        }

        btnkioskmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(am.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE)
                {
                    askAdminPassword();
                }
                else
                {
                    CheckKioskModeDialog dialog=new CheckKioskModeDialog();
                    dialog.show(getFragmentManager(),"KIOSK_MODE_DIALOG");
                }

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        btn_call_first_activity=(Button) findViewById(R.id.btn_call_first_activity);
        btn_call_first_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DashboardActivity.this,FirstActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void KioskSetupFinish() {
        btnkioskmode.setText("Disable Kiosk MODE");
    }


        public void askAdminPassword(){
            final Dialog dialog = new Dialog(DashboardActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_ask_admin_password);

            final EditText et_admin_password=(EditText) dialog.findViewById(R.id.et_admin_password);
            Button btn_proceed=(Button) dialog.findViewById(R.id.btn_proceed);
            Button btn_exit=(Button) dialog.findViewById(R.id.btn_exit);

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(!TextUtils.isEmpty(et_admin_password.getText()))
                    {
                        if(getResources().getString(R.string.device_admin_password).equalsIgnoreCase(et_admin_password.getText().toString()))
                        {

                            disableKioskMode(mDevicePolicyManager,am);
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(DashboardActivity.this, "Please enter valid password.", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            });

            btn_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });



            dialog.show();

        }

    private void disableKioskMode(DevicePolicyManager devicePolicyManager,ActivityManager activityManager)
    {
    if(devicePolicyManager!=null && activityManager!=null){
        ComponentName mAdminComponentName = DeviceAdminReceiver.getComponentName(DashboardActivity.this);
        devicePolicyManager.clearPackagePersistentPreferredActivities(mAdminComponentName, getPackageName());
        devicePolicyManager.clearDeviceOwnerApp(getApplication().getPackageName());
        if(activityManager.getLockTaskModeState()!=ActivityManager.LOCK_TASK_MODE_NONE)
        {
            this.stopLockTask();
            btnkioskmode.setText(R.string.button_txt_enable_kiosk);
           
        }

    }

    }




}
