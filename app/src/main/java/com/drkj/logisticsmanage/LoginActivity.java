package com.drkj.logisticsmanage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private EditText phoneNumberText;

    private EditText passwordText;

    private Button loginButton;

    private ImageView clearImage;
    private ProgressBar progressBar;
    private Button getPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View rootView = findViewById(android.R.id.content);
        SupportMultipleScreensUtil.init(getApplication());
        SupportMultipleScreensUtil.scale(rootView);
        initviews();

    }

    private void initviews() {
        phoneNumberText = (EditText) findViewById(R.id.text_phone_number);
        passwordText = (EditText) findViewById(R.id.text_password);
        loginButton = (Button) findViewById(R.id.button_login);
        clearImage = (ImageView) findViewById(R.id.image_clear);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setEnabled(false);
                attemptLogin();
            }
        });
        clearImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberText.setText("");
            }
        });

        phoneNumberText.setText(getPhone());
        progressBar = findViewById(R.id.progressbar);

        getPasswordButton = findViewById(R.id.button_get_password);
        getPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailValid(phoneNumberText.getText().toString())){

                    try {
                        String parm = "phone=" + URLEncoder.encode(phoneNumberText.getText().toString(), "UTF-8");
                        HttpUtils.post("http://106.15.57.208:18080/entrance/app/mode/sendVaildCode", parm, new HttpUtils.MyCallback() {
                            @Override
                            public void success(String result) {

                            }

                            @Override
                            public void error(Exception e) {

                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    CountDownTimerUtils timer = new CountDownTimerUtils(getPasswordButton, 60000, 1000);
                    timer.start();
                }
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        KeybordUtils.closeKeybord(phoneNumberText, this);
        KeybordUtils.closeKeybord(passwordText, this);

        // Reset errors.
        phoneNumberText.setError(null);
        passwordText.setError(null);

        // Store values at the time of the login attempt.
        final String email = phoneNumberText.getText().toString();
        final String password = passwordText.getText().toString();

        boolean cancel = false;
        View focusView = null;

//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            phoneNumberText.setError("手机号码不能为空");
            focusView = phoneNumberText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            phoneNumberText.setError("手机号码不合法");
            focusView = phoneNumberText;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            focusView = passwordText;
            cancel = true;
            passwordText.setError("验证码应为6位");
//            Toast.makeText(LoginActivity.this, "验证码应为6位", Toast.LENGTH_SHORT).show();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            loginButton.setEnabled(true);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String parm = "phone=" + URLEncoder.encode(email, "UTF-8") + "&code=" + URLEncoder.encode(password, "UTF-8");

                        HttpUtils.post("http://106.15.57.208:18080/entrance/app/mode/loginValid", parm, new HttpUtils.MyCallback() {

                            @Override
                            public void success(String result) {

                                try {
                                    final JSONObject object = new JSONObject(result);
                                    if (object.getInt("code") == 200) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                try {
                                                    JSONObject object1 = object.getJSONObject("data");
                                                    JSONObject object2 = object1.getJSONObject("assigner");
                                                    String phoneNumber = object2.getString("assignerPhone");
                                                    BaseApplication.getInstance().setPhoneNumber(phoneNumber);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                keepPhone(email);
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginButton.setEnabled(true);
                                                Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void error(Exception e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        loginButton.setEnabled(true);
                                        Toast.makeText(LoginActivity.this, "连接服务器异常", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


//                    login(email, password);
                }
            }).start();
        }
    }

    private boolean isEmailValid(String email) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isPasswordValid(String password) {

        return password.length() == 6;
    }

    public void login(final String email, String password) {
        URL url;
        HttpURLConnection connection;
        BufferedReader reader = null;
        try {
            url = new URL("http://106.15.57.208:18080/entrance/app/mode/loginValid");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(3000);
            PrintWriter writer = new PrintWriter(connection.getOutputStream());
            String parm = "phone=" + URLEncoder.encode(email, "UTF-8") + "&code=" + URLEncoder.encode(password, "UTF-8");
            writer.print(parm);
            writer.flush();
            writer.close();
            InputStream in = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            JSONObject object = new JSONObject(result.toString());
            if (object.getInt("code") == 200) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        keepPhone(email);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loginButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "连接服务器异常", Toast.LENGTH_SHORT).show();
                }
            });
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void keepPhone(String phone) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    private String getPhone() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String phone = preferences.getString("phone", "");
        phoneNumberText.setText(phone);
        return phone;
    }

    class CountDownTimerUtils extends CountDownTimer {
        private Button button;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountDownTimerUtils(Button button, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.button = button;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            button.setEnabled(false);
            button.setText(millisUntilFinished / 1000 + "s");
        }

        @Override
        public void onFinish() {
            button.setText("重新获取验证码");
            button.setEnabled(true);
        }
    }
}

