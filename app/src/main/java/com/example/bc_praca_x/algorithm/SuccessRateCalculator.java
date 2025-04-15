package com.example.bc_praca_x.algorithm;

import java.util.List;

public class SuccessRateCalculator {

    public static double certaintyValue(int value){
        switch (value) {
            case 3:
                return 0.33;
            case 4:
                return 0.67;
            case 5:
                return 1.0;
            default:
                return 0.0; // 0-2 SM values
        }
    }


    public static double calculateSuccessRate(List<Integer> ratings) {

        if (ratings.isEmpty()) return 0;

        double successRate = 0.0;

        for (int rating : ratings) {
            successRate += certaintyValue(rating);
        }

        return (successRate / ratings.size()) * 100;
    }

}
