package cn.james.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/22.
 */
public final class Share implements IWeiboHandler.Response {

    private IWXAPI wxApi;
    private Tencent mTencent;
    private IWeiboShareAPI mWeiboShareAPI;

    public Share(Context context, String wechatAppId, String qqAppId, String sinaAppId) {
        wxApi = WXAPIFactory.createWXAPI(context, wechatAppId, true);
        wxApi.registerApp(wechatAppId);
        mTencent = Tencent.createInstance(qqAppId, context);
        // 创建微博 SDK 接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, sinaAppId);
    }

    // 分享短信
    public void toSms(Context context, String content) {
        int absent = TelephonyManager.SIM_STATE_ABSENT;
        if (1 == absent) {
            Toast.makeText(context, "请确认sim卡是否插入或者sim卡暂时不可用！", Toast.LENGTH_SHORT);
            return;
        }
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", content);
        sendIntent.setType("vnd.android-dir/mms-sms");
    }

    /**
     * 微信分享
     * @param context 上下文对象
     * @param appId 微信开放平台注册的appid
     * @param flag 0：发送到会话， 1：发送到朋友圈
     * @param title 分享标题
     * @param content 分享内容
     * @param resId 分享的logo图片
     * @param mTargetUrl 分享的链接地址
     */
    public void shareToWechat(Context context, String appId, int flag, String title, String content, int resId, String mTargetUrl) {
        wxApi = WXAPIFactory.createWXAPI(context, appId, true);
        wxApi.registerApp(appId);
        if (!wxApi.isWXAppInstalled()) {
            Toast.makeText(context, "您还未安装微信客户端", Toast.LENGTH_SHORT);
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mTargetUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
//        Bitmap thumb = ImageLoader.getInstance().loadImageSync(imgUrl);
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), resId);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    public void shareToQQ(Activity context, String title, String content, String mTargetUrl, String imgUrl) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mTargetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        mTencent.shareToQQ(context, params, new BaseUiListener(context));
    }

    public void shareToQzone(Activity context, String title, String content, String mTargetUrl, String imgUrl) {
        final Bundle params = new Bundle();
        //分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mTargetUrl);//必填
//        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
        ArrayList<String> list = new ArrayList<>();
        list.add(imgUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
        mTencent.shareToQzone(context, params, new BaseUiListener(context));
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {

    }

    public class BaseUiListener implements IUiListener {

        private Activity context;

        public BaseUiListener(Activity context) {
            this.context = context;
        }

        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {
            Log.e("QQ", "分享成功");
            Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT);
        }

        @Override
        public void onError(UiError e) {
            Log.e("QQ", "分享失败");
            Toast.makeText(context, e.errorMessage, Toast.LENGTH_SHORT);
//            showResult("onError:", "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            Log.e("QQ", "分享取消");
            Toast.makeText(context, "分享取消", Toast.LENGTH_SHORT);
        }
    }


    /**
     * 初始化 UI 和微博接口实例 。
     */
    public void shareToSina(Context context, String content, int resId, String sinaAppId) {
        // 获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        // 设置微博客户端相关信息
        int supportApiLevel = mWeiboShareAPI.getWeiboAppSupportAPI();
        String installInfo;
        if (isInstalledWeibo)
            installInfo = "已安装";
        else
            installInfo = "未安装";
        if (!isInstalledWeibo) {
            Toast.makeText(context, installInfo, Toast.LENGTH_SHORT).show();
            return;
        }
        // 注册到新浪微博
        boolean isSucceed = mWeiboShareAPI.registerApp();
        Log.e("isSucceed", isSucceed + "");
        sendMultiMessage(context, content, resId, sinaAppId);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     */
    private void sendMultiMessage(final Context context, String content, int resId, String sinaAppId) {
        String SCOPE = "email,direct_messages_read,direct_messages_write,"
                + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                + "follow_app_official_microblog," + "invitation_write";
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(content);
        weiboMessage.imageObject = getImageObj(context, resId);
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        AuthInfo authInfo = new AuthInfo(context, sinaAppId, "http://www.sina.com", SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest((Activity) context, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                Log.e("error", arg0.getMessage() + ",,," + arg0.getLocalizedMessage());
            }

            @Override
            public void onComplete(Bundle bundle) {
                // TODO Auto-generated method stub
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(context, newToken);
                Log.e("微博分享", "成功");
                Toast.makeText(context, "onAuthorizeComplete token = " + newToken.getToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.e("微博分享", "取消");
            }
        });
    }

    /**
     * sina创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String content) {
        TextObject textObject = new TextObject();
        textObject.text = content;
        return textObject;
    }

    /**
     * sina创建图片消息对象。
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Context context, int resId) {
        ImageObject imageObject = new ImageObject();
        //设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }
}
