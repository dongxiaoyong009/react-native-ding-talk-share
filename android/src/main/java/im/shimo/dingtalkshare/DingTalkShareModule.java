package im.shimo.dingtalkshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class DingTalkShareModule extends ReactContextBaseJavaModule implements IDDAPIEventHandler {
    public static DingTalkShareModule mInstance;

    private IDDShareApi mIDDShareApi;
    private Promise mPromise;

    public DingTalkShareModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mIDDShareApi = DDShareApiFactory.createDDShareApi(reactContext, getAppID(reactContext), true);
        if (mInstance == null) {
            mInstance = this;
        }
    }

    @Override
    public String getName() {
        return "RNDingTalkShareModule";
    }

    @ReactMethod
    public void shareWebPage(String url, String thumbImage, String title, String content, Promise promise) {
        mPromise = promise;
        if (!checkSupport()) {
            mPromise.reject("NOT_SUPPORTED", "钉钉未安装或者版本不支持");
            return;
        }
        //初始化一个DDWebpageMessage并填充网页链接地址
        DDWebpageMessage webPageObject = new DDWebpageMessage();
        webPageObject.mUrl = url;

        //构造一个DDMediaMessage对象
        DDMediaMessage webMessage = new DDMediaMessage();
        webMessage.mMediaObject = webPageObject;

        //填充网页分享必需参数，开发者需按照自己的数据进行填充
        webMessage.mTitle = title;
        webMessage.mContent = content;
        webMessage.mThumbUrl = thumbImage;
        //构造一个Req
        SendMessageToDD.Req webReq = new SendMessageToDD.Req();
        webReq.mMediaMessage = webMessage;

        //调用api接口发送消息到支付宝
        mIDDShareApi.sendReq(webReq);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.mErrCode;
        switch (errCode) {
            case BaseResp.ErrCode.ERR_OK:
                mPromise.resolve(true);
                break;
            default:
                mPromise.reject(errCode + "", baseResp.mErrStr);
                break;
        }
    }

    /**
     * 获取钉钉 App ID
     * @param context
     * @return
     */
    public static String getAppID(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.get("DD_APP_ID").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handleIntent(Intent intent) {
        if (mInstance != null) {
            mInstance.mIDDShareApi.handleIntent(intent, mInstance);
        }
    }

    private boolean checkSupport() {
        if (!mIDDShareApi.isDDAppInstalled()) {
            Toast.makeText(getReactApplicationContext(), "请安装钉钉客户端", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!mIDDShareApi.isDDSupportAPI()) {
            Toast.makeText(getReactApplicationContext(), "请升级钉钉客户端版本", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
