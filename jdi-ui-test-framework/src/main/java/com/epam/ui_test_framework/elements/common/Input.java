/****************************************************************************
 * Copyright (C) 2014 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 ***************************************************************************/
package com.epam.ui_test_framework.elements.common;

import com.epam.ui_test_framework.elements.interfaces.common.IInput;
import org.openqa.selenium.By;

import static com.epam.ui_test_framework.settings.FrameworkSettings.asserter;

/**
 * Text Field control implementation
 *
 * @author Alexeenko Yan
 * @author Shubin Konstantin
 * @author Zharov Alexandr
 */
public class Input extends Text implements IInput {
    public Input() { super(); }
    public Input(By byLocator) { super(byLocator); }

    protected void setValueAction(String value) { newInput(value); }
    @Override
    protected String getTextAction() { return getWebElement().getAttribute("value"); }
    protected void inputAction(String text) { getWebElement().sendKeys(text); }
    protected void clearAction() { getWebElement().clear(); }
    protected void focusAction() { getWebElement().click(); }

    public final void setValue(String value) {
        doJAction("Set value", () -> setValueRule.invoke(value, this::setValueAction));
    }

    public final void input(String text) {
        doJAction("Input text '" + text + "' in text field",
                () -> setValueRule.invoke(text, this::inputAction));
    }
    public final void newInput(String text) {
        asserter.silentException(() -> setValueRule.invoke(text, t -> {
            clear();
            input(t);
        }));
    }
    public final void clear() {
        doJAction("Clear text field", this::clearAction);
    }
    public final void focus() {
        doJAction("Focus on text field", this::focusAction);
    }

}