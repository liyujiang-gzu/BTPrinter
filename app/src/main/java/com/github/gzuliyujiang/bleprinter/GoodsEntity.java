package com.github.gzuliyujiang.bleprinter;

import java.io.Serializable;

/**
 * Created by liyujiang on 2019/11/09 21:00
 *
 * @author 大定府羡民
 */
public class GoodsEntity implements Serializable {
    private String id;
    private String name;
    private String number;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
