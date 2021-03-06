package com.ggasoftware.jdi_ui_tests.elements.interfaces.common;

import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IElement;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.ISetValue;
import com.ggasoftware.jdi_ui_tests.elements.page_objects.annotations.JDIAction;

/**
 * Created by Roman_Iovlev on 7/6/2015.
 */
public interface ITextField extends ISetValue, IText, IElement {
    /** Input text in textfield */
    @JDIAction
    void input(String text);
    /** Input text in textfield */
    @JDIAction
    void sendKeys(String text);
    /** Clear and input text in textfield */
    @JDIAction
    void newInput(String text);
    /** Clear textfield */
    @JDIAction
    void clear();
    /** Focus(click) on textfield */
    @JDIAction
    void focus();
}
