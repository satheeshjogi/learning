package com.ggasoftware.jdi_ui_tests.elements.complex.table;

import com.ggasoftware.jdi_ui_tests.elements.base.SelectElement;
import com.ggasoftware.jdi_ui_tests.utils.common.LinqUtils;
import com.ggasoftware.jdi_ui_tests.utils.map.MapArray;
import com.ggasoftware.jdi_ui_tests.settings.JDISettings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;

import static java.lang.String.format;

/**
 * Created by 12345 on 26.10.2014.
 */
class Columns<T extends SelectElement> extends TableLine<T> {
    public Columns() {
        haveHeader = true;
        elementIndex = ElementIndexType.Nums;
    }

    protected String[] getHeadersAction() {
        return LinqUtils.select(table.getWebElement().findElements(By.xpath(".//th")), WebElement::getText)
                .toArray(new String[1]);
    }

    private void throwColsException(String colName, Exception ex) {
        JDISettings.asserter.exception(format("Can't Get Column '%s'. Exception: %s", colName, ex));
    }
    public final MapArray<String, ICell<T>> getRow(String rowName) {
        try {
            return cellsToRow(LinqUtils.select(headers(), colName -> table.cell(new Column(colName), new Row(rowName))));
        }
        catch (Exception ex) { throwColsException(rowName, ex); return null; }
    }

    public MapArray<String, ICell<T>> cellsToRow(Collection<ICell<T>> cells) {
        return JDISettings.asserter.silent(() -> new MapArray<String, ICell<T>>(cells,
                cell -> headers()[cell.columnNum() - 1],
                cell -> cell));
    }

    public MapArray<String, ICell<T>> getRow(int rowNum) {
        int colsCount = -1;
        if (count > 0)
        colsCount = count;
        else if (headers != null && (headers.length > 0))
        colsCount = headers.length;
        if (colsCount > 0 && colsCount < rowNum)
            JDISettings.asserter.exception(format("Can't Get Column '%s'. [num] > ColumnsCount(%s).", rowNum, colsCount));
        try {
            return new MapArray<>(count(),
                    colNum -> headers()[colNum],
                    colNum -> table.cell(new Column(colNum), new Row(rowNum)));
        }
        catch (Exception ex) { throwColsException(rowNum + "", ex); return null; }
    }

    public MapArray<String, MapArray<String, ICell<T>>> get() {
        MapArray<String, MapArray<String, ICell<T>>> cols = new MapArray<>();
        for(String columnName : headers()) {
            MapArray<String, ICell<T>> row = new MapArray<>();
            for (String rowName : table.rows().headers())
                row.add(rowName, table.cell(new Column(columnName), new Row(rowName)));
            cols.add(columnName, row);
        }
        return cols;
    }
}
