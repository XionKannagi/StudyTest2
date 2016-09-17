package zack.inc.jp.studytest2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        Intent intent = new Intent(this, DriveActivity.class);
        if (driverName != null) {
            intent.putExtra("DRIVER_NAME", driverName);
            startActivity(intent);
        } else {
            //TODO Toastの実装


        }

    }


}
