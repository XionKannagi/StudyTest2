package zack.inc.jp.studytest2;

/**
 * Created by togane on 2016/09/05.
 */
public class SceneAnalyzer {


    private float Ax, Ay, Az;
    private double speed;
    private int result = 1;


    //運転の状態に応じて状態値を返すメソッド
    public int judgeStatus() {
        if (Az < 0.4f && speed <= 1.4) {
            return 1;//停車中が状態orブレーキ終了状態１->return 1
        } else if (speed > 1.4) {
            return 2;//走行中が状態２->return 2
        } else {
            return 3;//ブレーキ中が状態３->return 3
        }

    }

    public void mainfunc(float aX, float aY, float aZ, double v) {
        Ax = aX;
        Ay = aY;
        Az = aZ;
        speed = v;


        result = judgeStatus();

        if (result == 1){

        } else  if(result == 2) {

        } else {
            //TODO ブレーキ中の挙動->加速度のピークとその時の時間を計測
        }

    }





}
