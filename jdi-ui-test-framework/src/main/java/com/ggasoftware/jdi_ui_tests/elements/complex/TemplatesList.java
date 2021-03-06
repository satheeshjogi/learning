package com.ggasoftware.jdi_ui_tests.elements.complex;

import com.ggasoftware.jdi_ui_tests.elements.BaseElement;
import com.ggasoftware.jdi_ui_tests.elements.base.Element;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IElement;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IVisible;
import com.ggasoftware.jdi_ui_tests.utils.common.EnumUtils;
import com.ggasoftware.jdi_ui_tests.settings.JDISettings;
import com.ggasoftware.jdi_ui_tests.utils.common.WebDriverByUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

import static com.ggasoftware.jdi_ui_tests.elements.base.Element.copy;
import static com.ggasoftware.jdi_ui_tests.utils.common.EnumUtils.getEnumValue;
import static com.ggasoftware.jdi_ui_tests.utils.common.LinqUtils.first;
import static com.ggasoftware.jdi_ui_tests.utils.common.LinqUtils.select;
import static com.ggasoftware.jdi_ui_tests.utils.common.WebDriverByUtils.fillByTemplateSilent;
import static java.lang.String.format;

/**
 * Created by Roman_Iovlev on 7/3/2015.
 */
abstract class TemplatesList<TType extends Element, TEnum extends Enum> extends BaseElement implements IVisible {
    public TemplatesList() { }
    public TemplatesList(By byLocator, TType templateElement) {
        super(byLocator);
        this.templateElement = templateElement;
    }
    public TemplatesList(By byLocator, TType templateElement, TEnum enumMember) {
        super(byLocator);
        this.templateElement = templateElement;
        elementsNames = (List<String>) select(enumMember.getClass().getEnumConstants(), EnumUtils::getEnumValue);
    }

    public void setListOfElements(List<String> elementsNames) { this.elementsNames = elementsNames; }
    public void setListOfElements(TEnum enumMember) { this.elementsNames =
            (List<String>) select(enumMember.getClass().getEnumConstants(), EnumUtils::getEnumValue); }
    protected List<String> elementsNames;
    private TType templateElement;

    public boolean waitDisplayed() {
        return timer().wait(() -> first(getElementsList(), el -> el.getWebElement().isDisplayed()) != null);
    }

    public boolean waitVanished()  {
        setWaitTimeout(JDISettings.timeouts.retryMSec);
        boolean result = timer().wait(() -> {
                for (TType el : getElementsList())
                    try { if (el.getWebElement().isDisplayed()) return false;
                    } catch (Exception ignore) { }
                return true;
            });
        setWaitTimeout(JDISettings.timeouts.waitElementSec);
        return result;
    }

    public WebElement getWebElement(String name) { return getElement(name).getWebElement(); }
    public WebElement getWebElement(TEnum enumName) { return getElement(enumName).getWebElement(); }

    public TType getElement(String name) {
        return copy(templateElement, fillByTemplateSilent(getLocator(), name));
    }
    public TType getElement(TEnum enumName) {
        return getElement(getEnumValue(enumName));
    }

    public List<WebElement> getWebElements(TEnum enumName) {
        return (List<WebElement>) select(getElementsList(), IElement::getWebElement); }
    protected List<TType> getElementsListAction() {
        try { return elementsNames.stream().map(this::getElement).collect(Collectors.toList());
        } catch (Exception ex) { JDISettings.asserter.exception(ex.getMessage()); return null; }
    }
    public final List<TType> getElementsList() {
        if (elementsNames == null || elementsNames.size() == 0)
            JDISettings.asserter.exception(format("Please specify elements names for list element '%s'", toString()));
        return doJActionResult("Get elements", this::getElementsListAction);
    }
    public int count() {
        return getElementsList().size();
    }
}
