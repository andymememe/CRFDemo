/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andymememe.crfdemo.model;

import com.andymememe.crfdemo.data.AirDataIter;
import com.andymememe.crfdemo.data.AirDataSequence;
import iitb.CRF.CRF;
import iitb.CRF.DataSequence;
import iitb.Model.FeatureGenImpl;
import iitb.Utils.Options;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Andy Chen
 */
public class AirModel extends AbstModel {

    Options options;
    public String baseDir = "IRProj";
    public String outDir = "Test";

    public AirModel(Options o) throws Exception {
        nlabel = 133;
        this.options = o;
        dataIter = null;
        featureGen = new FeatureGenImpl(modelGraphType, nlabel);
        crfModel = new CRF(featureGen.numStates(), featureGen, options);
    }

    public AirModel() throws Exception {
        this(new Options());
    }

    /* Save model */
    @Override
    public void saveModel(String savePath) throws Exception {
        File bfdr = new File(savePath);

        if (!bfdr.exists()) {
            bfdr.mkdirs();
        }
        crfModel.write(new File(bfdr, "crf.txt").getAbsolutePath());
        featureGen.write(new File(bfdr, "features.txt").getAbsolutePath());
    }

    /* Load model */
    @Override
    public void loadModel(String path) throws Exception {
        File bfdr = new File(path);

        crfModel.read(new File(bfdr, "crf.txt").getAbsolutePath());
        featureGen.read(new File(bfdr, "features.txt").getAbsolutePath());
    }

    /* Train */
    @Override
    public void train() throws Exception {
        long st;
        long et;
        float rt;

        st = System.currentTimeMillis();
        featureGen.train(dataIter);
        crfModel.train(dataIter);
        et = System.currentTimeMillis();
        rt = (float) ((et - st) / 1000.0);
        System.out.printf("\t[Train] Training done...\n");
        System.out.printf("\t[Train] Total spending time : %.02f sec.\n", rt);
    }

    /* Validate */
    @Override
    public Stat validate(String valiDataPath, String outputPath)
            throws Exception {
        BufferedReader br;
        BufferedWriter bw;
        Stat stat;
        String line;
        AirDataSequence seq;

        stat = new Stat();
        br = new BufferedReader(new FileReader(new File(valiDataPath)));
        bw = new BufferedWriter(new FileWriter(new File(outputPath)));
        int seqCnt = 0;
        while ((line = br.readLine()) != null) {
            seq = new AirDataSequence(line, "data/label.txt");
            if (seq.size() > 0) {
                predictSeq(seq, stat);
                outputTaggedData(bw, seq);
                seqCnt++;
            }
        }
        bw.close();
        br.close();
        System.out.printf("\t[Validate] Total processing seq=%d\n", seqCnt);
        return stat;
    }

    @Override
    public void test(String testDataPath, String outputPath) throws Exception {
        BufferedReader br;
        BufferedWriter bw;
        String line;
        AirDataSequence seq;

        br = new BufferedReader(new FileReader(new File(testDataPath)));
        bw = new BufferedWriter(new FileWriter(new File(outputPath)));
        int seqCnt = 0;
        while ((line = br.readLine()) != null) {
            seq = new AirDataSequence(line, "data/label.txt");
            if (seq.size() > 0) {
                predictSeq(seq);
                outputTaggedData(bw, seq);
                seqCnt++;
            }
        }
        bw.close();
        br.close();
        System.out.printf("\t[Test] Total processing seq=%d\n", seqCnt);
    }

    @Override
    public AirDataSequence test(String testWord) {
        AirDataSequence seq;
        seq = new AirDataSequence(testWord, "data/label.txt");
        if (seq.size() > 0) {
            predictSeq(seq);
            return seq;
        } else {
            return null;
        }
    }

    /* Parse raw data */
    public void parseTrainData(String fn) throws IOException {
        try {
            List<AirDataSequence> dataList;
            File trainData;
            BufferedReader br;
            String line;
            AirDataSequence seq;

            dataList = new LinkedList<>();
            trainData = new File(fn);
            br = new BufferedReader(new FileReader(trainData));
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                System.out.printf("\t[Train] Read line=%s\n", line);
                seq = new AirDataSequence(line, "data/label.txt");
                if (seq.length() > 0) {
                    System.out.printf(String.format("\t[Train] Parsed result=%s/%d", seq.x(0), seq.y(0)));
                    for (int i = 1; i < seq.length(); i++) {
                        System.out.printf(String.format(" %s/%d", seq.x(i), seq.y(i)));
                    }
                    System.out.printf("\n");
                    System.out.printf("\n");
                    dataList.add(seq);
                }
            }

            dataIter = new AirDataIter(dataList);
            System.out.printf("\t[Train] Total %d sequence data\n",
                    dataIter.size());
        } catch (IOException e) {
            throw e;
        }
    }

    /* Predict sequence */
    public void predictSeq(DataSequence seq) {
        crfModel.apply(seq);
        featureGen.mapStatesToLabels(seq);
    }

    /* Predict sequence and recorded stat */
    public void predictSeq(DataSequence seq, Stat stat) {
        int labels[] = new int[seq.length()];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = seq.y(i);
        }
        crfModel.apply(seq);
        featureGen.mapStatesToLabels(seq);
        for (int i = 0; i < seq.length(); i++) {
            if (labels[i] != seq.y(i)) {
                stat.missing();
                labels[i] = seq.y(i);
            } else {
                stat.hitting();
            }
        }
    }

    /* Output formatted tagged data */
    protected void outputTaggedData(BufferedWriter bw, DataSequence seq)
            throws Exception {
        if (seq.length() > 0) {
            bw.append(String.format("%s/%s", seq.x(0), AirDataSequence.getToken(seq.y(0))));
            for (int i = 1; i < seq.length(); i++) {
                bw.append(String.format(" %s/%s", seq.x(i), AirDataSequence.getToken(seq.y(i))));
            }
            bw.append("\r\n");
        }
    }

}
