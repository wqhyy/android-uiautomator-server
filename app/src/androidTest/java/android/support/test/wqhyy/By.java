/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.test.wqhyy;

import java.util.regex.Pattern;

/**
 * <p>{@link By} is a utility class which enables the creation of {@link android.support.test.wqhyy.BySelector}s in a concise
 * manner.</p>
 *
 * <p>Its primary function is to provide static factory methods for constructing {@link android.support.test.wqhyy.BySelector}s
 * using a shortened syntax. For example, you would use {@code findObject(By.text("foo"))} rather
 * than {@code findObject(new BySelector().text("foo"))} to select UI elements with the text value
 * "foo".</p>
 */
public class By {

    /** This class is not meant to be instanciated */
    private By() { }


    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and copies the criteria from {@code original}.
     */
    public static android.support.test.wqhyy.BySelector copy(android.support.test.wqhyy.BySelector original) {
        return new android.support.test.wqhyy.BySelector(original);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the class name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#clazz(String)
     */
    public static android.support.test.wqhyy.BySelector clazz(String className) {
        return new android.support.test.wqhyy.BySelector().clazz(className);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the class name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#clazz(String, String)
     */
    public static android.support.test.wqhyy.BySelector clazz(String packageName, String className) {
        return new android.support.test.wqhyy.BySelector().clazz(packageName, className);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the class name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#clazz(Class)
     */
    public static android.support.test.wqhyy.BySelector clazz(Class clazz) {
        return new android.support.test.wqhyy.BySelector().clazz(clazz);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the class name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#clazz(Pattern)
     */
    public static android.support.test.wqhyy.BySelector clazz(Pattern className) {
        return new android.support.test.wqhyy.BySelector().clazz(className);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the content description criteria.
     *
     * @see android.support.test.wqhyy.BySelector#desc(String)
     */
    public static android.support.test.wqhyy.BySelector desc(String contentDescription) {
        return new android.support.test.wqhyy.BySelector().desc(contentDescription);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the content description criteria.
     *
     * @see android.support.test.wqhyy.BySelector#descContains(String)
     */
    public static android.support.test.wqhyy.BySelector descContains(String substring) {
        return new android.support.test.wqhyy.BySelector().descContains(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the content description criteria.
     *
     * @see android.support.test.wqhyy.BySelector#descStartsWith(String)
     */
    public static android.support.test.wqhyy.BySelector descStartsWith(String substring) {
        return new android.support.test.wqhyy.BySelector().descStartsWith(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the content description criteria.
     *
     * @see android.support.test.wqhyy.BySelector#descEndsWith(String)
     */
    public static android.support.test.wqhyy.BySelector descEndsWith(String substring) {
        return new android.support.test.wqhyy.BySelector().descEndsWith(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the content description criteria.
     *
     * @see android.support.test.wqhyy.BySelector#desc(Pattern)
     */
    public static android.support.test.wqhyy.BySelector desc(Pattern contentDescription) {
        return new android.support.test.wqhyy.BySelector().desc(contentDescription);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the application package name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#pkg(String)
     */
    public static android.support.test.wqhyy.BySelector pkg(String applicationPackage) {
        return new android.support.test.wqhyy.BySelector().pkg(applicationPackage);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the application package name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#pkg(Pattern)
     */
    public static android.support.test.wqhyy.BySelector pkg(Pattern applicationPackage) {
        return new android.support.test.wqhyy.BySelector().pkg(applicationPackage);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the resource name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#res(String)
     */
    public static android.support.test.wqhyy.BySelector res(String resourceName) {
        return new android.support.test.wqhyy.BySelector().res(resourceName);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the resource name criteria.
     *
     * @see android.support.test.wqhyy.BySelector#res(String, String)
     */
    public static android.support.test.wqhyy.BySelector res(String resourcePackage, String resourceId) {
        return new android.support.test.wqhyy.BySelector().res(resourcePackage, resourceId);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the resource id criteria.
     *
     * @see android.support.test.wqhyy.BySelector#res(Pattern)
     */
    public static android.support.test.wqhyy.BySelector res(Pattern resourceName) {
        return new android.support.test.wqhyy.BySelector().res(resourceName);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the text value criteria.
     *
     * @see android.support.test.wqhyy.BySelector#text(String)
     */
    public static android.support.test.wqhyy.BySelector text(String text) {
        return new android.support.test.wqhyy.BySelector().text(text);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the text value criteria.
     *
     * @see android.support.test.wqhyy.BySelector#textContains(String)
     */
    public static android.support.test.wqhyy.BySelector textContains(String substring) {
        return new android.support.test.wqhyy.BySelector().textContains(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the text value criteria.
     *
     * @see android.support.test.wqhyy.BySelector#textStartsWith(String)
     */
    public static android.support.test.wqhyy.BySelector textStartsWith(String substring) {
        return new android.support.test.wqhyy.BySelector().textStartsWith(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the text value criteria.
     *
     * @see android.support.test.wqhyy.BySelector#textEndsWith(String)
     */
    public static android.support.test.wqhyy.BySelector textEndsWith(String substring) {
        return new android.support.test.wqhyy.BySelector().textEndsWith(substring);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the text value criteria.
     *
     * @see android.support.test.wqhyy.BySelector#text(Pattern)
     */
    public static android.support.test.wqhyy.BySelector text(Pattern regex) {
        return new android.support.test.wqhyy.BySelector().text(regex);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the checkable criteria.
     *
     * @see android.support.test.wqhyy.BySelector#checkable(boolean)
     */
    public static android.support.test.wqhyy.BySelector checkable(boolean isCheckable) {
        return new android.support.test.wqhyy.BySelector().checkable(isCheckable);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the checked criteria.
     *
     * @see android.support.test.wqhyy.BySelector#checked(boolean)
     */
    public static android.support.test.wqhyy.BySelector checked(boolean isChecked) {
        return new android.support.test.wqhyy.BySelector().checked(isChecked);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the clickable criteria.
     *
     * @see android.support.test.wqhyy.BySelector#clickable(boolean)
     */
    public static android.support.test.wqhyy.BySelector clickable(boolean isClickable) {
        return new android.support.test.wqhyy.BySelector().clickable(isClickable);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the enabled criteria.
     *
     * @see android.support.test.wqhyy.BySelector#enabled(boolean)
     */
    public static android.support.test.wqhyy.BySelector enabled(boolean isEnabled) {
        return new android.support.test.wqhyy.BySelector().enabled(isEnabled);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the focusable criteria.
     *
     * @see android.support.test.wqhyy.BySelector#focusable(boolean)
     */
    public static android.support.test.wqhyy.BySelector focusable(boolean isFocusable) {
        return new android.support.test.wqhyy.BySelector().focusable(isFocusable);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the focused criteria.
     *
     * @see android.support.test.wqhyy.BySelector#focused(boolean)
     */
    public static android.support.test.wqhyy.BySelector focused(boolean isFocused) {
        return new android.support.test.wqhyy.BySelector().focused(isFocused);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the long clickable criteria.
     *
     * @see android.support.test.wqhyy.BySelector#longClickable(boolean)
     */
    public static android.support.test.wqhyy.BySelector longClickable(boolean isLongClickable) {
        return new android.support.test.wqhyy.BySelector().longClickable(isLongClickable);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the scrollable criteria.
     *
     * @see android.support.test.wqhyy.BySelector#scrollable(boolean)
     */
    public static android.support.test.wqhyy.BySelector scrollable(boolean isScrollable) {
        return new android.support.test.wqhyy.BySelector().scrollable(isScrollable);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the selected criteria.
     *
     * @see android.support.test.wqhyy.BySelector#selected(boolean)
     */
    public static android.support.test.wqhyy.BySelector selected(boolean isSelected) {
        return new android.support.test.wqhyy.BySelector().selected(isSelected);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and sets the depth criteria.
     */
    public static android.support.test.wqhyy.BySelector depth(int depth) {
        return new android.support.test.wqhyy.BySelector().depth(depth);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and adds a child selector criteria.
     *
     * @see android.support.test.wqhyy.BySelector#hasChild(android.support.test.wqhyy.BySelector)
     */
    public static android.support.test.wqhyy.BySelector hasChild(android.support.test.wqhyy.BySelector childSelector) {
        return new android.support.test.wqhyy.BySelector().hasChild(childSelector);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and adds a descendant selector criteria.
     *
     * @see android.support.test.wqhyy.BySelector#hasDescendant(android.support.test.wqhyy.BySelector)
     */
    public static android.support.test.wqhyy.BySelector hasDescendant(android.support.test.wqhyy.BySelector descendantSelector) {
        return new android.support.test.wqhyy.BySelector().hasDescendant(descendantSelector);
    }

    /**
     * Constructs a new {@link android.support.test.wqhyy.BySelector} and adds a descendant selector criteria.
     *
     * @see android.support.test.wqhyy.BySelector#hasDescendant(android.support.test.wqhyy.BySelector, int)
     */
    public static android.support.test.wqhyy.BySelector hasDescendant(android.support.test.wqhyy.BySelector descendantSelector, int maxDepth) {
        return new android.support.test.wqhyy.BySelector().hasDescendant(descendantSelector, maxDepth);
    }

}
