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

import java.util.List;

/** The Searchable interface represents an object that can be searched for matching UI elements. */
interface Searchable {

    /** Returns whether there is a match for the given {@code selector} criteria. */
    public boolean hasObject(android.support.test.wqhyy.BySelector selector);

    /** Returns the first object to match the {@code selector} criteria. */
    public android.support.test.wqhyy.UiObject2 findObject(android.support.test.wqhyy.BySelector selector);

    /** Returns all objects that match the {@code selector} criteria. */
    public List<android.support.test.wqhyy.UiObject2> findObjects(android.support.test.wqhyy.BySelector selector);
}
