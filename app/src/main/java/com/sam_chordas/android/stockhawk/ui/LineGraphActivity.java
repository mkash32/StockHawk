package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.Collections;

public class LineGraphActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    int maxPrice, minPrice;
    private Cursor mCursor;
    private LineChartView lineChartView;
    private LineSet lineSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        initializeChart();
        String symbol = getIntent().getStringExtra(getResources().getString(R.string.string_symbol));
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.string_symbol), symbol);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{args.getString(getResources().getString(R.string.string_symbol))},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        refreshChart();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void initializeChart() {
        lineSet = new LineSet();
        lineChartView = (LineChartView)findViewById(R.id.linechart);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.line_color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(dpToPx(1));

        lineChartView.setBorderSpacing(1)
                .setAxisBorderValues(minPrice - 50, maxPrice + 50, 50)
                .setLabelsColor(getResources().getColor(R.color.labels))
                .setBorderSpacing(dpToPx(5))
                .setGrid(ChartView.GridType.HORIZONTAL, paint);

        lineSet.setColor(getResources().getColor(R.color.line_set_color))
                .setDotsStrokeColor(getResources().getColor(R.color.line_color))
                .setDotsColor(Color.WHITE)
                .setDotsRadius(10)
                .setDotsStrokeThickness(dpToPx(2));
    }

    public void refreshChart() {
        findMinMax();
        addPointsToLineSet();
        lineChartView.addData(lineSet);
        lineChartView.show();
    }

    private void addPointsToLineSet() {
        mCursor.moveToFirst();
        for(int i = 0 ; !mCursor.isAfterLast() ; i++) {
            float price = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            lineSet.addPoint(""+i, price);
            mCursor.moveToNext();
        }
    }

    private void findMinMax() {
        ArrayList<Float> priceList = new ArrayList<Float>();

        mCursor.moveToFirst();
        while (!mCursor.isAfterLast()) {
            priceList.add(Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE))));
            mCursor.moveToNext();
        }

        maxPrice = Math.round(Collections.max(priceList));
        minPrice = Math.round(Collections.min(priceList));
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}