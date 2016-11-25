package GsonBean;

/**
 * Created by XX on 2016/5/5.
 */
public class NewsData {
    private String name;
    private String shortname;
    private String type;
    private String logoaddress;

    public String getLogoaddress() {
        return logoaddress;
    }

    public void setLogoaddress(String logoaddress) {
        this.logoaddress = logoaddress;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
