package com.ggasoftware.uitest.control.new_controls.complex.table;


import com.ggasoftware.uitest.control.Element;
import com.ggasoftware.uitest.control.interfaces.base.IClickableText;

import java.util.ArrayList;
import java.util.List;

import static com.ggasoftware.uitest.control.base.asserter.TestNGAsserter.asserter;
import static com.ggasoftware.uitest.utils.ReflectionUtils.isClass;
import static com.ggasoftware.uitest.utils.Timer.getResultAction;


/**
 * Created by 12345 on 25.10.2014.
 */
public abstract class TableLine<T extends IClickableText, P> extends Element {
    public int startIndex = 1;
    public boolean haveHeader;
    public ElementIndexType elementIndex;

    public Table<T, P> table;

    protected int count = -1;
    public void setCount(int value) { count = value; }
    public int count() {
        return (count > 0)
            ? count
            : headers() != null ? headers.length : 0;
    }

    protected String[] headers;
    public void setHeaders(String[] value) { headers = value; }
    protected abstract String[] getHeadersAction() ;
    public final String[] headers() {
        if (headers != null)
            return headers;
        String[] localHeaders = getResultAction(this::getHeadersAction);
        setHeaders((haveHeader)
                ? localHeaders
                : getNumList(localHeaders.length));
        if (headers == null || headers.length == 0)
            asserter.exception("Can't get headers for Table");
        setCount(headers.length);
        return headers;
    }

    protected String[] getNumList(int count) {
        return getNumList(count, 1);
    }
    protected String[] getNumList(int count, int from) {
        List<String> result = new ArrayList<>();
        for (int i = from; i < count + from; i++)
        result.add(i + "");
        return result.toArray(new String[count]);
    }

    public final void update(TableLine<T, P> tableLine) {
        if (tableLine.count > 0)
            setCount(tableLine.count());
        if (tableLine.startIndex != 1)
            startIndex = tableLine.startIndex;
        if (tableLine.headers != null && tableLine.headers.length > 0)
            setHeaders(tableLine.headers());
        if ((isClass(tableLine.getClass(), Columns.class) && !tableLine.haveHeader)
                || (isClass(tableLine.getClass(), Rows.class) && tableLine.haveHeader))
            haveHeader = tableLine.haveHeader;
        if (tableLine.elementIndex != ElementIndexType.Nums)
        elementIndex = tableLine.elementIndex;
    }
}
