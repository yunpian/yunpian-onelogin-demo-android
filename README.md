# 云片 移动认证 Android SDK 接入指南

一键登录是云片提供的 APP 身份验证产品。整合三大运营商的手机号网关验证服务，可以替代 APP 上的注册、登录等场景需要使用到的短信验证码，优化 APP 用户在移动设备上的使用体验。

## 1. 整体集成流程

以下为一键登录的集成流程，整个集成流程是顺序进行的，在下一个步骤开始前请确保上一个步骤的操作都已正确完成。

#### 1.1. 获取应用 AppId

1、访问[云片官网](https://www.yunpian.com/entry?method=register)进行注册账号，联系客服或者销售申请开通移动认证服务。

2、成功开通服务后，进入[管理控制台](https://www.yunpian.com/admin/main)，进入移动认证的产品管理页面。

3、选择**新增应用**，填写应用名称以及 BundleId，系统会为该产品分配 **AppId**，应用进入审核状态，并联系客服进行审核，等待审核完成。

#### 2. 集成客户端 SDK

客户端 SDK 支持 **Android**、**iOS** 两大平台，最低支持的系统版本 A：Android 4.1、iOS 9.0 及以上，涉及到网站主的两个 API 请求以及与服务端的几个 API 请求。客户端 SDK 的业务使用流程为：

1、调用初始化接口，初始化一键登录 SDK。

2、在初始化接口的回调中，调用预取号接口。

3、调用一键登录接口，完成一键登录，将成功回调的 cid 返回给开发者自己的服务端。

####  3. 从接口获取校验结果(开发者服务端)

开发者服务端从客户端请求解析到相关参数后，接口能正确返回校验结果，即代表集成成功。

[云片移动认证服务端接入文档](https://github.com/yunpian/yunpian-onelogin-demo-android/blob/master/云片移动认证服务端接入文档.md)

## 开始使用

### 集成

需要确保主项目 build.gradle 文件中声明了 jcenter() 配置

```
implementation 'com.yunpian:onelogin:2.0.1.6'
```

#### 初始化

使用开发者自己的 appId 进行初始化 SDK

```java
QPOneLogin.getInstance().init(context, "your appId", callback);
```

#### 预取号

可以在取号之前预取号，避免取号时间太长

```java
QPOneLogin.getInstance().preGetToken(callback);
```

#### 取号

```java
QPOneLogin.getInstance().requestToken(themeConfig,new AbsQPResultCallback(){
		@Override
		public void onSuccess(String message) {

		}

		@Override
		public void onFail(String message) {

		}

		@Override
		public void onAuthActivityCreate(Activity activity) {
		    // 授权页面拉起回调
		}

		@Override
		public void onAuthWebActivityCreate(Activity activity) {
		    // 隐私条款页面拉起回调
		}

		@Override
		public void onLoginButtonClick() {
		    // 登录按钮点击回调
		}

		@Override
		public void onPrivacyCheckBoxClick(boolean isChecked) {
		    // CheckBox点击回调
		}

		@Override
		public void onPrivacyClick(String name, String url) {
		    // 隐私条款点击回调
		}
});
```

#### 关闭授权页面

```java
QPOneLogin.getInstance().dismissAuthActivity();
```

#### 打开短信回退界面

```java
QPOneLogin.getInstance().requestSmsToken(callback);
```

### 注册短信界面生命周期回调接口

```java
QPOneLogin.getInstance().registerSmsAuthActivityLifecycleCallback(lifecycleCallbacks);
```

### 自定义短信回退界面

覆盖相关布局文件或者资源文件即可。然后在短信界面的生命周期回调接口的 onActivityCreated  方法编写相关逻辑，例如

```java
QPOneLogin.getInstance().registerSmsAuthActivityLifecycleCallback(new SimpleActivityLifecycleCallback() {
            @Override
            public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
                LinearLayout rootLl = activity.findViewById(R.id.root);
                LinearLayout customView = (LinearLayout) getLayoutInflater().inflate(R.layout.custom_login, null);
                // 自定义逻辑
            }
        });
}
```

#### 设置日志输出

```java
QPOneLogin.getInstance().setLogEnable(enable);
```

#### 开发者自定义认证界面控件

在 requestToken() 方法之前调用。允许开发者在授权页面 titlebar 和 body 添加自定义的控件
注意：自定义的控件不允许覆盖 SDK 默认的 UI。

```java
QPOneLogin.getInstance().addOneLoginRegisterViewConfig(id, authRegisterViewConfig);
```

#### AuthRegisterViewConfig 接口

| 方法名 | 传参类型 | 说明 |
| --- | ------- | --- |
| setView | View | 开发者传入自定义的控件，需要设置好控件的布局属性，SDK 只支持 RelativeLayout 布局 |
| setRootViewId | int | 设置控件的位置。RootViewId.ROOT_VIEW_ID_TITLE_BAR 指标题栏，RootViewId.ROOT_VIEW_ID_BODY 指授权页空白处 |
| setCustomInterface | CustomInterface | 设置控件事件。CustomInterface是一个接口，实现了点击方法 |

#### 自定义 UI

设置背景图片：setAuthBGImgPath(String authBGImgPath)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| authBGImgPath | String | 设置背景图片。放在drawable文件下，以下背景图片路径与之保持一致 | gt_one_login_bg |

设置弹窗主题相关：setDialogTheme(boolean isDialogTheme, int dialogWidth, int dialogHeight, int dialogX, int dialogY, boolean isDialogBottom, boolean isWebViewDialogTheme)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| isDialogTheme | boolean | 是否使用弹窗模式，true为使用，false为不使用 | false |
| dialogWidth | int | 弹窗模式宽度，长宽的单位都为dp，以下单位与之保持一致 | 300 |
| dialogHeight | int | 弹窗模式高度 | 500 |
| dialogX | int | 授权⻚弹窗X偏移量（以屏幕⼼为原点） | 0 |
| dialogY | int | 授权⻚弹窗Y偏移量（以屏幕⼼为原点） | 0 |
| isDialogBottom | boolean | 授权⻚弹窗是否贴于屏幕底部true：显示到屏幕底部， dialogY参数设置将⽆效 false：不显示到屏幕底部，以 dialogY参数为准 | false |
| isWebViewDialogTheme | boolean | 隐私页面是否使用弹窗模式 | false |

设置状态栏颜色、字体颜色：setStatusBar(int statusBarColor,int navigationBarColor,boolean isLightColor)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| statusBarColor | int | 自定义状态栏背景颜色 | 0 |
| navigationBarColor | int | 自定义底部导航栏背景颜色 | 0 |
| isLightColor | boolean | 设置状态栏内容的颜色（只能黑白），true为黑色，false为白色 | false |

设置标题栏布局：setAuthNavLayout(int navColor, int authNavHeight, boolean authNavTransparent, boolean authNavGone)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| navColor | int | 标题栏颜色 | 0xFF3973FF |
| authNavHeight | int | 标题栏高度 | 49 |
| authNavTransparent | boolean | 标题栏是否透明 | true |
| authNavGone | boolean | 标题栏是否隐藏，此处的隐藏为View.GONE | false |

设置标题栏中间文字相关：setAuthNavTextView(String navText, int navTextColor, int navTextSize, boolean navTextNormal, String navWebViewText, int navWebViewTextColor, int navWebViewTextSize)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| navText | String | 文字设置 | 一键登录 |
| navTextColor | int | 字体颜色 | 0xFFFFFFFF |
| navTextSize | int | 字体大小，单位为sp,以下设置字体大小的单位与之保持一致 | 17 |
| navTextNormal | boolean | 设置是否隐私条款页面的标题栏中间文字使用默认值，true为使用navWebViewText，false为使用默认隐私条款的名字 | false |
| navWebViewText | String | 隐私条款页面的标题栏中间文字 | 服务条款 |
| navWebViewTextColor | int | 隐私条款页面的标题栏中间文字颜色 | 0xFF000000 |
| navWebViewTextSize | int | 隐私条款页面的标题栏中间文字大小 | 17 |

设置标题栏返回按钮相关：setAuthNavReturnImgView(String navReturnImgPath, int returnImgWidth, int returnImgHeight, boolean navReturnImgHidden, int returnImgOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| navReturnImgPath | String | 文字设置 | gt_one_login_ic_chevron_left_black |
| returnImgWidth | int | 返回按钮图片宽度 | 24 |
| returnImgHeight | int | 返回按钮图片高度 | 24 |
| navReturnImgHidden | boolean | 返回按钮是否隐藏 | false |
| returnImgOffsetX | int | 返回按钮图片距离屏幕左边X轴偏移量 | 12 |

设置LOGO相关：setLogoImgView(String logoImgPath, int logoWidth, int logoHeight, boolean logoHidden, int logoOffsetY, int logoOffsetY_B, int logoOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| logoImgPath | String | logo图片 | gt_one_login_logo |
| logoWidth | int | logo图片宽度 | 71 |
| logoHeight | int | logo图片高度 | 71 |
| logoHidden | boolean | logo是否隐藏 | false |
| logoOffsetY | int | logo相对于状态栏下边缘y偏移 | 125 |
| logoOffsetY_B | int | logo相对于底部y偏移 | 0 |
| logoOffsetX | int | logo相对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |

设置号码相关：setNumberView(int numberColor, int numberSize, int numFieldOffsetY, int numFieldOffsetY_B, int numFieldOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| numberColor | int | 号码栏字体颜色 | 0xFF3D424C |
| numberSize | int | 号码栏字体大小 | 24 |
| numFieldOffsetY | int | 号码栏相对于标题栏下边缘y偏移 | 200 |
| numFieldOffsetY_B | int | 号码栏相对于底部y偏移 | 0 |
| numFieldOffsetX | int | 号码栏相对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |

设置号码栏字体相关：setNumberViewTypeface(Typeface numberViewTypeface)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| numberViewTypeface | Typeface | 号码栏的文字的字体 | Typeface.DEFAULT |

设置切换账号相关：setSwitchView(String switchText, int switchColor, int switchSize, boolean switchAccHidden, int switchAccOffsetY, int switchOffsetY_B, int switchOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| switchText | String | 切换账号文字 | 切换账号 |
| switchColor | int | 切换账号字体颜色 | 0xFF3973FF |
| switchSize | int | 切换账号字体大小 | 14 |
| switchAccHidden | boolean | 切换账号是否隐藏 | false |
| switchAccOffsetY | int | 切换账号相对于标题栏下边缘y偏移 | 249 |
| switchOffsetY_B | int | 切换账号相对于底部y偏移 | 0 |
| switchOffsetX | int | 切换账号相对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |

设置切换账号字体相关：setSwitchViewTypeface(Typeface switchViewTypeface)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| switchViewTypeface | Typeface | 切换账号的文字的字体 | Typeface.DEFAULT |

设置登录按钮布局：setLogBtnLayout(String loginImgPath, int logBtnWidth, int logBtnHeight, int logBtnOffsetY, int logBtnOffsetY_B, int logBtnOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| loginImgPath | String | 登录按钮背景图片 | gt_one_login_btn_normal |
| logBtnWidth | int | 登录按钮宽度 | 268 |
| logBtnHeight | int | 登录按钮高度 | 36 |
| logBtnOffsetY | int | 登录按钮相对于标题栏下边缘y偏移 | 249 |
| logBtnOffsetY_B | int | 登录按钮相对于底部y偏移 | 0 |
| logBtnOffsetX | int | 登录按钮相对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |

设置登录按钮中间文字相关：setLogBtnTextView(String loginButtonText, int loginButtonColor, int logBtnTextSize)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| loginButtonText | String | 文字设置 | 一键登录 |
| loginButtonColor | int | 文字颜色 | 0xFFFFFFFF |
| logBtnTextSize | int | 文字大小 | 15 |

设置登录按钮中间文字的字体相关：setLogBtnTextViewTypeface(Typeface logBtnTextViewTypeface)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| logBtnTextViewTypeface | Typeface | 登录按钮中间的文字的字体 | Typeface.DEFAULT |

设置loading图片相关：setLogBtnLoadingView(String loadingView, int loadingViewWidth, int loadingViewHeight, int loadingViewOffsetRight)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| loadingView | String | loading图片地址 | 一键登录 |
| loadingViewWidth | int | loading图片宽度 | 20 |
| loadingViewHeight | int | loading图片高度 | 20 |
| loadingViewOffsetRight | int | loading图片距离屏幕右边X轴偏移量 | 12 |

设置Slogan相关：setSloganView(int sloganColor, int sloganSize, int sloganOffsetY, int sloganOffsetY_B, int sloganOffsetX)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| sloganColor | int | 设置Slogan字体颜色 | 20 |
| sloganSize | int | 设置Slogan字体大小 | 10 |
| sloganOffsetY | int | 设置Slogan相对于标题栏下边缘y偏移 | 382 |
| sloganOffsetY_B | int | 设置Slogan相对于底部y偏移 | 0 |
| sloganOffsetX | int | 设置Slogan相对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |

设置slogan文字的字体相关：setSloganViewTypeface(Typeface sloganViewTypeface)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| sloganViewTypeface | Typeface | slogan的文字的字体 | Typeface.DEFAULT |

设置隐私条款布局：setPrivacyLayout(int privacyLayoutWidth, int privacyOffsetY, int privacyOffsetY_B, int privacyOffsetX,boolean isUseNormalWebActivity)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| privacyLayoutWidth | int | 设置隐私条款宽度，隐私条款大部分为字体，只规定宽度，高度自适应 | 256 |
| privacyOffsetY | int | 设置隐私条款相对于标题栏下边缘y偏移 | 0 |
| privacyOffsetY_B | int | 设置隐私条款相对于底部y偏移	 | 18 |
| privacyOffsetX | int | 设置隐私条款对于屏幕左边X轴偏移量,当为0时表示居中显示 | 0 |
| isUseNormalWebActivity | boolean | 设置是否跳转到默认的隐私条款页面 | true |

设置隐私条款选择框相关：setPrivacyCheckBox(String unCheckedImgPath, String checkedImgPath, boolean privacyState, int privacyCheckBoxWidth, int privacyCheckBoxHeight)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| unCheckedImgPath | String | 设置未选中下按钮的图片地址| gt_one_login_unchecked |
| checkedImgPath | String | 设置选中下按钮的图片地址 | gt_one_login_checked |
| privacyState | boolean | 设置选择框是否默认选中 | true |
| privacyCheckBoxWidth | int | 选择框图片宽度 | 9 |
| privacyCheckBoxHeight | int | 选择框图片高度 | 9 |

设置隐私条款字体相关：setPrivacyClauseView(int baseClauseColor, int color, int privacyClauseTextSize)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| baseClauseColor | int | 设置隐私条款基础文字颜色	 | 0xFFA8A8A8 |
| color | int | 设置隐私条款协议文字颜色 | 0xFF3973FF |
| privacyClauseTextSize | int | 设置隐私条款字体大小 | 10 |

设置除了隐私条款其他的字体相关：setPrivacyTextView(String privacyTextViewTv1, String privacyTextViewTv2, String privacyTextViewTv3, String privacyTextViewTv4)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| privacyTextViewTv1 | String | 设置隐私条款文字1 | 登录即同意 |
| privacyTextViewTv2 | String | 设置隐私条款文字2 | 和 |
| privacyTextViewTv3 | String | 设置隐私条款文字3 | 、 |
| privacyTextViewTv4 | String | 设置隐私条款文字4 | 并使用本机号码登录 |

设置隐私栏文字的字体相关：setPrivacyClauseViewTypeface(Typeface privacyClauseBaseTextViewTypeface,Typeface privacyClauseTextViewTypeface)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| privacyClauseBaseTextViewTypeface | Typeface | 隐私栏基础的文字的字体 | Typeface.DEFAULT |
| privacyClauseTextViewTypeface | Typeface | 隐私条款的文字的字体 | Typeface.DEFAULT |

设置开发者隐私条款相关。自定义隐私条款顺序。按顺序设置，当有条款名称与条款URL一起为空时，则使用默认的运营商隐私条款。否则按照顺序排列，自定义隐私条款最多只能两个。：setPrivacyClauseText(String clauseNameOne, String clauseUrlOne, String clauseNameTwo, String clauseUrlTwo, String clauseNameThree, String clauseUrlThree)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| clauseNameOne | String | 设置隐私条款1名称 | null |
| clauseUrlOne | String | 设置隐私条款1URL | null |
| clauseNameTwo | String | 设置隐私条款2名称 | null |
| clauseUrlTwo | String | 设置隐私条款2URL | null |
| clauseNameThree | String | 设置隐私条款3名称 | null |
| clauseUrlThree | String | 设置隐私条款3URL | null |

设置未同意隐私条款的文字提示相关：setPrivacyUnCheckedToastText(String privacyUnCheckedToastText)

| 参数 | 参数类型 | 说明 | 默认值 |
| --- | ------- | --- | ----- |
| privacyUnCheckedToastText | String | 设置未同意隐私条款的文字提示相关	 | 请同意服务条款	 |

#### 错误码

| 响应码  |  具体描述
| ------ | ------------------------------------
| 200    | 成功
| -1     | 未知错误
| -2     | AppId 不能为空，请检查是否初始化 SDK
| -3     | 初始化失败
| -20101 | app_id 未传
| -20102 | 当前上下文未传
| -20105 | 预取号超时或者拉起授权页超时
| -20106 | 取号时更换了 SIM 卡
| -20200 | 当前网络不可用
| -20201 | 当前手机没有电话卡
| -20202 | 当前没有开启流量
| -20203 | 未获取到运营商
| -20204 | 获取运营商错误
| -20205 | 预取号超时
| -20206 | 没有预取号成功就进行取号
| -20301 | 退出取号页面
| -20302 | 按返回键退出取号页面
| -20303 | 切换账号登陆方式
| -20501 | 当前上下文未传
| -20502 | 授权页未配置
| -20503 | 授权页面加载异常
| -40101 | 移动运营商预取号失败
| -40102 | 移动运营商取号失败
| -40201 | 联通运营商预取号失败
| -40202 | 联通运营商取号失败
| -40301 | 电信运营商预取号失败
| -40302 | 电信运营商取号失败
| -50100 | SDK内部请求pre_get_token接口返回异常
| -50101 | SDK内部请求pre_get_token接口解密失败

## 效果演示

#### 一键登录

![](https://github.com/yunpian/yunpian-onelogin-demo-android/blob/master/image/onelogin.jpeg)
### 短信验证
![](https://github.com/yunpian/yunpian-onelogin-demo-android/blob/master/image/sms.jpeg)