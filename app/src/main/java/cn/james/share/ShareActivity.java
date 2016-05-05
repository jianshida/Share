package cn.james.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tencent.tauth.Tencent;

/**
 * Created by Administrator on 2016/4/21.
 */
public class ShareActivity extends Activity implements View.OnClickListener {

    private Share share;
    private TextView qq, qqzone;
    private TextView weixin, weixinFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        qq = (TextView) findViewById(R.id.share_qq);
        qqzone = (TextView) findViewById(R.id.share_qq_zone);
        weixin = (TextView) findViewById(R.id.share_weixin);
        weixinFriend = (TextView) findViewById(R.id.share_friend);
        qqzone.setOnClickListener(this);
        qq.setOnClickListener(this);
        weixinFriend.setOnClickListener(this);
        weixin.setOnClickListener(this);
        share = new Share(this, "", "1103835235", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 官方文档没没没没没没没没没没没这句代码, 但是很很很很很很重要, 不然不会回调!
        Tencent.onActivityResultData(requestCode, resultCode, data, new Share(this, "", "1103835235", "").new BaseUiListener(this));
    }

            String imgUrl = "http://images.xiangwangolf.com/data/default/badgeUrl/default_team_logo.png";
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.share_close) {
            finish();
        } else if (v.getId() == R.id.share_weixin) {
            share.shareToWechat(this, "wx60351b4bc874abba", 1, "测试", "测试", R.drawable.default_team_logo, "http://www.baidu.com");
        } else if (v.getId() == R.id.share_friend) {
            share.shareToWechat(this, "wx60351b4bc874abba", 0, "测试", "测试", R.drawable.default_team_logo, "http://www.baidu.com");
        } else if (v.getId() == R.id.share_qq) {
            share.shareToQQ(this, "测试", "测试", "http://www.baidu.com", imgUrl);
        } else if (v.getId() == R.id.share_qq_zone) {
            share.shareToQQ(this, "测试", "测试", "http://www.baidu.com", imgUrl);
        } else if (v.getId() == R.id.share_contacts) {
            share.toSms(this, "测试");
        }

    }

}
