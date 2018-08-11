/*
 * Copyright (C) 2012 The Android Open Source Project
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

/**
 * Used to enumerate a container's UI elements for the purpose of counting,
 * or targeting a sub elements by a child's text or description.
 * @since API Level 16
 */
public class UiCollection extends UiObject {

    /**
     * Constructs an instance as described by the selector
     *
     * @param selector
     * @since API Level 16
     */
    public UiCollection(android.support.test.wqhyy.UiSelector selector) {
        super(selector);
    }

    /**
     * Searches for child UI element within the constraints of this UiCollection {@link android.support.test.wqhyy.UiSelector}
     * selector.
     *
     * It looks for any child matching the <code>childPattern</code> argument that has
     * a child UI element anywhere within its sub hierarchy that has content-description text.
     * The returned UiObject will point at the <code>childPattern</code> instance that matched the
     * search and not at the identifying child element that matched the content description.</p>
     *
     * @param childPattern {@link android.support.test.wqhyy.UiSelector} selector of the child pattern to match and return
     * @param text String of the identifying child contents of of the <code>childPattern</code>
     * @return {@link UiObject} pointing at and instance of <code>childPattern</code>
     * @throws UiObjectNotFoundException
     * @since API Level 16
     */
    public UiObject getChildByDescription(android.support.test.wqhyy.UiSelector childPattern, String text)
            throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        if (text != null) {
            int count = getChildCount(childPattern);
            for (int x = 0; x < count; x++) {
                UiObject row = getChildByInstance(childPattern, x);
                String nodeDesc = row.getContentDescription();
                if(nodeDesc != null && nodeDesc.contains(text)) {
                    return row;
                }
                UiObject item = row.getChild(new android.support.test.wqhyy.UiSelector().descriptionContains(text));
                if (item.exists()) {
                    return row;
                }
            }
        }
        throw new UiObjectNotFoundException("for description= \"" + text + "\"");
    }

    /**
     * Searches for child UI element within the constraints of this UiCollection {@link android.support.test.wqhyy.UiSelector}
     * selector.
     *
     * It looks for any child matching the <code>childPattern</code> argument that has
     * a child UI element anywhere within its sub hierarchy that is at the <code>instance</code>
     * specified. The operation is performed only on the visible items and no scrolling is performed
     * in this case.
     *
     * @param childPattern {@link android.support.test.wqhyy.UiSelector} selector of the child pattern to match and return
     * @param instance int the desired matched instance of this <code>childPattern</code>
     * @return {@link UiObject} pointing at and instance of <code>childPattern</code>
     * @since API Level 16
     */
    public UiObject getChildByInstance(android.support.test.wqhyy.UiSelector childPattern, int instance)
            throws UiObjectNotFoundException {
        Tracer.trace(childPattern, instance);
        android.support.test.wqhyy.UiSelector patternSelector = android.support.test.wqhyy.UiSelector.patternBuilder(getSelector(),
                android.support.test.wqhyy.UiSelector.patternBuilder(childPattern).instance(instance));
        return new UiObject(patternSelector);
    }

    /**
     * Searches for child UI element within the constraints of this UiCollection {@link android.support.test.wqhyy.UiSelector}
     * selector.
     *
     * It looks for any child matching the <code>childPattern</code> argument that has
     * a child UI element anywhere within its sub hierarchy that has text attribute =
     * <code>text</code>. The returned UiObject will point at the <code>childPattern</code>
     * instance that matched the search and not at the identifying child element that matched the
     * text attribute.</p>
     *
     * @param childPattern {@link android.support.test.wqhyy.UiSelector} selector of the child pattern to match and return
     * @param text String of the identifying child contents of of the <code>childPattern</code>
     * @return {@link UiObject} pointing at and instance of <code>childPattern</code>
     * @throws UiObjectNotFoundException
     * @since API Level 16
     */
    public UiObject getChildByText(android.support.test.wqhyy.UiSelector childPattern, String text)
            throws UiObjectNotFoundException {
        Tracer.trace(childPattern, text);
        if (text != null) {
            int count = getChildCount(childPattern);
            for (int x = 0; x < count; x++) {
                UiObject row = getChildByInstance(childPattern, x);
                String nodeText = row.getText();
                if(text.equals(nodeText)) {
                    return row;
                }
                UiObject item = row.getChild(new android.support.test.wqhyy.UiSelector().text(text));
                if (item.exists()) {
                    return row;
                }
            }
        }
        throw new UiObjectNotFoundException("for text= \"" + text + "\"");
    }

    /**
     * Counts child UI element instances matching the <code>childPattern</code>
     * argument. The method returns the number of matching UI elements that are
     * currently visible.  The count does not include items of a scrollable list
     * that are off-screen.
     *
     * @param childPattern a {@link android.support.test.wqhyy.UiSelector} that represents the matching child UI
     * elements to count
     * @return the number of matched childPattern under the current {@link UiCollection}
     * @since API Level 16
     */
    public int getChildCount(android.support.test.wqhyy.UiSelector childPattern) {
        Tracer.trace(childPattern);
        android.support.test.wqhyy.UiSelector patternSelector =
                android.support.test.wqhyy.UiSelector.patternBuilder(getSelector(), android.support.test.wqhyy.UiSelector.patternBuilder(childPattern));
        return getQueryController().getPatternCount(patternSelector);
    }
}
