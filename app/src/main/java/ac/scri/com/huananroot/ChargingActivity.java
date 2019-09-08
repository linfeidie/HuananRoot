package ac.scri.com.huananroot;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/9/8
 * <p>
 * 版本号：HuananRoot
 */
public class ChargingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_charging);
    }
}
