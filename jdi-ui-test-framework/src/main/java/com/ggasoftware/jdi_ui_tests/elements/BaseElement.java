package com.ggasoftware.jdi_ui_tests.elements;

import com.ggasoftware.jdi_ui_tests.elements.base.Element;
import com.ggasoftware.jdi_ui_tests.elements.complex.table.Table;
import com.ggasoftware.jdi_ui_tests.elements.page_objects.annotations.functions.Functions;
import com.ggasoftware.jdi_ui_tests.logger.base.LogSettings;
import com.ggasoftware.jdi_ui_tests.utils.common.Timer;
import com.ggasoftware.jdi_ui_tests.utils.linqInterfaces.*;
import com.ggasoftware.jdi_ui_tests.utils.map.MapArray;
import com.ggasoftware.jdi_ui_tests.elements.apiInteract.GetElementModule;
import com.ggasoftware.jdi_ui_tests.elements.base.Clickable;
import com.ggasoftware.jdi_ui_tests.elements.common.*;
import com.ggasoftware.jdi_ui_tests.elements.complex.*;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IBaseElement;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IClickable;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.IElement;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.base.ISelector;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.common.*;
import com.ggasoftware.jdi_ui_tests.elements.interfaces.complex.*;
import com.ggasoftware.jdi_ui_tests.reporting.PerformanceStatistic;
import com.ggasoftware.jdi_ui_tests.settings.JDISettings;
import com.ggasoftware.jdi_ui_tests.utils.common.ReflectionUtils;
import com.ggasoftware.jdi_ui_tests.utils.common.StringUtils;
import com.ggasoftware.jdi_ui_tests.utils.interfaces.IScenario;
import com.ggasoftware.jdi_ui_tests.utils.interfaces.IScenarioWithResult;
import org.openqa.selenium.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static com.ggasoftware.jdi_ui_tests.elements.page_objects.annotations.AnnotationsUtil.getElementName;
import static com.ggasoftware.jdi_ui_tests.reporting.PerformanceStatistic.addStatistic;
import static com.ggasoftware.jdi_ui_tests.utils.common.LinqUtils.first;
import static com.ggasoftware.jdi_ui_tests.utils.common.LinqUtils.select;
import static com.ggasoftware.jdi_ui_tests.utils.common.ReflectionUtils.getFieldValue;
import static com.ggasoftware.jdi_ui_tests.utils.common.Timer.sleep;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Roman_Iovlev on 6/10/2015.
 */
public abstract class BaseElement implements IBaseElement {
    public BaseElement() { this(null); }
    public static boolean createFreeInstance = false;
    public BaseElement(By byLocator) {
        avatar = new GetElementModule(byLocator, this);
        if (!createFreeInstance)
            CascadeInit.InitElements(this);
    }

    private String name;
    public String getName() { return name != null ? name : getTypeName(); }
    public void setName(String name) { this.name = name; }
    public Functions function = Functions.NONE;
    protected GetElementModule avatar;
    protected String parentTypeName = "";
    protected Timer timer() { return avatar.timer(); }
    protected JavascriptExecutor jsExecutor() { return (JavascriptExecutor) getDriver(); }

    public boolean haveLocator() { return avatar.haveLocator(); }
    public WebDriver getDriver() { return avatar.getDriver(); }
    public By getLocator() { return avatar.byLocator; }

    public GetElementModule getAvatar() {return avatar; }
    public void setAvatar(GetElementModule avatar) { this.avatar = avatar; }
    public void setAvatar(By byLocator, GetElementModule avatar) {
        this.avatar = new GetElementModule(byLocator, avatar.context, this);
        this.avatar.localElementSearchCriteria = avatar.localElementSearchCriteria;
    }

    protected void setWaitTimeout(long mSeconds) {
        JDISettings.logger.debug("Set wait timeout to " + mSeconds);
        getDriver().manage().timeouts().implicitlyWait(mSeconds, MILLISECONDS);
    }
    private String typeName;
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    protected String getTypeName() { return (typeName != null) ? typeName : getClass().getSimpleName(); }
    protected String getParentName() { return parentTypeName; }
    protected void setParentName(String parrentName) { parentTypeName = parrentName; }

    @Override
    public String toString() {
        return format("Name: '%s', Type: '%s' In: '%s', %s",
                getName(), getTypeName(), getParentName(), avatar);
    }

    public static IScenario invocationScenario = (element, actionName, jAction, logSettings) -> {
        sleep(100);
        element.logAction(actionName, logSettings);
        Timer timer = new Timer();
        Timer.alwaysDoneAction(jAction::invoke);
        JDISettings.logger.info(actionName + " done");
        PerformanceStatistic.addStatistic(timer.timePassedInMSec());
    };
    public static IScenarioWithResult invocationScenarioWithResult = new IScenarioWithResult() {
        @Override
        public <TResult> TResult invoke(BaseElement element, String actionName, JFuncT<TResult> jAction, JFuncTT<TResult, String> logResult, LogSettings logSettings) {
            sleep(100);
            element.logAction(actionName);
            Timer timer = new Timer();
            TResult result = Timer.getResultAction(jAction::invoke);
            String stringResult = (logResult == null)
                    ? result.toString()
                    : JDISettings.asserter.silent(() -> logResult.invoke(result));
            Long timePassed = timer.timePassedInMSec();
            PerformanceStatistic.addStatistic(timer.timePassedInMSec());
            JDISettings.logger.toLog(format("Get result '%s' in %s seconds", stringResult,
                    format("%.2f", (double) timePassed / 1000)), logSettings);
            return result;
        }
    };

