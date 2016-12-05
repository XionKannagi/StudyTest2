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

    Toast  toast;
    public void teaching(int pattern){

        if(toast != null)toast.cancel();
        // switch分による教示分け
        switch (pattern){
            case 1:
                toast = Toast.makeText(context,"踏み始めを緩やかにしましょう！ ",Toast.LENGTH_LONG);//.show()
                break;
            case 2:
                toast = Toast.makeText(context,"おしい！踏み始めをもう少し緩やかにしましょう！",Toast.LENGTH_LONG);//.show();
                break;
            case 3:
                toast = Toast.makeText(context,"踏み始めを強めにしましょう！",Toast.LENGTH_LONG);//.show();
                break;
            case 4:
                toast = Toast.makeText(context,"あとちょっと！踏み始めをもう少し強めにしましょう！",Toast.LENGTH_LONG);//.show();
                break;
            case 5:
               toast =  Toast.makeText(context,"急ブレーキです！気をつけてください！",Toast.LENGTH_LONG);//.show();
                break;
            case 6:
                toast =Toast.makeText(context,"いいですね！理想的です！",Toast.LENGTH_LONG);//.show();
                break;
            default:
                break;
        }

        toast.show();
    }
}
