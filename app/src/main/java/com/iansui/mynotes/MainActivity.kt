package com.iansui.mynotes

import android.app.Application
import android.os.Bundle
import android.view.Menu
import android.view.SubMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.iansui.mynotes.database.Note
import com.iansui.mynotes.database.NotesDatabase
import com.iansui.mynotes.databinding.ActivityMainBinding
import com.iansui.mynotes.repository.NotesRepository
import com.iansui.mynotes.viewmodel.NotesViewModel
import com.iansui.mynotes.viewmodel.NotesViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sharedViewModel: NotesViewModel
    var temporaryCategoryList: ArrayList<String> = arrayListOf()
    lateinit var subMenu: SubMenu
    lateinit var categoryList: LiveData<List<Note>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment!!.findNavController()
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        navController.addOnDestinationChangedListener{ nc: NavController, nd: NavDestination, args:Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        NavigationUI.setupWithNavController(binding.navView, navController)

        val application: Application = requireNotNull(this).application

        val dataSource = NotesRepository(NotesDatabase.getInstance(application).notesDAO)

        val viewModelFactory = NotesViewModelFactory(dataSource)

        sharedViewModel = ViewModelProvider(this, viewModelFactory)
            .get(NotesViewModel::class.java)

        binding.navView.itemIconTintList = null

        val menu: Menu = binding.navView.menu
        subMenu = menu.addSubMenu("Categories")

        sharedViewModel.categories.observe(this, { categories ->
            categories.forEach { category ->
                if (!temporaryCategoryList.contains(category)) {
                    val subMenuItem = subMenu.add(category)
                    temporaryCategoryList.add(category)
                    subMenuItem.title = category
                    subMenuItem.setIcon(R.drawable.ic_category)
                }
            }
        })

        categoryList = sharedViewModel.notes

        binding.navView.setNavigationItemSelectedListener{ menuItem ->
            sharedViewModel.categories.observe(this, { categories ->
                categories.forEach { category ->
                    if (menuItem.title == category) {
                        categoryList = sharedViewModel.getCategory(category)
                        drawerLayout.closeDrawer(GravityCompat.START)

                        navController.navigateUp()
                        navController.navigate(R.id.notesByCategoryFragment)
                    }
                }
            })
            if (menuItem.itemId == R.id.my_notes) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
           return@setNavigationItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = navHostFragment!!.findNavController()
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}