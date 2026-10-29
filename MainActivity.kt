package com.momai.assistant

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var statusText: TextView
    private lateinit var transcriptText: TextView
    private lateinit var speakButton: Button
    private var tts: TextToSpeech? = null
    private val requestSpeech = 101
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            setBackgroundColor(0xFFF7F8F5.toInt())
        }

        val title = TextView(this).apply {
            text = "Mom AI"
            textSize = 32f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF173C2B.toInt())
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "आई, माझ्याशी मराठीत बोला"
            textSize = 20f
            setTextColor(0xFF343A35.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, dp(20))
        }
        root.addView(subtitle, matchWrap())

        speakButton = Button(this).apply {
            text = "🎙️  बोला"
            textSize = 25f
            isAllCaps = false
            setOnClickListener { startListening() }
        }
        root.addView(speakButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(76)
        ).apply { bottomMargin = dp(18) })

        statusText = TextView(this).apply {
            text = "तयार आहे"
            textSize = 18f
            setTextColor(0xFF256746.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }
        root.addView(statusText, matchWrap())

        val heardLabel = TextView(this).apply {
            text = "तुम्ही म्हणालात:"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF343A35.toInt())
            setPadding(0, dp(16), 0, dp(6))
        }
        root.addView(heardLabel, matchWrap())

        transcriptText = TextView(this).apply {
            text = "तुमचे बोलणे येथे दिसेल."
            textSize = 20f
            setTextColor(0xFF171717.toInt())
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(0xFFFFFFFF.toInt())
        }
        root.addView(transcriptText, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val help = TextView(this).apply {
            text = "ही सुरुवातीची चाचणी आवृत्ती आहे. फोन करण्यापूर्वी पुढे नेहमी पुष्टी विचारली जाईल."
            textSize = 14f
            setTextColor(0xFF5D625E.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, 0)
        }
        root.addView(help, matchWrap())

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mr-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "mr-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "मराठीत बोला")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        try {
            statusText.text = "ऐकत आहे..."
            startActivityForResult(intent, requestSpeech)
        } catch (_: ActivityNotFoundException) {
            statusText.text = "या फोनवर speech recognition उपलब्ध नाही."
            say("माफ करा, आवाज ओळखण्याची सुविधा उपलब्ध नाही.")
        } catch (_: Exception) {
            statusText.text = "आवाज सुरू करता आला नाही."
        }
    }

    @Deprecated("Deprecated by Android, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != requestSpeech) return
        if (resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = matches?.firstOrNull().orEmpty()
            transcriptText.text = if (spoken.isBlank()) "काही ऐकू आले नाही." else spoken
            val reply = replyFor(spoken)
            statusText.text = reply
            say(reply)
        } else {
            statusText.text = "पुन्हा प्रयत्न करण्यासाठी बटण दाबा."
        }
    }

    private fun replyFor(input: String): String {
        val text = input.lowercase(Locale("mr", "IN"))
        return when {
            text.contains("नमस्कार") || text.contains("हॅलो") || text.contains("hello") ->
                "नमस्कार आई. मी ऐकत आहे."
            text.contains("वेळ") || text.contains("किती वाजले") ->
                "वेळ पाहण्यासाठी फोनच्या घड्याळाकडे पाहा. पुढील आवृत्तीत मी वेळ सांगू शकेन."
            text.contains("फोन कर") || text.contains("कॉल कर") || text.contains("लाव") ->
                "मी कोणालाही फोन केलेला नाही. पुढील आवृत्तीत आधी नाव सांगून तुमची परवानगी विचारेन."
            text.contains("धन्यवाद") ->
                "काही हरकत नाही."
            text.contains("बंद") || text.contains("थांब") ->
                "ठीक आहे. पुन्हा बोलायचे असल्यास बटण दाबा."
            else -> "मला ऐकू आले: $input. हे अजून चाचणीचे स्वरूप आहे."
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("mr", "IN")) ?: TextToSpeech.LANG_MISSING_DATA
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!ttsReady) {
                tts?.setLanguage(Locale("hi", "IN"))
            }
        }
    }

    private fun say(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "mom-ai-reply")
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun matchWrap() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
