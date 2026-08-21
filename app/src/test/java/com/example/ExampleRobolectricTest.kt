package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.PromptLibrary
import com.example.model.Personas
import com.example.ui.components.MarkdownBlock
import com.example.ui.components.parseMarkdown
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("دردشة", appName)
    }

    @Test
    fun `verify personas library`() {
        val allPersonas = Personas.all
        assertTrue(allPersonas.size >= 8)
        val coder = Personas.getById("coder")
        assertEquals("المبرمج وخبير التقنية", coder.titleArabic)
        assertTrue(coder.starterPrompts.isNotEmpty())
    }

    @Test
    fun `verify prompt templates library`() {
        val categories = PromptLibrary.categories
        assertTrue(categories.isNotEmpty())
        val allPrompts = PromptLibrary.allPrompts
        assertTrue(allPrompts.size >= 10)
    }

    @Test
    fun `verify markdown parser`() {
        val md = """
            # عنوان رئيسي
            هذه فقرة توضيحية.
            ```kotlin
            fun main() {}
            ```
            - نقطة أولى
        """.trimIndent()

        val blocks = parseMarkdown(md)
        assertTrue(blocks.any { it is MarkdownBlock.Header })
        assertTrue(blocks.any { it is MarkdownBlock.Paragraph })
        assertTrue(blocks.any { it is MarkdownBlock.CodeBlock })
        assertTrue(blocks.any { it is MarkdownBlock.BulletItem })
    }
}
