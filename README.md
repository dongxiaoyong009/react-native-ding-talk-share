
# react-native-ding-talk-share

钉钉分享 react native 版 SDK

* [钉钉开放平台](https://open-doc.dingtalk.com/) 点击 `分享`

## 安装

```sh
npm install react-native-ding-talk-share --save
react-native link react-native-ding-talk-share
```

### Android

首先在工程中创建 `ddshare` 的 package，然后在在该 package 下创建 `DDShareActivity`，内容如下

```java
import android.app.Activity;
import android.os.Bundle;

import im.shimo.dingtalkshare.DingTalkShareModule;

public class DDShareActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            DingTalkShareModule.handleIntent(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
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

## Usage

```javascript
import RNDingTalkShare from 'react-native-ding-talk-share';

// TODO: What to do with the module?
RNDingTalkShare;
```
