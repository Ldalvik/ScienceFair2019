package drone.siege.root.rainbowsixsiegedrone;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    private Context ctx;

    public Utils(Context ctx){
        this.ctx = ctx;
    }

    public void makeToast(String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
    public static String command(int left, int right){
        return left+","+right+"/";
    }
}
