

package android.support.test.wqhyy;


import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.app.UiAutomation.AccessibilityEventFilter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * UiDevice provides access to state information about the device.
 * You can also use this class to simulate user actions on the device,
 * such as pressing the d-pad or pressing the Home and Menu buttons.
 * @since API Level 16
 */
public class UiDevice implements android.support.test.wqhyy.Searchable {

    private static final String LOG_TAG = UiDevice.class.getSimpleName();

    // Sometimes HOME and BACK key presses will generate no events if already on
    // home page or there is nothing to go back to, Set low timeouts.
    private static final long KEY_PRESS_EVENT_TIMEOUT = 1 * 1000;

    // store for registered UiWatchers
    private final HashMap<String, android.support.test.wqhyy.UiWatcher> mWatchers = new HashMap<String, android.support.test.wqhyy.UiWatcher>();
    private final List<String> mWatchersTriggers = new ArrayList<String>();

    // remember if we're executing in the context of a UiWatcher
    private boolean mInWatcherContext = false;

    // provides access the {@link QueryController} and {@link InteractionController}
    private InstrumentationUiAutomatorBridge mUiAutomationBridge;

    /** keep a reference of {@link Instrumentation} instance*/
    private Instrumentation mInstrumentation;

    // Singleton instance
    private static UiDevice sInstance;

    // Get wait functionality from a mixin
    private android.support.test.wqhyy.WaitMixin<UiDevice> mWaitMixin = new android.support.test.wqhyy.WaitMixin<UiDevice>(this);


    /**
     * A forward-looking API Level for development platform builds
     *
     * This will be the actual API level on a released platform build, and will be last released
     * API Level + 1 on development platform build
     * @hide
     */
    static final int API_LEVEL_ACTUAL = Build.VERSION.SDK_INT
            + ("REL".equals(Build.VERSION.CODENAME) ? 0 : 1);

    /**
     * @deprecated Should use {@link UiDevice#UiDevice(Instrumentation)} instead.
     */
    @Deprecated
    private UiDevice() {}

    /** Private constructor. Clients should use {@link UiDevice#getInstance(Instrumentation)}. */
    private UiDevice(Instrumentation instrumentation) {
        mInstrumentation = instrumentation;
        UiAutomation uiAutomation = instrumentation.getUiAutomation();
        mUiAutomationBridge = new InstrumentationUiAutomatorBridge(
                instrumentation.getContext(), uiAutomation);

        // Enable multi-window support for API level 21 and up
        if (UiDevice.API_LEVEL_ACTUAL >= Build.VERSION_CODES.LOLLIPOP) {
            // Subscribe to window information
            AccessibilityServiceInfo info = uiAutomation.getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
            uiAutomation.setServiceInfo(info);
        }
    }

    /**
     * @hide
     * @deprecated Should use {@link UiDevice#getInstance(Instrumentation)} instead.
     */
    @Deprecated
    public void initialize(InstrumentationUiAutomatorBridge uiAutomatorBridge) {
        mUiAutomationBridge = uiAutomatorBridge;
    }

    boolean isInWatcherContext() {
        return mInWatcherContext;
    }

    /**
     * Provides access the {@link android.support.test.wqhyy.QueryController} and {@link android.support.test.wqhyy.InteractionController}
     * @return {@link InstrumentationUiAutomatorBridge}
     */
    InstrumentationUiAutomatorBridge getAutomatorBridge() {
        if (mUiAutomationBridge == null) {
            throw new RuntimeException("UiDevice not initialized");
        }
        return mUiAutomationBridge;
    }

    /**
     * Returns a UiObject which represents a view that matches the specified selector criteria.
     *
     * @param selector
     * @return UiObject object
     */
    public android.support.test.wqhyy.UiObject findObject(android.support.test.wqhyy.UiSelector selector) {
        return new android.support.test.wqhyy.UiObject(this, selector);
    }