    protected void logAction(String actionName, LogSettings logSettings) {
        JDISettings.logger.toLog(format("Perform action '%s' with element (%s)", actionName, this.toString()), logSettings);
    }
    protected void logAction(String actionName) { logAction(actionName, new LogSettings());
    }
    protected final <TResult> TResult doJActionResult(String actionName, JFuncT<TResult> action) {
        return doJActionResult(actionName, action, null, new LogSettings());
    }
    protected final <TResult> TResult doJActionResult(String actionName, JFuncT<TResult> action, JFuncTT<TResult, String> logResult) {
        return doJActionResult(actionName, action, logResult, new LogSettings());
    }
    protected final <TResult> TResult doJActionResult(String actionName, JFuncT<TResult> action, LogSettings logSettings) {
        return doJActionResult(actionName, action, null, logSettings);
    }
    protected final <TResult> TResult doJActionResult(String actionName, JFuncT<TResult> action,
                                                      JFuncTT<TResult, String> logResult, LogSettings logSettings) {
        try {
            JDISettings.driverFactory.processDemoMode(this);
            return invocationScenarioWithResult.invoke(this, actionName, action, logResult, logSettings);
        }
        catch (Exception ex) {
            JDISettings.asserter.exception(format("Failed to do '%s' action. Exception: %s", actionName, ex));
            return null;
        }
    }

    protected final void doJAction(String actionName, JAction action) {
        doJAction(actionName, action, new LogSettings());
    }

    protected final void doJAction(String actionName, JAction action, LogSettings logSettings) {
        try {
            JDISettings.driverFactory.processDemoMode(this);
            invocationScenario.invoke(this, actionName, action, logSettings);
        }
        catch (Exception ex) {
            JDISettings.asserter.exception(format("Failed to do '%s' action. Exception: %s", actionName, ex));
        }
    }

    public static void setValueRule(String text, JActionT<String> action)  {
        JDISettings.asserter.silent(() -> setValueRule.invoke(text, action));
    }
    public static JActionTT<String, JActionT<String>> setValueRule = (text, action) -> {
        if (text == null) return;
        action.invoke(text);
    };
    public static JActionTT<String, JActionT<String>> setValueEmptyAction = (text, action) -> {
        if (text == null || text.equals("")) return;
        action.invoke(text.equals("#CLEAR#") ? "" : text);
    };


    private static MapArray<Class, Class> map;
    public static MapArray<Class, Class> getInterfacesMap() {
        try {
            if (map == null)
                map = new MapArray<>(new Object[][]{
                        {IElement.class, Element.class},
                        {IButton.class, Button.class},
                        {IClickable.class, Clickable.class},
                        {IComboBox.class, ComboBox.class},
                        {ILink.class, Link.class},
                        {ISelector.class, Selector.class},
                        {IText.class, Text.class},
                        {ITextArea.class, TextArea.class},
                        {ITextField.class, TextField.class},
                        {ILabel.class, Label.class},
                        {IDropDown.class, Dropdown.class},
                        {IDropList.class, DropList.class},
                        {IGroup.class, ElementsGroup.class},
                        {ITable.class, Table.class},
                        {ICheckBox.class, CheckBox.class},
                        {IRadioButtons.class, RadioButtons.class},
                        {ICheckList.class, CheckList.class},
                        {ITextList.class, TextList.class},
                        {ITabs.class, Tabs.class},
                        {IMenu.class, Menu.class},
                        {IFileInput.class, FileInput.class},
                        {IDatePicker.class, DatePicker.class},
                });
            return map;
        } catch (Exception ex) { JDISettings.asserter.exception("Error in getInterfaceTypeMap" + StringUtils.LineBreak + ex.getMessage()); }
        return null;
    }

    protected Button getButton(String buttonName) {
        List<Field> fields = ReflectionUtils.getFields(this, IButton.class);
        if (fields.size() == 1)
            return (Button) getFieldValue(fields.get(0), this);
        Collection<Button> buttons = select(fields, f -> (Button) getFieldValue(f, this));
        Button button = first(buttons, b -> namesEqual(b.getName(), buttonName.toLowerCase().contains("button") ? buttonName : buttonName + "button"));
        if (button == null) {
            JDISettings.asserter.exception(format("Can't find button '%s' for element '%s'", buttonName, toString()));
            return null;
        }
        return button;
    }

    protected boolean namesEqual(String name1, String name2) {
        return name1.toLowerCase().replace(" ", "").equals(name2.toLowerCase().replace(" ", ""));
    }

    protected Button getButton(Functions funcName) {
        List<Field> fields = ReflectionUtils.getFields(this, IButton.class);
        if (fields.size() == 1)
            return (Button) getFieldValue(fields.get(0), this);
        Collection<Button> buttons = select(fields, f -> (Button) getFieldValue(f, this));
        Button button = first(buttons, b -> b.function.equals(funcName));
        if (button == null) {
            JDISettings.asserter.exception(format("Can't find button '%s' for element '%s'", funcName, toString()));
            return null;
        }
        return button;
    }

    protected Text getTextElement() {
        Field textField = first(getClass().getDeclaredFields(), f -> (f.getType() == Text.class) || (f.getType() == IText.class));
        if (textField!= null) return (Text) getFieldValue(textField, this);
        JDISettings.asserter.exception(format("Can't find Text element '%s'", toString()));
        return null;
    }
}
