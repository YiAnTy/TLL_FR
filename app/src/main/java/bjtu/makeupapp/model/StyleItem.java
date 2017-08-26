package bjtu.makeupapp.model;

/**
 * Created by Logic on 2017/8/26.
 */

public class StyleItem {
    private int imageUrl;   //妆容类型照片
    private String name;       //妆容名字

    public StyleItem(int imageUrl,String name){
        this.imageUrl=imageUrl;
        this.name=name;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
