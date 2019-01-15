/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andymememe.crfdemo;

import com.andymememe.crfdemo.model.AbstModel.Stat;
import com.andymememe.crfdemo.model.AirModel;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andy Chen
 */
public class CRFDemo {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            String trainData = "data/train_data.txt";
            String valData = "data/validate_data.txt";
            String valTagData = "data/validate_tag.txt";
//            String testData = "data/test_data.txt";
//            String testTagData = "data/test_tag.txt";
            String outPath = "data/model";
            AirModel model = new AirModel();
            File savedModel = new File(outPath + "/crf.txt");
            File savedFeature = new File(outPath + "/features.txt");
            long st;
            long et;
            float rt;
            
            /* Get model */
            if(!savedModel.exists() || !savedFeature.exists()) {
                /*Training model*/
                model.parseTrainData(trainData);
                model.train();
                model.saveModel(outPath);
            }
            else {
                model.loadModel(outPath);
                System.out.println("\t[System] Model loaded!");
            }

            /* Validation model */
            st = System.currentTimeMillis();
            Stat pi = model.validate(valData, valTagData);
            et = System.currentTimeMillis();
            rt = (float) ((et - st) / 1000.0);
            System.out.printf("\t[Validate] Hit rate=%.02f; Miss rate=%.02f\n",
                    pi.hitRate(), pi.missRate());
            System.out.printf("\t[Validate] Total spending time : %.02f sec.\n", rt);
            
            /* Test model */
//            model.test(testData, testTagData);
//            System.out.printf("\t[Test] Test Done...\n");
            
            /* Done */
            System.out.printf("\t[Info] Done!\n");
        } catch (Exception ex) {
            Logger.getLogger(CRFDemo.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

}
