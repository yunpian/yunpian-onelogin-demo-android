package com.yunpian.mobileauth.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qipeng.yp.onelogin.QPOneLogin;
import com.qipeng.yp.onelogin.callback.QPResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView oneLoginLl;
    private boolean isFirstPreGet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStatusBarColor();
        setContentView(R.layout.activity_main);
        initSDK();

        oneLoginLl = findViewById(R.id.one_login_ll);
        oneLoginLl.setVisibility(View.VISIBLE);

        TextView versionTv = findViewById(R.id.version_tv);
        versionTv.setText(versionTv.getText() + " " + Utils.getVersionName(this, getPackageName()));
    }

    private void initSDK() {
        QPOneLogin.getInstance().setLogEnable(true);
        QPOneLogin.getInstance().init(this, "474b4c6159e54ace9bb28ab08e8406f2", new QPResultCallback() {
            @Override
            public void onSuccess(String message) {
//                preGetToken();
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(MainActivity.this, "发生错误，失败原因：" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readyState() {
        setEnable(oneLoginLl, true);
    }

    private void disableState() {
        setEnable(oneLoginLl, false);
    }

    private void setEnable(View view, boolean enable) {
        if (view != null) {
            view.setAlpha(enable ? 1.0f : 0.5f);
            view.setEnabled(enable);
        }
    }

    public void oneLogin(View view) {
        if (!isNetworkConnected(this)) {
            Toast.makeText(MainActivity.this, "请确保网络连接正常", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(this, ActivityAuthStyleChoose.class));
    }

    private void preGetToken() {
        disableState();
//        Toast.makeText(MainActivity.this, "数据准备中...", Toast.LENGTH_SHORT).show();
        QPOneLogin.getInstance().preGetToken(new QPResultCallback() {
            @Override
            public void onSuccess(String message) {
                readyState();
                if (isFirstPreGet) {
                    isFirstPreGet = false;
//                    Toast.makeText(MainActivity.this, "准备就绪", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String message) {
                readyState();
                try {
                    JSONObject result = new JSONObject(message);
                    int errorCode = result.optInt("errorCode");
                    if (errorCode == -20206 || errorCode == -20202 || message.toLowerCase().contains("unable to resolve host") || errorCode == -40102 || errorCode == -40202 || errorCode == -40302) {
                        message = "请确保数据流量开关已开启";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.bg_activity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setNavigationBarColor(getResources().getColor(R.color.bg_activity));
        }
    }


}
