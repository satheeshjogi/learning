package com.ggasoftware.jdi_ui_tests.elements.complex;

import com.ggasoftware.jdi_ui_tests.elements.BaseElement;
import com.ggasoftware.jdi_ui_tests.elements.base.Element;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.complex.IGroup;
import com.ggasoftware.jdi_ui_tests.settings.JDISettings;
import com.ggasoftware.jdi_ui_tests.utils.usefulUtils.TryCatchUtil;
import org.openqa.selenium.By;

import static com.ggasoftware.jdi_ui_tests.utils.common.EnumUtils.getEnumValue;
import static com.ggasoftware.jdi_ui_tests.utils.common.WebDriverByUtils.fillByTemplateSilent;
import static java.lang.String.format;


/**
 * Created by Roman_Iovlev on 7/8/2015.
 */
public class ElementsGroup<TEnum extends Enum, TType extends Element> extends BaseElement implements IGroup<TEnum, TType> {
    public ElementsGroup() { }
    public ElementsGroup(By byLocator, Class<TType> clazz) {
        super(byLocator);
        this.clazz = clazz;
    }
    private Class<TType> clazz;

    public TType get(TEnum name) {
        return get(getEnumValue(name));
    }
    public TType get(String name) {
        TType instance = TryCatchUtil.tryGetResult(clazz::newInstance);
        if (instance == null) {
            JDISettings.asserter.exception(format("Can't get instace of '%s' element from Elements Group '%s'", name, toString()));
            return null;
        }
        instance.setAvatar(fillByTemplateSilent(getLocator(), name), getAvatar());
        return instance;
    }
}
