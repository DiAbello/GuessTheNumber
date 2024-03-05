package ru.potapov.guessnumber

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainMenu : AppCompatActivity() {
    lateinit var etName: EditText;
    lateinit var etSecs: EditText;

    lateinit var btnPlay: Button;
    lateinit var btnTheme: Button;

    lateinit var tvError: TextView;
    lateinit var tvDifficulty: TextView;
    lateinit var tvResult: TextView;
    lateinit var tvSelected: TextView;

    var selectedDiff: String = "";
    var reason: String = "";
    var nameValid: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //var secsValid: Boolean = false;

        etName = findViewById(R.id.et_Username)
        etSecs = findViewById(R.id.et_Seconds)
        etSecs.visibility = View.GONE;
        tvDifficulty = findViewById(R.id.tvDiffValue)
        btnPlay = findViewById(R.id.play_btn)
        btnPlay.isEnabled = false

        tvResult = findViewById(R.id.tvResult)
        tvResult.visibility = View.GONE;

        btnTheme = findViewById(R.id.theme_btn)

        registerForContextMenu(btnTheme)

        reason = intent.getStringExtra("gameResult").toString();
        if(reason.isNotEmpty() && reason != "null") {
            tvResult.visibility = View.VISIBLE;
            tvResult.text = reason;
        }

        etName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                nameValid = (s.toString().length !== 0);
                btnPlay.isEnabled = nameValid && (selectedDiff !== "");
            }
        })
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("name", etName.text.toString())
        outState.putString("diff", selectedDiff)
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        etName.setText(savedInstanceState.getString("name"))
        selectedDiff = savedInstanceState.getString("diff").toString()
        when(selectedDiff) {
            "easy" -> tvDifficulty.text = getString(R.string.diff_easy)
            "middle" -> tvDifficulty.text = getString(R.string.diff_middle)
            "hard" -> tvDifficulty.text = getString(R.string.diff_hard)
        }
        btnPlay.isEnabled = nameValid && (selectedDiff !== "");
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_diff_easy -> {
                selectedDiff = "easy"
                tvDifficulty.text = getString(R.string.diff_easy)
                btnPlay.isEnabled = nameValid && (selectedDiff !== "");
            };
            R.id.action_diff_middle -> {
                selectedDiff = "middle"
                tvDifficulty.text = getString(R.string.diff_middle)
                btnPlay.isEnabled = nameValid && (selectedDiff !== "");
            };
            R.id.action_diff_hard -> {
                selectedDiff = "hard"
                tvDifficulty.text = getString(R.string.diff_hard)
                btnPlay.isEnabled = nameValid && (selectedDiff !== "");
            };
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?,
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        menu?.setHeaderTitle("Выбор темы")
        menuInflater.inflate(R.menu.context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_theme_dark -> {
                btnTheme.text = getString(R.string.context_theme_dark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            R.id.action_theme_light -> {
                btnTheme.text = getString(R.string.context_theme_light)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            else -> { tvSelected.text = "undefined" }
        }
        return super.onContextItemSelected(item);
    }
    fun onPlayClick(view: View) {
        val intent = Intent(this@MainMenu, PlayGame::class.java)
        intent.putExtra("username", etName.text.toString())
        intent.putExtra("difficulty", selectedDiff)
        startActivity(intent)
    }
    fun isInRange(num: Int, min: Int, max: Int): Boolean {
        return (min..max).contains(num)
    }

    fun showToast(str: String) {
        return Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show();
    }
}