package GsonBean;

/**
 * Created by XX on 2016/5/6.
 */
public class SearchData {
    private String page;
    private String name;
    private String shortname;
    private String date;
    private String serveraddress;

    public String getServeraddress() {
        return serveraddress;
    }

    public void setServeraddress(String serveraddress) {
        this.serveraddress = serveraddress;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
