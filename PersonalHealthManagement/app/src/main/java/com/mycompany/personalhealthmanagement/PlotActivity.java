package com.mycompany.personalhealthmanagement;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class PlotActivity extends Activity {

    private final String TAG = "PHM-Plot";
    private XYPlot plot1;
    private String plotType = "";
    private String plotUnit = "";
    int plotLowBound = Integer.MAX_VALUE;
    int plotHighBound = Integer.MIN_VALUE;
    Number[] xData;
    Number[] yData;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot);
        ArrayList<String> x_data = getIntent().getStringArrayListExtra(DetailActivity.PLOT_DATA_X);
        int data_size = getIntent().getIntExtra(DetailActivity.PLOT_DATA_X_SIZE, 0);
        ArrayList<String> y_data = getIntent().getStringArrayListExtra(DetailActivity.PLOT_DATA_Y);
        plotType = getIntent().getStringExtra(DetailActivity.PLOT_TYPE);
        plotUnit = getIntent().getStringExtra(DetailActivity.PLOT_UNIT);

        xData = new Number[data_size];
        yData = new Number[data_size];

        for (int i = 0; i < data_size; i++) {
            xData[i] = Long.parseLong(x_data.get(i));
            yData[i] = Long.parseLong(y_data.get(i));
            if (yData[i].intValue() > plotHighBound) {
                plotHighBound = yData[i].intValue() + 50;
            }
            if (yData[i].intValue() < plotLowBound) {
                plotLowBound = yData[i].intValue() - 50;
            }
        }

        plot1 = (XYPlot) findViewById(R.id.plot1);

        // create our series from our array of nums:
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(xData),
                Arrays.asList(yData),
                plotType);

        plot1.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        plot1.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
        plot1.getGraphWidget().getDomainGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
        plot1.getGraphWidget().getRangeGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot1.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 100, 0),                   // line color
                Color.rgb(0, 100, 0),                   // point color
                Color.rgb(100, 200, 0), null);                // fill color


        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);

        // ugly usage of LinearGradient. unfortunately there's no way to determine the actual size of
        // a View from within onCreate.  one alternative is to specify a dimension in resources
        // and use that accordingly.  at least then the values can be customized for the device type and orientation.
        lineFill.setShader(new LinearGradient(0, 0, 200, 200, Color.WHITE, Color.GREEN, Shader.TileMode.CLAMP));

        LineAndPointFormatter formatter  =
                new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED, null);
        formatter.setFillPaint(lineFill);
        plot1.getGraphWidget().setPaddingRight(2);
        plot1.addSeries(series2, formatter);

        // draw a domain tick for each year:
        plot1.setDomainStep(XYStepMode.SUBDIVIDE, xData.length);
        plot1.setRangeBoundaries(plotLowBound, plotHighBound, BoundaryMode.FIXED);

        // customize our domain/range labels
        plot1.setDomainLabel("Date");
        plot1.setRangeLabel(plotUnit);

        // get rid of decimal points in our range labels:
        plot1.setRangeValueFormat(new DecimalFormat("0"));


        plot1.setDomainValueFormat(new Format() {

            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                // we multiply our timestamp by 1000:
                long timestamp = ((Number) obj).longValue();
                //long timestamp = years[fetchidx++].longValue();
                Log.i("Time", "timestamp = " + timestamp);
                Date date = new Date(timestamp);
                Log.i("Time", date.toString());
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;

            }
        });
        plot1.getGraphWidget().setDomainLabelOrientation(-15);

        // by default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        //plot1.disableAllMarkup();
    }

    public void onPlotBackClick(View view) {
        super.onBackPressed();
    }
}