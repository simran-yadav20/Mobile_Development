
package com.example.translation_app

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var translateButton: Button
    private lateinit var outputText: TextView
    private lateinit var translator: Translator
    private var selectedLanguageCode: String = "es" // Default to Spanish

    private val languageMap = mapOf(
        "English" to "en",
        "Spanish" to "es",
        "French" to "fr",
        "German" to "de",
        "Hindi" to "hi",
        "Chinese" to "zh",
        "Japanese" to "ja"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        languageSpinner = findViewById(R.id.languageSpinner)
        translateButton = findViewById(R.id.translateButton)
        outputText = findViewById(R.id.outputText)

        // Set up Spinner with language options
        val languages = languageMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Handle language selection
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedLanguageCode = languageMap[languages[position]] ?: "es"
                initializeTranslator()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Default to Spanish if nothing selected
                selectedLanguageCode = "es"
                initializeTranslator()
            }
        }

        // Set up translate button
        translateButton.setOnClickListener {
            val text = inputText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter text to translate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            translateText(text)
        }
    }

    private fun initializeTranslator() {
        // Configure ML Kit Translator
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH) // Assuming input is in English
            .setTargetLanguage(selectedLanguageCode)
            .build()
        translator = Translation.getClient(options)

        // Download language model if needed
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                // Model downloaded successfully
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Model download failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun translateText(text: String) {
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                outputText.text = translatedText
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Translation failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up translator resources
        translator.close()
    }
}