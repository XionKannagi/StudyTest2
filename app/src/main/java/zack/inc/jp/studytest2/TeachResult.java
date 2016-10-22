package zack.inc.jp.studytest2;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by togane on 2016/10/17.
 */
public class TeachResult {

    private Context context;

    public TeachResult (Context context){
        this.context = context;
    }


    public void teaching(int pattern){

        // switch分による教示分け
        switch (pattern){
            case 1:
                Toast.makeText(context,"BRAKE_PATTERN_FRONT_PEAK",Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(context,"BREAK_PATTERN_BACK_PEAK",Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(context,"BRAKE_PATTERN_SUDDEN",Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(context,"BREAK_PATTERN_GOOD",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
