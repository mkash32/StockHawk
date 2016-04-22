package com.sam_chordas.android.stockhawk.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by mkash32 on 12/4/16.
 */
public class QuoteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuoteViewsFactory(this);
    }
}
