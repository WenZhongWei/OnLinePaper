package Data;

import cn.bmob.v3.BmobObject;

/**
 * Created by haiyuan 1995 on 2016/5/21.
 */

public class Comment extends BmobObject {
    private String content;
    private String newspaperId;
    private MyUser user;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(String newspaperId) {
        this.newspaperId = newspaperId;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }
}
