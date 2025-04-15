package com.example.bc_praca_x.algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SM2 {
    private int q; //quality, user grade, 0-5
    private int n; //repitition number
    private double EF; //easiness factor
    private int I; //inter-repetition interval

    public Map<String, Object> calculateNewInterval(){
        if(q>=3){
            if(n==0){
                I = 1;
            } else if(n==1){
                I = 6;
            } else{
                I = (int) Math.round(I * EF);
            }

            n++;

        } else{
            n = 0;
            I = 1;
        }

        calculateEasinessFactor();

        return Map.of("n", n, "I", I, "EF", EF);
    }

    private void calculateEasinessFactor(){
        EF = EF + (0.1 - (5-q)*(0.08+(5-q)*0.02));

        if(EF<1.3) EF = 1.3;
    }

    public void setValues(int q, int n, double EF, int I){
        this.q = q;
        this.n = n;
        this.EF = EF;
        this.I = I;
    }

    public Date calculateNextReviewDate(int interval) {
        Calendar calendar = Calendar.getInstance(); //now
        calendar.add(Calendar.DAY_OF_YEAR, interval);
        return calendar.getTime();
    }

}