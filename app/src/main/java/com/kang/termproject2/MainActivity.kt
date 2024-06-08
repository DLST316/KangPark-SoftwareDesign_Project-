package com.kang.termproject2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kang.termproject2.databinding.ActivityMainBinding
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val menuSwitchItem = binding.navView.menu.findItem(R.id.nav_theme_switch)
        val actionView = menuSwitchItem.actionView as SwitchCompat

        actionView.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        actionView.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setSupportActionBar(binding.toolbar)
        setupDrawerToggle()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_video1 -> showConfirmationDialog("https://www.youtube.com/watch?v=XxWcirHIwVo&ab_channel=JeremyEthier")
                R.id.nav_video2 -> showConfirmationDialog("https://www.youtube.com/watch?v=gcNh17Ckjgg&ab_channel=JeremyEthier")
                R.id.nav_video3 -> showConfirmationDialog("https://www.youtube.com/watch?v=4Y2ZdHCOXok&ab_channel=JeremyEthier")
                R.id.nav_video4 -> showConfirmationDialog("https://www.youtube.com/watch?v=eGo4IYlbE5g&ab_channel=Calisthenicmovement")
                R.id.nav_video5 -> showConfirmationDialog("https://www.youtube.com/watch?v=IODxDxX7oi4&ab_channel=Calisthenicmovement")
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.btnOpenListActivity.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        binding.btnStartWorkout.setOnClickListener {
            val intent = Intent(this, SelectExerciseActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewHistory.setOnClickListener {
            val intent = Intent(this, ExerciseHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDrawerToggle() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, 0, 0)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog(videoUrl: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage("YouTube로 이동하시겠습니까?")
        builder.setPositiveButton("확인") { dialog, _ ->
            openYouTubeLink(videoUrl)
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openYouTubeLink(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        startActivity(intent)
    }
}
