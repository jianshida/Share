#分享
**分享** 将android手机端的内容或者信息等通过第三方平台(qq、微信、新浪微博以及短信)发送给相应的好友、朋友圈、空间等地方
### ==QQ分享==
* 开放平台注册应用（http://open.qq.com/reg）
	- 到开放平台根据projectName注册并上传规定尺寸的分享logo获得appkey等值（不注册也可分享，但是在qq聊天记录中或者qqZone中无限显示分享来源icon）
* 导包、导类
	- jar包导入（open_sdk_r5509.jar,mta-sdk-1.6.2.jar）可在腾讯开放平台里下载，或者在gradle中直接引用 compile 'com.share.qq:mta-sdk:1.6.2' compile 'com.share.qq:open_sdk:r5509'
	- 导入AccessTokenKeeper class（从demo中copy）
	- 导入Share class（从demo中copy）
* 调用具体方法
	- 调用Share中的shareToQQ()或者shareToZone()传入对应的参数（**注意：**mTargetUrl这个参数类型如：http://www.baidu.com,而不能是www.baidu.com）
	- 特别注意：必须重写以下代码
	```
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 官方文档没没没没没这句代码, 但是很很很很重要, 不然不会回调!
        Tencent.onActivityResultData(requestCode, resultCode, data, new Share(this, "", "1103835235", "").new BaseUiListener(this));
    }
		```
* 配置文件
	- AndroidManifest.xml引用AuthActivity、AssistActivity具体引用内容:
	![](http://images.xiangwangolf.com/qq_share.jpg)
	```
	   <activity
            android:name="com.tencent.tauth.AuthActivity"
            android:launchMode="singleTask"
            android:noHistory="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="tencent1103835235" />
            </intent-filter>
        </activity>
	```
	```
        <activity
            android:name="com.tencent.connect.common.AssistActivity"
            android:screenOrientation="behind"
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:configChanges="orientation|keyboardHidden">
        </activity>
	```

###==微信分享==
* 官网注册（https://open.weixin.qq.com/）
	- 到微信开放平台根据project的packageName和MD5签名(**md5签名必须去冒号并且字母小写**)注册app并上传相应尺寸icon，审核成功后会生成appId，appkey等(注册时无法立即生效，正常是隔天)，注意**：如果调用微信分享方法时，apk的签名必须和注册是的签名一致，否则报errorCode = -1 分享失败。**
* 导包、引用类
	- jar包导入（libammsdk.jar）可在微信开放平台下载或者build.gradle中直接引用 compile 'com.share.wechat:libammsdk:1.0'
	- 在工程的根目录下新建package名字必须为“wxapi”，包里引用回调类 WXEntryActivity（可在此类中做成功或者失败的相关动作）,可在demo中copy（**注意**：命名及目录级别必须严格按照上述要求，否则无法回调）
* 配置文件
	- AndroidManifest.xml中配置以下内容：
	```
        <activity
            android:name=".wxapi.WXEntryActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:exported="true"
            android:launchMode="singleInstance"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
```
* 调用分享的方法
	- 调用Share中的ShareToWechat()，传入对应参数，成功与失败在回调函数中查看