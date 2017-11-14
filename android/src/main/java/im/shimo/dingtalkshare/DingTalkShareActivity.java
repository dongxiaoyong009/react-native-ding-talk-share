package im.shimo.dingtalkshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDAPIEventHandler;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.BaseReq;
import com.android.dingtalk.share.ddsharemodule.message.BaseResp;

public class DingTalkShareActivity extends Activity implements IDDAPIEventHandler{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DingTalkShare" ,"onCreate==========>");
        try {
            // activity的export为true，try起来，防止第三方拒绝服务攻击
            IDDShareApi mIDDShareApi = DDShareApiFactory.createDDShareApi(this, DingTalkShareModule.getAppID(this), false);
            mIDDShareApi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DingTalkShare" , "e===========>"+e.toString());
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d("DingTalkShare", "onReq=============>" );
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int errCode = baseResp.mErrCode;
        Log.d("DingTalkShare", "errCode=============>" + errCode);
        switch (errCode){
            case BaseResp.ErrCode.ERR_OK:
                break;
        }
        finish();
    }
}
