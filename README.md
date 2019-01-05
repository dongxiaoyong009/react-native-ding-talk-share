
# react-native-ding-talk-share-pro

钉钉分享 react native 版 SDK

* [钉钉开放平台](https://open-doc.dingtalk.com/) 点击 `分享`

## 安装

```sh
npm install react-native-ding-talk-share-pro --save
react-native link react-native-ding-talk-share-pro
```

`package.json` 中添加 `"dt_app_id": "<your_ding_talk_app_id>",`

### Android

首先在工程中创建 `ddshare` 的 package，然后在在该 package 下创建 `DDShareActivity`，内容如下

```java
package com.qcs_nativefe.ddshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
// import android.widget.Toast;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

import im.shimo.dingtalkshare.DingTalkShareModule;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.ShareConstant;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.SendAuth;

public class DDShareActivity extends Activity implements IDDAPIEventHandler {

    private IDDShareApi mIDDShareApi;
    private static final String DING_APP_ID = "dingoaydnur9ckrjrwmhnz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            // activity的export为true，try起来，防止第三方拒绝服务攻击
            IDDShareApi mIDDShareApi = DDShareApiFactory.createDDShareApi(this, DingTalkShareModule.getAppID(this),
                    false);
            mIDDShareApi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DingTalkShare", "e===========>" + e.toString());
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d("DingTalkShare", "onReq=============>");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.mErrCode;
        String errMsg = baseResp.mErrStr;

        Log.d("DingTalkShare", "errCode=============>" + errCode);
        Log.d("DingTalkShare", "errMsg=============>" + errMsg);

        switch (errCode) {
        case BaseResp.ErrCode.ERR_OK:
            SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
            this.finish();
            // Toast.makeText(this, "授权成功，授权码为:" + authResp.code,
            // Toast.LENGTH_SHORT).show();
            WritableMap map = Arguments.createMap();
            map.putInt("errCode", baseResp.mErrCode);
            map.putString("code", authResp.code);
            DingTalkShareModule.mPromise.resolve(map);

            break;
        }

        finish();
    }
}
```

注意 package 名字，和 Activity 名字都不能改，因为钉钉是按名字去查找 Activity 的。

再在 `AndroidManifest.xml` 中添加如下内容：

```xml
<activity
    android:exported="true"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:launchMode="singleInstance"
    android:name=".ddshare.DDShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"></action>
        <category android:name="android.intent.category.DEFAULT"></category>
    </intent-filter>
</activity>
```

### iOS

参考官方文档修改 `LSApplicationQueriesSchemes` 和 `URL Types`

General > Linked Frameworks and Libraries > 添加 `node_modules/react-native-ding-talk-share-pro/ios/DTShareKit.framework`

Build Settings > Framework Search Paths > 添加 `$(SRCROOT)/../node_modules/react-native-ding-talk-share-pro/ios` `no recursive`

## Usage

```javascript
import DingTalk from 'react-native-ding-talk-share-pro';

// share web page
result = await DingTalk.shareWebPage(link, wechatURIProcess(thumb || icon), title, content);

// share image
result = await DingTalk.shareImage(image);

// get auth code
result = await DingTalk.getAuthCode();
```
