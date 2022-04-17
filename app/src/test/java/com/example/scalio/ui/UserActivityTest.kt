package com.example.scalio.ui

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.scalio.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserActivityTest {

    lateinit var activity: UserActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(UserActivity::class.java).create().get()
    }

    @Test
    fun areActivityViewsVisible() {

        val retryBtn = activity.findViewById<Button>(R.id.retry_btn)
        val emptyListTv = activity.findViewById<TextView>(R.id.empty_list_tv)
        val searchIcon = activity.findViewById<ImageView>(R.id.search_icon)
        val searchEt = activity.findViewById<EditText>(R.id.search_box_et)
        assertNotNull(retryBtn)
        assertNotNull(emptyListTv)
        assertNotNull(searchIcon)
        assertNotNull(searchEt)
    }

    @Test
    fun isContentsAsExpected() {
        val retryBtn = activity.findViewById<Button>(R.id.retry_btn)

        assertEquals(retryBtn.text, "Retry")
    }
}
