package com.ggasoftware.jdi_ui_tests.elements.interfaces.complex;

import com.ggasoftware.jdi_ui_tests.elements.base.Element;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IBaseElement;

/**
 * Created by Roman_Iovlev on 7/8/2015.
 */
public interface IGroup<TEnum extends Enum, TType extends Element> extends IBaseElement {
    TType get(TEnum name);
    TType get(String name);
}
