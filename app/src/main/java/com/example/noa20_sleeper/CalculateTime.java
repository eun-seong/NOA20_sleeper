package com.example.noa20_sleeper;

public class CalculateTime {
    public static String calculate(int time){

        int minute = time%60;
        int hour = time/60;

        return String.format("%d:%02d",hour,minute);
    }
}