    /** Returns whether there is a match for the given {@code selector} criteria. */
    public boolean hasObject(android.support.test.wqhyy.BySelector selector) {
        AccessibilityNodeInfo node = android.support.test.wqhyy.ByMatcher.findMatch(this, selector, getWindowRoots());
        if (node != null) {
            node.recycle();
            return true;
        }
        return false;
    }

    /** Returns the first object to match the {@code selector} criteria. */
    public android.support.test.wqhyy.UiObject2 findObject(android.support.test.wqhyy.BySelector selector) {
        AccessibilityNodeInfo node = android.support.test.wqhyy.ByMatcher.findMatch(this, selector, getWindowRoots());
        return node != null ? new android.support.test.wqhyy.UiObject2(this, selector, node) : null;
    }

    /** Returns all objects that match the {@code selector} criteria. */
    public List<android.support.test.wqhyy.UiObject2> findObjects(android.support.test.wqhyy.BySelector selector) {
        List<android.support.test.wqhyy.UiObject2> ret = new ArrayList<android.support.test.wqhyy.UiObject2>();
        for (AccessibilityNodeInfo node : android.support.test.wqhyy.ByMatcher.findMatches(this, selector, getWindowRoots())) {
            ret.add(new android.support.test.wqhyy.UiObject2(this, selector, node));
        }

        return ret;
    }


    /**
     * Waits for given the {@code condition} to be met.
     *
     * @param condition The {@link android.support.test.wqhyy.SearchCondition} to evaluate.
     * @param timeout Maximum amount of time to wait in milliseconds.
     * @return The final result returned by the condition.
     */
    public <R> R wait(android.support.test.wqhyy.SearchCondition<R> condition, long timeout) {
        return mWaitMixin.wait(condition, timeout);
    }

    /**
     * Performs the provided {@code action} and waits for the {@code condition} to be met.
     *
     * @param action The {@link Runnable} action to perform.
     * @param condition The {@link android.support.test.wqhyy.EventCondition} to evaluate.
     * @param timeout Maximum amount of time to wait in milliseconds.
     * @return The final result returned by the condition.
     */
    public <R> R performActionAndWait(Runnable action, android.support.test.wqhyy.EventCondition<R> condition, long timeout) {

        AccessibilityEvent event = null;
        try {
            event = getAutomatorBridge().executeCommandAndWaitForAccessibilityEvent(
                    action, new EventForwardingFilter(condition), timeout);
        } catch (TimeoutException e) {
            // Ignore
        }

        if (event != null) {
            event.recycle();
        }

        return condition.getResult();
    }

    /** Proxy class which acts as an {@link AccessibilityEventFilter} and forwards calls to an
     * {@link android.support.test.wqhyy.EventCondition} instance. */
    private static class EventForwardingFilter implements AccessibilityEventFilter {
        private android.support.test.wqhyy.EventCondition<?> mCondition;

        public EventForwardingFilter(android.support.test.wqhyy.EventCondition<?> condition) {
            mCondition = condition;
        }

        @Override
        public boolean accept(AccessibilityEvent event) {
            // Guard against nulls
            return Boolean.TRUE.equals(mCondition.apply(event));
        }
    }

    /**
     * Enables or disables layout hierarchy compression.
     *
     * If compression is enabled, the layout hierarchy derived from the Acessibility
     * framework will only contain nodes that are important for uiautomator
     * testing. Any unnecessary surrounding layout nodes that make viewing
     * and searching the hierarchy inefficient are removed.
     *
     * @param compressed true to enable compression; else, false to disable
     * @since API Level 18
     */
    public void setCompressedLayoutHeirarchy(boolean compressed) {
        getAutomatorBridge().setCompressedLayoutHierarchy(compressed);
    }

