/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andymememe.crfdemo.data;

import iitb.CRF.DataIter;
import java.util.List;

/**
 *
 * @author Andy Chen
 */
public class AirDataIter implements DataIter {

    public List<AirDataSequence> data = null;
    public int pos = 0;

    public AirDataIter(List data) {
        this.data = data;
    }

    @Override
    public void startScan() {
        pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < data.size();
    }

    @Override
    public AirDataSequence next() {
        return data.get(pos++);
    }

    public int size() {
        return data.size();
    }
}
