package com.yunpian.mobileauth.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cmic.sso.sdk.AuthRegisterViewConfig;
import com.cmic.sso.sdk.utils.rglistener.CustomInterface;
import com.geetest.onelogin.OneLoginHelper;
import com.geetest.onelogin.config.OneLoginThemeConfig;
import com.qipeng.yp.onelogin.QPOneLogin;
import com.qipeng.yp.onelogin.callback.AbsQPResultCallback;
import com.qipeng.yp.onelogin.callback.QPResultCallback;
import com.qipeng.yp.onelogin.callback.SimpleActivityLifecycleCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAuthStyleChoose extends AppCompatActivity {

    private View immersiveMode;
    private View dialogMode;
    private View floatMode;
    private View landscapeMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_auth_style_choose);
        initToolbar();

        immersiveMode = findViewById(R.id.immersive_mode);
        dialogMode = findViewById(R.id.dialog_mode);
        floatMode = findViewById(R.id.float_mode);
        landscapeMode = findViewById(R.id.landscape_mode);

        landscapeMode.setBackgroundResource(R.drawable.bg_button_white);
    }

    private void readyState() {
        setEnable(immersiveMode, true);
        setEnable(dialogMode, true);
        setEnable(floatMode, true);
        setEnable(landscapeMode, true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void disableState() {
        setEnable(immersiveMode, false);
        setEnable(dialogMode, false);
        setEnable(floatMode, false);
        setEnable(landscapeMode, false);
    }

    private void setEnable(View view, boolean enable) {
        if (view != null) {
            view.setAlpha(enable ? 1.0f : 0.5f);
            view.setEnabled(enable);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.choose_bg));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setNavigationBarColor(getResources().getColor(R.color.choose_bg));
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void landscapeMode(View view) {
        immersiveMode(view);
    }

    public void floatMode(View view) {
        immersiveMode(view);
    }

    public void popDialogMode(View view) {
        immersiveMode(view);
    }

    public void immersiveMode(final View view) {
        disableState();

        View customView = getLayoutInflater().inflate(R.layout.custom_login, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, 0, 0, Utils.dip2px(this, 80));
        layoutParamsOther.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        customView.setLayoutParams(layoutParamsOther);
        customView.findViewById(R.id.login_wechat_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityAuthStyleChoose.this, "微信登录成功", Toast.LENGTH_SHORT).show();
            }
        });
        customView.findViewById(R.id.login_weibo_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityAuthStyleChoose.this, "微博登录成功", Toast.LENGTH_SHORT).show();
            }
        });
        customView.findViewById(R.id.login_sms_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsLogin();
            }
        });

        AuthRegisterViewConfig defaultAuthRegisterViewConfig = new AuthRegisterViewConfig.Builder()
                .setCustomInterface(new CustomInterface() {
                    @Override
                    public void onClick(Context context) {
                        Log.d("", "");
                    }
                })
                .setView(null)
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .build();
        QPOneLogin.getInstance().addOneLoginRegisterViewConfig("title_btn", defaultAuthRegisterViewConfig);

        OneLoginThemeConfig oneLoginThemeConfig;
        // 沉浸模式添加自定义入口
        if (view.getId() == R.id.immersive_mode) {
            AuthRegisterViewConfig authRegisterViewConfig = new AuthRegisterViewConfig.Builder()
                    .setCustomInterface(new CustomInterface() {
                        @Override
                        public void onClick(Context context) {
                            Log.d("", "");
                        }
                    })
                    .setView(customView)
                    .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                    .build();
            QPOneLogin.getInstance().addOneLoginRegisterViewConfig("title_btn", authRegisterViewConfig);
            oneLoginThemeConfig = new OneLoginThemeConfig.Builder()
                    .setStatusBar(0xFFFFFFFF, 0xFFFFFFFF, true)
                    .setAuthNavReturnImgView("qp_ic_arrow_back", 24, 24, false, 12)
                    .setLogoImgView("ic_launcher", 88, 88, false, 100, 0, 0)
                    .setNumberView(0xFF3D424C, 24, 168, 0, 0) // 电话号码
                    .setSloganView(0x994A4A4A, 14, 216, 0, 0) // 认证服务 slogan
                    .setPrivacyClauseView(0xFF4A4A4A, 0xFF3973FF, 10)
                    .setPrivacyCheckBox("gt_one_login_unchecked", "gt_one_login_checked", false, 11, 11)
                    .setSwitchView("切换账号", 0xFF3973FF, 14, true, 249, 0, 0) // 切换账号
                    .setLogBtnLayout("bg_button", 268, 48, 260, 0, 0) // 一键登录 按钮
                    .setPrivacyLayout(300, 320, 0, 0, true) // 服务条款
                    .build();
        } else if (view.getId() == R.id.dialog_mode) {
            oneLoginThemeConfig = new OneLoginThemeConfig.Builder()
                    .setDialogTheme(true, 300, 500, 0, 0, false, false)
                    .setLogoImgView("ic_launcher", 88, 88, false, 80, 0, 0)
                    .setSwitchView("切换账号", 0xFF3973FF, 14, true, 249, 0, 0) // 切换账号
                    .setPrivacyCheckBox("gt_one_login_unchecked", "gt_one_login_checked", false, 11, 11)
                    .setPrivacyLayout(300, 0, 18, 0, true) // 服务条款
                    .build();
        } else if (view.getId() == R.id.float_mode) {
            oneLoginThemeConfig = new OneLoginThemeConfig.Builder()
                    .setDialogTheme(true, Utils.getScreenWidthForDp(this), 500, 0, 0, true, false)
                    .setLogoImgView("ic_launcher", 88, 88, false, 80, 0, 0)
                    .setSwitchView("切换账号", 0xFF3973FF, 14, true, 249, 0, 0) // 切换账号
                    .setPrivacyCheckBox("gt_one_login_unchecked", "gt_one_login_checked", false, 11, 11)
                    .setPrivacyLayout(300, 0, 18, 0, true) // 服务条款
                    .build();
        } else {
            oneLoginThemeConfig = new OneLoginThemeConfig.Builder()
                    .setStatusBar(0xFFFFFFFF, 0xFFFFFFFF, true)
                    .setAuthNavReturnImgView("qp_ic_arrow_back", 24, 24, false, 12)
                    .setLogoImgView("ic_launcher", 88, 88, false, 20, 0, 0)
                    .setNumberView(0xFF3D424C, 24, 88, 0, 0) // 电话号码
                    .setSloganView(0x994A4A4A, 11, 136, 0, 0) // 认证服务 slogan
                    .setPrivacyClauseView(0xFF4A4A4A, 0xFF3973FF, 10)
                    .setPrivacyCheckBox("gt_one_login_unchecked", "gt_one_login_checked", false, 11, 11)
                    .setSwitchView("切换账号", 0xFF3973FF, 14, true, 169, 0, 0) // 切换账号
                    .setLogBtnLayout("bg_button", 268, 48, 180, 0, 0) // 一键登录 按钮
                    .setPrivacyLayout(300, 152, 0, 0, true) // 服务条款
                    .build();
            OneLoginHelper.with().setRequestedOrientation(this, false);
        }

        QPOneLogin.getInstance().requestToken(oneLoginThemeConfig, new AbsQPResultCallback() {
            @Override
            public void onSuccess(String message) {
                readyState();
                QPOneLogin.getInstance().dismissAuthActivity();
                startActivity(new Intent(ActivityAuthStyleChoose.this, ActivityAuthResult.class));
            }

            @Override
            public void onFail(String message) {
                readyState();
                String messageResult = "OneLogin fail message = " + message;
                Log.d("kkk", "message = " + messageResult);
                try {
                    JSONObject result = new JSONObject(message);
                    int errorCode = result.optInt("errorCode");
                    if (errorCode == -20303) {
                        return;
                    }
                    if (errorCode == -20301 || errorCode == -20302) {
                        messageResult = "用户手动取消验证";
                        return;
                    }
                    if (errorCode == -20202 || errorCode == -20201) {
                        messageResult = "请确保数据流量开关已开启，然后重试";
                    }
                    if (errorCode == -20205) {
                        messageResult = "连接超时";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ActivityAuthStyleChoose.this, messageResult, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthActivityCreate(Activity activity) {
                Log.d("qipeng_log", "onAuthActivityCreate");
                if (view.getId() == R.id.landscape_mode) {
                    OneLoginHelper.with().setRequestedOrientation(activity, false);
                }
            }

            @Override
            public void onAuthWebActivityCreate(Activity activity) {
                Log.d("qipeng_log", "onAuthWebActivityCreate");
            }

            @Override
            public void onLoginButtonClick() {
                Log.d("qipeng_log", "onLoginButtonClick");
            }

            @Override
            public void onPrivacyCheckBoxClick(boolean isChecked) {
                Log.d("qipeng_log", "onPrivacyCheckBoxClick");
            }

            @Override
            public void onPrivacyClick(String name, String url) {
                Log.d("qipeng_log", "onPrivacyClick");
            }
        });
    }

    private void smsLogin() {
        if (!isNetworkConnected(this)) {
            Toast.makeText(ActivityAuthStyleChoose.this, "请确保网络连接正常", Toast.LENGTH_SHORT).show();
            return;
        }
        // 自定义短信界面
        QPOneLogin.getInstance().registerSmsAuthActivityLifecycleCallback(new SimpleActivityLifecycleCallback() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                // 调整虚拟导航栏颜色
                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.setNavigationBarColor(Color.WHITE);
                }

                LinearLayout customView = activity.findViewById(R.id.custom_login_ll);
                customView.findViewById(R.id.login_wechat_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ActivityAuthStyleChoose.this, "微信登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
                customView.findViewById(R.id.login_weibo_iv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ActivityAuthStyleChoose.this, "微博登录成功", Toast.LENGTH_SHORT).show();
                    }
                });
                ImageView loginBtn = customView.findViewById(R.id.login_sms_iv);
                loginBtn.setImageResource(R.drawable.ic_login_onelogin);
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });
            }
        });
        QPOneLogin.getInstance().requestSmsToken(new QPResultCallback() {
            @Override
            public void onSuccess(String message) {
                try {
                    JSONObject result = new JSONObject(message);
                    if (result.has("cid")) {
                        Toast.makeText(ActivityAuthStyleChoose.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    }
                    if (result.optBoolean("passed")) {
                        Toast.makeText(ActivityAuthStyleChoose.this, "短信验证成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityAuthStyleChoose.this, ActivityAuthResult.class);
                        intent.putExtra("title", "短信验证成功");
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String message) {
                try {
                    JSONObject result = new JSONObject(message);
                    if (result.optInt("status") == 1001) {
//                        Toast.makeText(ActivityAuthStyleChoose.this, "用户手动取消验证", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String msg = result.optString("msg", "短信验证失败");
                    Toast.makeText(ActivityAuthStyleChoose.this, msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

}
