package com.d4vram.ahorrapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.d4vram.ahorrapp.viewmodel.TpvViewModel
import com.d4vram.ahorrapp.viewmodel.rememberTpvViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests para ProfileScreen usando Compose testing
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun scanSoundSwitch_isDisplayed_and_clickable() {
        // Given: App is launched
        composeTestRule.waitForIdle()

        // Navigate to profile if needed (assuming we start on main screen)
        // This is simplified - in real app you'd need proper navigation

        // When: Look for sound switch
        composeTestRule.onNodeWithText("Sonido de escaneo").assertExists()

        // Then: Switch should be displayed and clickable
        composeTestRule.onNode(hasClickAction() and hasText("Sonido de escaneo"))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun backupSwitch_isDisplayed() {
        // Given: App is launched and navigated to profile
        composeTestRule.waitForIdle()

        // When: Look for backup switch
        composeTestRule.onNodeWithText("Backup automático diario").assertExists()

        // Then: Should be displayed
        composeTestRule.onNodeWithText("Backup automático diario")
            .assertIsDisplayed()
    }
}