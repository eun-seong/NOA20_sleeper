package com.example.noa20_sleeper;

public class CalculateTime {
    public static String calculate(String time){
        int intTime = Integer.parseInt(time);

        int minute = intTime%60;
        int hour = (int)intTime/60;

        return String.format(hour+":"+minute);
    }
}