    /**
     * Retrieves a singleton instance of UiDevice
     *
     * @deprecated Should use {@link #getInstance(Instrumentation)} instead. This version hides
     * UiDevice's dependency on having an Instrumentation reference and is prone to misuse.
     * @return UiDevice instance
     * @since API Level 16
     */
    @Deprecated
    public static UiDevice getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("UiDevice singleton not initialized");
        }
        return sInstance;
    }

    /**
     * Retrieves a singleton instance of UiDevice
     *
     * @return UiDevice instance
     */
    public static UiDevice getInstance(Instrumentation instrumentation) {
        if (sInstance == null) {
            sInstance = new UiDevice(instrumentation);
        }
        return sInstance;
    }

    /**
     * Returns the display size in dp (device-independent pixel)
     *
     * The returned display size is adjusted per screen rotation. Also this will return the actual
     * size of the screen, rather than adjusted per system decorations (like status bar).
     *
     * @return a Point containing the display size in dp
     */
    public Point getDisplaySizeDp() {
        android.support.test.wqhyy.Tracer.trace();
        Display display = getAutomatorBridge().getDefaultDisplay();
        Point p = new Point();
        display.getRealSize(p);
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        float dpx = p.x / metrics.density;
        float dpy = p.y / metrics.density;
        p.x = Math.round(dpx);
        p.y = Math.round(dpy);
        return p;
    }

    /**
     * Retrieves the product name of the device.
     *
     * This method provides information on what type of device the test is running on. This value is
     * the same as returned by invoking #adb shell getprop ro.product.name.
     *
     * @return product name of the device
     * @since API Level 17
     */
    public String getProductName() {
        android.support.test.wqhyy.Tracer.trace();
        return Build.PRODUCT;
    }

    /**
     * Retrieves the text from the last UI traversal event received.
     *
     * You can use this method to read the contents in a WebView container
     * because the accessibility framework fires events
     * as each text is highlighted. You can write a test to perform
     * directional arrow presses to focus on different elements inside a WebView,
     * and call this method to get the text from each traversed element.
     * If you are testing a view container that can return a reference to a
     * Document Object Model (DOM) object, your test should use the view's
     * DOM instead.
     *
     * @return text of the last traversal event, else return an empty string
     * @since API Level 16
     */
    public String getLastTraversedText() {
        android.support.test.wqhyy.Tracer.trace();
        return getAutomatorBridge().getQueryController().getLastTraversedText();
    }

    /**
     * Clears the text from the last UI traversal event.
     * See {@link #getLastTraversedText()}.
     * @since API Level 16
     */
    public void clearLastTraversedText() {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getQueryController().clearLastTraversedText();
    }

    /**
     * Simulates a short press on the MENU button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressMenu() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getInteractionController().sendKeyAndWaitForEvent(
                KeyEvent.KEYCODE_MENU, 0, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                KEY_PRESS_EVENT_TIMEOUT);
    }

    /**
     * Simulates a short press on the BACK button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressBack() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getInteractionController().sendKeyAndWaitForEvent(
                KeyEvent.KEYCODE_BACK, 0, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                KEY_PRESS_EVENT_TIMEOUT);
    }

    /**
     * Simulates a short press on the HOME button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressHome() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getInteractionController().sendKeyAndWaitForEvent(
                KeyEvent.KEYCODE_HOME, 0, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
                KEY_PRESS_EVENT_TIMEOUT);
    }

    /**
     * Simulates a short press on the SEARCH button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressSearch() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_SEARCH);
    }

    /**
     * Simulates a short press on the CENTER button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDPadCenter() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_CENTER);
    }

    /**
     * Simulates a short press on the DOWN button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDPadDown() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
    }

    /**
     * Simulates a short press on the UP button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDPadUp() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_UP);
    }

    /**
     * Simulates a short press on the LEFT button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDPadLeft() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
    }

    /**
     * Simulates a short press on the RIGHT button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDPadRight() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
    }

    /**
     * Simulates a short press on the DELETE key.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressDelete() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_DEL);
    }

    /**
     * Simulates a short press on the ENTER key.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressEnter() {
        android.support.test.wqhyy.Tracer.trace();
        return pressKeyCode(KeyEvent.KEYCODE_ENTER);
    }

    /**
     * Simulates a short press using a key code.
     *
     * See {@link KeyEvent}
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressKeyCode(int keyCode) {
        android.support.test.wqhyy.Tracer.trace(keyCode);
        waitForIdle();
        return getAutomatorBridge().getInteractionController().sendKey(keyCode, 0);
    }

    /**
     * Simulates a short press using a key code.
     *
     * See {@link KeyEvent}.
     * @param keyCode the key code of the event.
     * @param metaState an integer in which each bit set to 1 represents a pressed meta key
     * @return true if successful, else return false
     * @since API Level 16
     */
    public boolean pressKeyCode(int keyCode, int metaState) {
        android.support.test.wqhyy.Tracer.trace(keyCode, metaState);
        waitForIdle();
        return getAutomatorBridge().getInteractionController().sendKey(keyCode, metaState);
    }

    /**
     * Simulates a short press on the Recent Apps button.
     *
     * @return true if successful, else return false
     * @throws RemoteException
     * @since API Level 16
     */
    public boolean pressRecentApps() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getInteractionController().toggleRecentApps();
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public boolean openNotification() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return  getAutomatorBridge().getInteractionController().openNotification();
    }

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public boolean openQuickSettings() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getInteractionController().openQuickSettings();
    }

    /**
     * Gets the width of the display, in pixels. The width and height details
     * are reported based on the current orientation of the display.
     * @return width in pixels or zero on failure
     * @since API Level 16
     */
    public int getDisplayWidth() {
        android.support.test.wqhyy.Tracer.trace();
        Display display = getAutomatorBridge().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p.x;
    }

    /**
     * Gets the height of the display, in pixels. The size is adjusted based
     * on the current orientation of the display.
     * @return height in pixels or zero on failure
     * @since API Level 16
     */
    public int getDisplayHeight() {
        android.support.test.wqhyy.Tracer.trace();
        Display display = getAutomatorBridge().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        return p.y;
    }

    /**
     * Perform a click at arbitrary coordinates specified by the user
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     * @since API Level 16
     */
    public boolean click(int x, int y) {
        android.support.test.wqhyy.Tracer.trace(x, y);
        if (x >= getDisplayWidth() || y >= getDisplayHeight()) {
            return (false);
        }
        return getAutomatorBridge().getInteractionController().clickNoSync(x, y);
    }

    /**
     * Performs a swipe from one coordinate to another using the number of steps
     * to determine smoothness and speed. Each step execution is throttled to 5ms
     * per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param steps is the number of move steps sent to the system
     * @return false if the operation fails or the coordinates are invalid
     * @since API Level 16
     */
    public boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        android.support.test.wqhyy.Tracer.trace(startX, startY, endX, endY, steps);
        return getAutomatorBridge().getInteractionController()
                .swipe(startX, startY, endX, endY, steps);
    }

    /**
     * Performs a swipe from one coordinate to another coordinate. You can control
     * the smoothness and speed of the swipe by specifying the number of steps.
     * Each step execution is throttled to 5 milliseconds per step, so for a 100
     * steps, the swipe will take around 0.5 seconds to complete.
     *
     * @param startX X-axis value for the starting coordinate
     * @param startY Y-axis value for the starting coordinate
     * @param endX X-axis value for the ending coordinate
     * @param endY Y-axis value for the ending coordinate
     * @param steps is the number of steps for the swipe action
     * @return true if swipe is performed, false if the operation fails
     * or the coordinates are invalid
     * @since API Level 18
     */
    public boolean drag(int startX, int startY, int endX, int endY, int steps) {
        android.support.test.wqhyy.Tracer.trace(startX, startY, endX, endY, steps);
        return getAutomatorBridge().getInteractionController()
                .swipe(startX, startY, endX, endY, steps, true);
    }

    /**
     * Performs a swipe between points in the Point array. Each step execution is throttled
     * to 5ms per step. So for a 100 steps, the swipe will take about 1/2 second to complete
     *
     * @param segments is Point array containing at least one Point object
     * @param segmentSteps steps to inject between two Points
     * @return true on success
     * @since API Level 16
     */
    public boolean swipe(Point[] segments, int segmentSteps) {
        android.support.test.wqhyy.Tracer.trace(segments, segmentSteps);
        return getAutomatorBridge().getInteractionController().swipe(segments, segmentSteps);
    }

    /**
     * Waits for the current application to idle.
     * Default wait timeout is 10 seconds
     * @since API Level 16
     */
    public void waitForIdle() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle(Configurator.getInstance().getWaitForIdleTimeout());
    }

    /**
     * Waits for the current application to idle.
     * @param timeout in milliseconds
     * @since API Level 16
     */
    public void waitForIdle(long timeout) {
        android.support.test.wqhyy.Tracer.trace(timeout);
        getAutomatorBridge().waitForIdle(timeout);
    }

    /**
     * Retrieves the last activity to report accessibility events.
     * @deprecated The results returned should be considered unreliable
     * @return String name of activity
     * @since API Level 16
     */
    @Deprecated
    public String getCurrentActivityName() {
        android.support.test.wqhyy.Tracer.trace();
        return getAutomatorBridge().getQueryController().getCurrentActivityName();
    }

    /**
     * Retrieves the name of the last package to report accessibility events.
     * @return String name of package
     * @since API Level 16
     */
    public String getCurrentPackageName() {
        android.support.test.wqhyy.Tracer.trace();
        return getAutomatorBridge().getQueryController().getCurrentPackageName();
    }

    /**
     * Registers a {@link android.support.test.wqhyy.UiWatcher} to run automatically when the testing framework is unable to
     * find a match using a {@link android.support.test.wqhyy.UiSelector}. See {@link #runWatchers()}
     *
     * @param name to register the UiWatcher
     * @param watcher {@link android.support.test.wqhyy.UiWatcher}
     * @since API Level 16
     */
    public void registerWatcher(String name, android.support.test.wqhyy.UiWatcher watcher) {
        android.support.test.wqhyy.Tracer.trace(name, watcher);
        if (mInWatcherContext) {
            throw new IllegalStateException("Cannot register new watcher from within another");
        }
        mWatchers.put(name, watcher);
    }

    /**
     * Removes a previously registered {@link android.support.test.wqhyy.UiWatcher}.
     *
     * See {@link #registerWatcher(String, android.support.test.wqhyy.UiWatcher)}
     * @param name used to register the UiWatcher
     * @since API Level 16
     */
    public void removeWatcher(String name) {
        android.support.test.wqhyy.Tracer.trace(name);
        if (mInWatcherContext) {
            throw new IllegalStateException("Cannot remove a watcher from within another");
        }
        mWatchers.remove(name);
    }

    /**
     * This method forces all registered watchers to run.
     * See {@link #registerWatcher(String, android.support.test.wqhyy.UiWatcher)}
     * @since API Level 16
     */
    public void runWatchers() {
        android.support.test.wqhyy.Tracer.trace();
        if (mInWatcherContext) {
            return;
        }

        for (String watcherName : mWatchers.keySet()) {
            android.support.test.wqhyy.UiWatcher watcher = mWatchers.get(watcherName);
            if (watcher != null) {
                try {
                    mInWatcherContext = true;
                    if (watcher.checkForCondition()) {
                        setWatcherTriggered(watcherName);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exceuting watcher: " + watcherName, e);
                } finally {
                    mInWatcherContext = false;
                }
            }
        }
    }

    /**
     * Resets a {@link android.support.test.wqhyy.UiWatcher} that has been triggered.
     * If a UiWatcher runs and its {@link android.support.test.wqhyy.UiWatcher#checkForCondition()} call
     * returned <code>true</code>, then the UiWatcher is considered triggered.
     * See {@link #registerWatcher(String, android.support.test.wqhyy.UiWatcher)}
     * @since API Level 16
     */
    public void resetWatcherTriggers() {
        android.support.test.wqhyy.Tracer.trace();
        mWatchersTriggers.clear();
    }

    /**
     * Checks if a specific registered  {@link android.support.test.wqhyy.UiWatcher} has triggered.
     * See {@link #registerWatcher(String, android.support.test.wqhyy.UiWatcher)}. If a UiWatcher runs and its
     * {@link android.support.test.wqhyy.UiWatcher#checkForCondition()} call returned <code>true</code>, then
     * the UiWatcher is considered triggered. This is helpful if a watcher is detecting errors
     * from ANR or crash dialogs and the test needs to know if a UiWatcher has been triggered.
     *
     * @param watcherName
     * @return true if triggered else false
     * @since API Level 16
     */
    public boolean hasWatcherTriggered(String watcherName) {
        android.support.test.wqhyy.Tracer.trace(watcherName);
        return mWatchersTriggers.contains(watcherName);
    }

    /**
     * Checks if any registered {@link android.support.test.wqhyy.UiWatcher} have triggered.
     *
     * See {@link #registerWatcher(String, android.support.test.wqhyy.UiWatcher)}
     * See {@link #hasWatcherTriggered(String)}
     * @since API Level 16
     */
    public boolean hasAnyWatcherTriggered() {
        android.support.test.wqhyy.Tracer.trace();
        return mWatchersTriggers.size() > 0;
    }

    /**
     * Used internally by this class to set a {@link android.support.test.wqhyy.UiWatcher} state as triggered.
     * @param watcherName
     */
    private void setWatcherTriggered(String watcherName) {
        android.support.test.wqhyy.Tracer.trace(watcherName);
        if (!hasWatcherTriggered(watcherName)) {
            mWatchersTriggers.add(watcherName);
        }
    }

    /**
     * Check if the device is in its natural orientation. This is determined by checking if the
     * orientation is at 0 or 180 degrees.
     * @return true if it is in natural orientation
     * @since API Level 17
     */
    public boolean isNaturalOrientation() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        int ret = getAutomatorBridge().getRotation();
        return ret == UiAutomation.ROTATION_FREEZE_0 ||
                ret == UiAutomation.ROTATION_FREEZE_180;
    }

    /**
     * Returns the current rotation of the display, as defined in {@link Surface}
     * @since API Level 17
     */
    public int getDisplayRotation() {
        android.support.test.wqhyy.Tracer.trace();
        waitForIdle();
        return getAutomatorBridge().getRotation();
    }

    /**
     * Disables the sensors and freezes the device rotation at its
     * current rotation state.
     * @throws RemoteException
     * @since API Level 16
     */
    public void freezeRotation() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().freezeRotation();
    }

    /**
     * Re-enables the sensors and un-freezes the device rotation allowing its contents
     * to rotate with the device physical rotation. During a test execution, it is best to
     * keep the device frozen in a specific orientation until the test case execution has completed.
     * @throws RemoteException
     */
    public void unfreezeRotation() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().unfreezeRotation();
    }

    /**
     * Simulates orienting the device to the left and also freezes rotation
     * by disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors
     * see {@link #unfreezeRotation()}.
     * @throws RemoteException
     * @since API Level 17
     */
    public void setOrientationLeft() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().setRotationLeft();
        waitForIdle(); // we don't need to check for idle on entry for this. We'll sync on exit
    }

    /**
     * Simulates orienting the device to the right and also freezes rotation
     * by disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors
     * see {@link #unfreezeRotation()}.
     * @throws RemoteException
     * @since API Level 17
     */
    public void setOrientationRight() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().setRotationRight();
        waitForIdle(); // we don't need to check for idle on entry for this. We'll sync on exit
    }

    /**
     * Simulates orienting the device into its natural orientation and also freezes rotation
     * by disabling the sensors.
     *
     * If you want to un-freeze the rotation and re-enable the sensors
     * see {@link #unfreezeRotation()}.
     * @throws RemoteException
     * @since API Level 17
     */
    public void setOrientationNatural() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().setRotationNatural();
        waitForIdle(); // we don't need to check for idle on entry for this. We'll sync on exit
    }

    /**
     * This method simulates pressing the power button if the screen is OFF else
     * it does nothing if the screen is already ON.
     *
     * If the screen was OFF and it just got turned ON, this method will insert a 500ms delay
     * to allow the device time to wake up and accept input.
     * @throws RemoteException
     * @since API Level 16
     */
    public void wakeUp() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        if(getAutomatorBridge().getInteractionController().wakeDevice()) {
            // sync delay to allow the window manager to start accepting input
            // after the device is awakened.
            SystemClock.sleep(500);
        }
    }

    /**
     * Checks the power manager if the screen is ON.
     *
     * @return true if the screen is ON else false
     * @throws RemoteException
     * @since API Level 16
     */
    public boolean isScreenOn() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        return getAutomatorBridge().getInteractionController().isScreenOn();
    }

    /**
     * This method simply presses the power button if the screen is ON else
     * it does nothing if the screen is already OFF.
     *
     * @throws RemoteException
     * @since API Level 16
     */
    public void sleep() throws RemoteException {
        android.support.test.wqhyy.Tracer.trace();
        getAutomatorBridge().getInteractionController().sleepDevice();
    }

    /**
     * Helper method used for debugging to dump the current window's layout hierarchy.
     * Relative file paths are stored the application's internal private storage location.
     *
     * @param fileName
     * @since API Level 16
     * @deprecated Use {@link UiDevice#dumpWindowHierarchy(File)} or
     *     {@link UiDevice#dumpWindowHierarchy(OutputStream)} instead.
     */
    @Deprecated
    public void dumpWindowHierarchy(String fileName) {
        android.support.test.wqhyy.Tracer.trace(fileName);

        File dumpFile = new File(fileName);
        if (!dumpFile.isAbsolute()) {
            dumpFile = getAutomatorBridge().getContext().getFileStreamPath(fileName);
        }
        try {
            dumpWindowHierarchy(dumpFile);
        } catch (IOException e) {
            // Ignore to preserve existing behavior. Ugh.
        }
    }

    /**
     * Dump the current window hierarchy to a {@link java.io.File}.
     *
     * @param dest The file in which to store the window hierarchy information.
     * @throws IOException
     */
    public void dumpWindowHierarchy(File dest) throws IOException {
        OutputStream stream = new BufferedOutputStream(new FileOutputStream(dest));
        try {
            dumpWindowHierarchy(new BufferedOutputStream(new FileOutputStream(dest)));
        } finally {
            stream.close();
        }
    }

    /**
     * Dump the current window hierarchy to an {@link java.io.OutputStream}.
     *
     * @param out The output stream that the window hierarchy information is written to.
     * @throws IOException
     */
    public void dumpWindowHierarchy(OutputStream out) throws IOException {
        android.support.test.wqhyy.AccessibilityNodeInfoDumper.dumpWindowHierarchy(this, out);
    }

    /**
     * Waits for a window content update event to occur.
     *
     * If a package name for the window is specified, but the current window
     * does not have the same package name, the function returns immediately.
     *
     * @param packageName the specified window package name (can be <code>null</code>).
     *        If <code>null</code>, a window update from any front-end window will end the wait
     * @param timeout the timeout for the wait
     *
     * @return true if a window update occurred, false if timeout has elapsed or if the current
     *         window does not have the specified package name
     * @since API Level 16
     */
    public boolean waitForWindowUpdate(final String packageName, long timeout) {
        android.support.test.wqhyy.Tracer.trace(packageName, timeout);
        if (packageName != null) {
            if (!packageName.equals(getCurrentPackageName())) {
                return false;
            }
        }
        Runnable emptyRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        AccessibilityEventFilter checkWindowUpdate = new AccessibilityEventFilter() {
            @Override
            public boolean accept(AccessibilityEvent t) {
                if (t.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                    return packageName == null || packageName.equals(t.getPackageName());
                }
                return false;
            }
        };
        try {
            getAutomatorBridge().executeCommandAndWaitForAccessibilityEvent(
                    emptyRunnable, checkWindowUpdate, timeout);
        } catch (TimeoutException e) {
            return false;
        } catch (Exception e) {
            Log.e(LOG_TAG, "waitForWindowUpdate: general exception from bridge", e);
            return false;
        }
        return true;
    }

    /**
     * Take a screenshot of current window and store it as PNG
     *
     * Default scale of 1.0f (original size) and 90% quality is used
     * The screenshot is adjusted per screen rotation
     *
     * @param storePath where the PNG should be written to
     * @return true if screen shot is created successfully, false otherwise
     * @since API Level 17
     */
    public boolean takeScreenshot(File storePath) {
        android.support.test.wqhyy.Tracer.trace(storePath);
        return takeScreenshot(storePath, 1.0f, 90);
    }

    /**
     * Take a screenshot of current window and store it as PNG
     *
     * The screenshot is adjusted per screen rotation
     *
     * @param storePath where the PNG should be written to
     * @param scale scale the screenshot down if needed; 1.0f for original size
     * @param quality quality of the PNG compression; range: 0-100
     * @return true if screen shot is created successfully, false otherwise
     * @since API Level 17
     */
    public boolean takeScreenshot(File storePath, float scale, int quality) {
        android.support.test.wqhyy.Tracer.trace(storePath, scale, quality);
        return getAutomatorBridge().takeScreenshot(storePath, quality);
    }

    /**
     * Retrieves default launcher package name
     *
     * @return package name of the default launcher
     */
    public String getLauncherPackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager pm = mInstrumentation.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * Executes a shell command using shell user identity, and return the standard output in string.
     * <p>
     * Calling function with large amount of output will have memory impacts, and the function call
     * will block if the command executed is blocking.
     * <p>Note: calling this function requires API level 21 or above
     * @param cmd the command to run
     * @return the standard output of the command
     * @throws IOException
     * @since API Level 21
     * @hide
     */
    public String executeShellCommand(String cmd) throws IOException {
        ParcelFileDescriptor pfd = mInstrumentation.getUiAutomation().executeShellCommand(cmd);
        byte[] buf = new byte[512];
        int bytesRead;
        FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
        StringBuffer stdout = new StringBuffer();
        while ((bytesRead = fis.read(buf)) != -1) {
            stdout.append(new String(buf, 0, bytesRead));
        }
        fis.close();
        return stdout.toString();
    }

    /** Returns a list containing the root {@link AccessibilityNodeInfo}s for each active window */
    AccessibilityNodeInfo[] getWindowRoots() {
        waitForIdle();

        ArrayList<AccessibilityNodeInfo> ret = new ArrayList<AccessibilityNodeInfo>();
        // Support multi-window searches for API level 21 and up
        if (UiDevice.API_LEVEL_ACTUAL >= Build.VERSION_CODES.LOLLIPOP) {
            for (AccessibilityWindowInfo window : mInstrumentation.getUiAutomation().getWindows()) {
                AccessibilityNodeInfo root = window.getRoot();

                if (root == null) {
                    Log.w(LOG_TAG, String.format("Skipping null root node for window: %s",
                            window.toString()));
                    continue;
                }
                ret.add(root);
            }
            // Prior to API level 21 we can only access the active window
        } else {
            ret.add(mInstrumentation.getUiAutomation().getRootInActiveWindow());
        }
        return ret.toArray(new AccessibilityNodeInfo[ret.size()]);
    }
}