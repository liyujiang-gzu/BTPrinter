package com.github.gzuliyujiang.bleprinter;

import java.io.Serializable;

/**
 * Created by liyujiang on 2019/11/04 01:20
 *
 * @author 大定府羡民
 */
public class BluetoothEntity implements Serializable {
    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
