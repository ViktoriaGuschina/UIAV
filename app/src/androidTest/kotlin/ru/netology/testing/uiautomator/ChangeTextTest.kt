package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"


    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        // Press home
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)

        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testEmptyStringInput() {
        val appPackageName = MODEL_PACKAGE
        waitForPackage(appPackageName)

        val textView = device.findObject(By.res(appPackageName, "textToBeChanged"))
            ?: throw AssertionError("Элемент 'textToBeChanged' не найден")
        val initialText = textView.text

        val userInputField = device.findObject(By.res(appPackageName, "userInput"))
            ?: throw AssertionError("Элемент 'userInput' не найден")
        val changeButton = device.findObject(By.res(appPackageName, "buttonChange"))
            ?: throw AssertionError("Элемент 'buttonChange' не найден")

        userInputField.text = " "

        changeButton.click()
        device.waitForIdle()

        val resultTextView = device.findObject(By.res(appPackageName, "textToBeChanged"))
            ?: throw AssertionError("Элемент 'textToBeChanged' не найден после изменения")
        val resultText = resultTextView.text

        assertEquals(initialText, resultText)
    }

    @Test
    fun testOpenNewActivity() {
        val appPackageName = MODEL_PACKAGE
        waitForPackage(appPackageName)

        val userInputField = device.findObject(By.res(appPackageName, "userInput"))
            ?: throw AssertionError("Элемент 'userInput' не найден")
        val openActivityButton = device.findObject(By.res(appPackageName, "buttonActivity"))
            ?: throw AssertionError("Элемент 'buttonActivity' не найден")

        userInputField.text = textToSet

        openActivityButton.click()

        waitForPackage(appPackageName)

        val displayedTextObj = device.findObject(By.res(appPackageName, "text"))
            ?: throw AssertionError("Элемент 'text' на новой активности не найден")

        val displayedText = displayedTextObj.text

        assertEquals(textToSet, displayedText)
    }
}