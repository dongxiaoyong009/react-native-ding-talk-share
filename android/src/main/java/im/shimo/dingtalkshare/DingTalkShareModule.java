package im.shimo.dingtalkshare;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.ShareConstant;
import com.android.dingtalk.share.ddsharemodule.message.DDImageMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;
import com.android.dingtalk.share.ddsharemodule.message.SendAuth;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

public class DingTalkShareModule extends ReactContextBaseJavaModule implements IDDAPIEventHandler {
    private static final String TAG = "DingTalkShare";

    private static final String NOT_INSTALLED_CODE = "NOT_INSTALLED";
    private static final String NOT_SUPPORTED_CODE = "NOT_SUPPORTED";
    private static final String SHARE_FAILED_CODE = "SHARE_FAILED";
    static final String DING_SCOPE = SendAuth.Req.SNS_LOGIN;

    private static DingTalkShareModule mInstance;
    // 不能在构造方法里初始化，因为构造方法获取不到需要的 Activity.
    private static IDDShareApi mDDShareApi;
    private Promise mPromise;

    public DingTalkShareModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public static IDDShareApi getDdShareApi(Context context) {
        if (mDDShareApi == null) {
            String appId = getAppID(context);
            mDDShareApi = DDShareApiFactory.createDDShareApi(context, appId, true);
        }
        return mDDShareApi;
    }

    public static DingTalkShareModule getInstance(ReactApplicationContext reactContext) {
        if (mInstance == null) {
            mInstance = new DingTalkShareModule(reactContext);
        }
        return mInstance;
    }

    @Override
    public String getName() {
        return "RNDingTalkShareModule";
    }

    @ReactMethod
    public void isInstalled(Promise promise) {
        IDDShareApi ddShareApi = getDdShareApi(getCurrentActivity());
        promise.resolve(ddShareApi.isDDAppInstalled());
    }

    @ReactMethod
    public void isSupported(Promise promise) {
        IDDShareApi ddShareApi = getDdShareApi(getCurrentActivity());
        promise.resolve(ddShareApi.isDDSupportAPI());
    }

    @ReactMethod
    public void shareWebPage(String url, String thumbImage, String title, String content, Promise promise) {
        mPromise = promise;
        if (!checkSupport()) {
            return;
        }
        // 初始化一个DDWebpageMessage并填充网页链接地址
        DDWebpageMessage webPageObject = new DDWebpageMessage();
        webPageObject.mUrl = url;

        // 构造一个DDMediaMessage对象
        DDMediaMessage webMessage = new DDMediaMessage();
        webMessage.mMediaObject = webPageObject;

        // 填充网页分享必需参数，开发者需按照自己的数据进行填充
        webMessage.mTitle = title;
        webMessage.mContent = content;
        webMessage.mThumbUrl = thumbImage;
        // 构造一个Req
        SendMessageToDD.Req webReq = new SendMessageToDD.Req();
        webReq.mMediaMessage = webMessage;

        // 调用api接口发送消息到支付宝
        if (!getDdShareApi(getCurrentActivity()).sendReq(webReq)) {
            mPromise.reject(SHARE_FAILED_CODE, "分享失败");
        }
    }

    /**
     * 分享图片
     */
    @ReactMethod
    private void shareImage(String image, Promise promise) {
        mPromise = promise;
        if (!checkSupport()) {
            return;
        }
        // 初始化一个DDImageMessage
        DDImageMessage imageObject = new DDImageMessage();
        if (isLocalResource(image)) {
            imageObject.mImagePath = image;
        } else {
            imageObject.mImageUrl = image;
        }

        // 构造一个mMediaObject对象
        DDMediaMessage mediaMessage = new DDMediaMessage();
        mediaMessage.mMediaObject = imageObject;

        // 构造一个Req
        SendMessageToDD.Req req = new SendMessageToDD.Req();
        req.mMediaMessage = mediaMessage;

        // 调用api接口发送消息到支付宝
        if (!getDdShareApi(getCurrentActivity()).sendReq(req)) {
            mPromise.reject(SHARE_FAILED_CODE, "分享失败");
        }
    }

    @ReactMethod
    public void getAuthCode(Promise promise) {
        mPromise = promise;
        if (!checkSupport()) {
            return;
        }
        // WritableMap map = Arguments.createMap();
        // map.putInt("errCode", 1);
        // map.putString("msg", "ddd");
        // DDModule.promise.resolve(map);

        SendAuth.Req req = new SendAuth.Req();
        req.scope = DING_SCOPE;
        req.state = "test";

        // 调用api接口发送消息到支付宝
        if (!getDdShareApi(getCurrentActivity()).sendReq(req)) {
            mPromise.reject(SHARE_FAILED_CODE, "分享失败");
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq");
    }

    // @Override
    // public void onResp(BaseResp baseResp) {
    // Log.d(TAG, baseResp.mErrStr);
    // int errCode = baseResp.mErrCode;
    // switch (errCode) {
    // case BaseResp.ErrCode.ERR_OK:
    // mPromise.resolve(true);
    // break;
    // default:
    // mPromise.reject(errCode + "", baseResp.mErrStr);
    // break;
    // }
    // }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.mErrCode;
        String errMsg = baseResp.mErrStr;

        Log.d("DingTalkShare", "errCode=============>" + errCode);
        Log.d("DingTalkShare", "errMsg=============>" + errMsg);

        switch (errCode) {
        case BaseResp.ErrCode.ERR_OK:
            SendAuth.Resp authResp = (SendAuth.Resp) baseResp;
            WritableMap mapSuccess = Arguments.createMap();
            mapSuccess.putString("code", authResp.code);
            mPromise.resolve(mapSuccess);

            break;
        default:
            mPromise.reject(String.valueOf((errCode)) + ": " + errMsg);
        }
    }

    /**
     * 获取钉钉 App ID
     *
     * @param context
     * @return
     */
    public static String getAppID(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return appInfo.metaData.get("DT_APP_ID").toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handleIntent(Intent intent) {
        if (mInstance != null && mDDShareApi != null) {
            mDDShareApi.handleIntent(intent, mInstance);
        }
    }

    private boolean isLocalResource(String url) {
        Uri thumbUri = Uri.parse(url);
        // Verify scheme is set, so that relative uri (used by static resources) are not
        // handled.
        String scheme = thumbUri.getScheme();
        return (scheme == null || scheme.equals("file"));
    }

    private boolean checkSupport() {
        IDDShareApi ddShareApi = getDdShareApi(getCurrentActivity());
        if (!ddShareApi.isDDAppInstalled()) {
            mPromise.reject(NOT_INSTALLED_CODE, "请安装钉钉客户端");
            return false;
        } else if (!ddShareApi.isDDSupportAPI()) {
            mPromise.reject(NOT_SUPPORTED_CODE, "请升级钉钉客户端");
            return false;
        }
        return true;
    }
}
