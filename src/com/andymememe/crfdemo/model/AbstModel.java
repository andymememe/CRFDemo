/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andymememe.crfdemo.model;

import com.andymememe.crfdemo.data.AirDataIter;
import com.andymememe.crfdemo.data.AirDataSequence;
import iitb.CRF.CRF;
import iitb.Model.FeatureGenImpl;
import java.util.HashMap;

/**
 *
 * @author Andy Chen
 */
public abstract class AbstModel {

    public String modelGraphType = "naive";  // "semi-markov" or "naive"

    public int nlabel = 10;

    public FeatureGenImpl featureGen;

    public CRF crfModel;

    public AirDataIter dataIter;

    public abstract void saveModel(String savePath) throws Exception;

    public abstract void loadModel(String path) throws Exception;

    public abstract void train() throws Exception;

    public abstract Stat validate(String valiDataPath, String outputPath)
            throws Exception;

    public abstract void test(String testDataPath, String outputPath)
            throws Exception;
    
    public abstract AirDataSequence test(String testWord);

    public class Stat {

        public HashMap<Integer, HashMap<Integer, Integer>> missTagStat;
        public int hit;
        public int miss;

        public Stat() {
            this.miss = 0;
            this.hit = 0;
            this.missTagStat = new HashMap<>();
        }

        public int size() {
            return hit + miss;
        }

        public void hitting() {
            hit++;
        }

        public void missing() {
            miss++;
        }

        public float hitRate() {
            return ((float) hit) / (hit + miss);
        }

        public float missRate() {
            return 1 - hitRate();
        }

        public void missing(int answer, int tagged) {
            miss++;
            if (missTagStat.containsKey(answer)) {
                HashMap<Integer, Integer> mtagged;
                mtagged = missTagStat.get(answer);
                if (mtagged.containsKey(tagged)) {
                    mtagged.put(tagged, mtagged.get(tagged) + 1);
                } else {
                    mtagged.put(tagged, 1);
                }
                missTagStat.put(answer, mtagged);
            } else {
                HashMap mtagged = new HashMap();
                mtagged.put(tagged, 1);
                missTagStat.put(answer, mtagged);
            }
        }
    }
}
