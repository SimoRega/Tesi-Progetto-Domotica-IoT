package com.example.apptesi;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Arrays;

public class XAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
    private String values[];
    public XAxisValueFormatter(String v[]){
        values=v;
        System.out.println(Arrays.toString(values));
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return values[(int)value];
    }
}
