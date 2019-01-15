/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andymememe.crfdemo.data;

import iitb.CRF.DataSequence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andy Chen
 */
public class AirDataSequence implements DataSequence {

    private static final long serialVersionUID = 1L;
    public int miss = 0;
    private List<Integer> labelList; // List to keep label
    private List<String> tokenList; // List to keep token
    public static List<String> tokenSet;

    public AirDataSequence() {
        
    }

    public AirDataSequence(String line, String labelFileName) {
        try {
            initToken(labelFileName);
        } catch (IOException ex) {
            Logger.getLogger(AirDataSequence.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(line);
    }

    @Override
    public int length() {
        return labelList.size();
    }

    @Override
    public int y(int i) {
        return labelList.get(i);
    }

    @Override
    public String x(int i) {
        return tokenList.get(i);
    }

    @Override
    public void set_y(int i, int label) {
        labelList.set(i, label);
    }

    public int size() {
        return length();
    }

    public int getIndex(String value) {
        int result = 0;
        for (String entry : tokenSet) {
            if (entry.equals(value)) {
                return result;
            }
            result++;
        }
        return -1;
    }
    
    public static String getToken(int id) {
        return tokenSet.get(id);
    }

    private boolean loadData(String line) {
        labelList = new ArrayList();
        tokenList = new ArrayList();
        String[] words = line.split(" ");
        try {
            for (String word : words) {
                String pair[] = word.split("/");
                if (pair.length == 2) {
                    tokenList.add(pair[0]);
                    labelList.add(getIndex(pair[1]));
                } else {
                    tokenList.add(pair[0]);
                    labelList.add(-1);
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static void initToken(String labelFilename) throws IOException {
        tokenSet = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(labelFilename)))
        {
            String line;
            while ((line = br.readLine()) != null) {
               tokenSet.add(line);
            }
        }
    }
}
