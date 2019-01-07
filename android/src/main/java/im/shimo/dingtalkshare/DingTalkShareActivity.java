package im.shimo.dingtalkshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.ShareConstant;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;
import com.android.dingtalk.share.ddsharemodule.message.SendAuth;

public class DingTalkShareActivity extends Activity implements IDDAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DingTalkShare", "onCreate==========>");
        try {
            // activity的export为true，try起来，防止第三方拒绝服务攻击
            IDDShareApi mIDDShareApi = DDShareApiFactory.createDDShareApi(this, DingTalkShareModule.getAppID(this), false);
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
            WritableMap mapSuccess = Arguments.createMap();
            mapSuccess.putString("code", authResp.code);
            DingTalkShareModule.mPromise.resolve(mapSuccess);

            break;
        default:
            DingTalkShareModule.mPromise.reject(String.valueOf((errCode)) + ": " + errMsg);
        }

        finish();
    }
}
