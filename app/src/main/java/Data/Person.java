package Data;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by XX on 2016/5/19.
 */
public class Person extends BmobObject {

    private BmobFile pictureFile;
    private MyUser userName;

    public MyUser getUserName() {
        return userName;
    }

    public void setUserName(MyUser userName) {
        this.userName = userName;
    }

    public BmobFile getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(BmobFile pictureFile) {
        this.pictureFile = pictureFile;
    }

}