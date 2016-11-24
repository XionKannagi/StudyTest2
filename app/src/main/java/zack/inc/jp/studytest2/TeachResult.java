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
                Toast.makeText(context,"踏み始めを緩やかにしましょう！ ",Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(context,"おしい！踏み始めをもう少し緩やかにしましょう！",Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(context,"踏み始めを強めにしましょう！",Toast.LENGTH_LONG).show();
                break;
            case 4:
                Toast.makeText(context,"あとちょっと！踏み始めをもう少し強めにしましょう！",Toast.LENGTH_LONG).show();
                break;
            case 5:
                Toast.makeText(context,"急ブレーキです！気をつけてください！",Toast.LENGTH_LONG).show();
                break;
            case 6:
                Toast.makeText(context,"いいですね！理想的です！",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
