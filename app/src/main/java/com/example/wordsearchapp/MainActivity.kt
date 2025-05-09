
package com.example.wordsearchapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.FileInputStream
import org.apache.poi.xwpf.usermodel.XWPFDocument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var searchTerm by remember { mutableStateOf("") }
            var results by remember { mutableStateOf(listOf<Pair<String, String>>()) }

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                        results = searchInWordDocs(this@MainActivity, searchTerm)
                    },
                    label = { Text("Rechercher dans les .docx") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(results) { (fileName, extract) ->
                        Text("$fileName: $extract", modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }

    private fun searchInWordDocs(context: Context, query: String): List<Pair<String, String>> {
        val results = mutableListOf<Pair<String, String>>()
        val docFiles = context.getExternalFilesDir(null)?.listFiles { _, name -> name.endsWith(".docx") } ?: return results
        for (file in docFiles) {
            try {
                val fis = FileInputStream(file)
                val doc = XWPFDocument(fis)
                val text = doc.paragraphs.joinToString("\n") { it.text }
                if (text.contains(query, ignoreCase = true)) {
                    val snippet = text.substringAfter(query).take(100)
                    results.add(file.name to snippet)
                }
                fis.close()
            } catch (_: Exception) {}
        }
        return results
    }
}
