package ac.scri.com.huananroot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 文件描述：.
 * <p>
 * 作者：Created by 林飞堞 on 2019/9/6
 * <p>
 * 版本号：HuananRoot
 */
public class LoginActivity extends AppCompatActivity {

    private EditText loginAccount_id;
    private EditText password_id;
    private Button login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.activity_login);

        loginAccount_id = findViewById(R.id.loginAccount_id);
        password_id = findViewById(R.id.password_id);
        login = findViewById(R.id.login);

    }

    public void login(View view){
        if(isUserNameAndPwdValid()) {
            if(isUserNameAndPwdOK()) {
                Intent intent = new Intent(LoginActivity.this,MainActivity2.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public boolean isUserNameAndPwdValid() {
        // 用户名和密码不得为空
        if (loginAccount_id.getText().toString().trim().equals("")) {
            Toast.makeText(this, "用户名不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (password_id.getText().toString().trim().equals("")) {
            Toast.makeText(this, "密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean isUserNameAndPwdOK() {

//        Toast.makeText(this,loginAccount_id.getText().toString(),Toast.LENGTH_SHORT);
//        Toast.makeText(this,loginAccount_id.getText().toString(),Toast.LENGTH_SHORT);
        // 用户名和密码不得为空
        if (!loginAccount_id.getText().toString().trim().equals("admin")) {
            return false;
        } else if (!password_id.getText().toString().trim().equals("123")) {
            return false;
        }
        Toast.makeText(this,"用户信息错误",Toast.LENGTH_SHORT);

        return true;
    }

}
