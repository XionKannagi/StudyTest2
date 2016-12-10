package zack.inc.jp.studytest2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText editText;
    String driverName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit_name);


    }

    public void startDrive(View v) {


        SpannableStringBuilder sp = (SpannableStringBuilder)editText.getText();
        driverName = sp.toString();


        if (driverName.length() != 0) {
            Intent intent = new Intent(this, DriveActivity.class);
            intent.putExtra("DRIVER_NAME", driverName);
            startActivity(intent);
        } else {

            Toast.makeText(this, "運転手の名前を入力して下さい", Toast.LENGTH_LONG).show();


        }

    }


}
