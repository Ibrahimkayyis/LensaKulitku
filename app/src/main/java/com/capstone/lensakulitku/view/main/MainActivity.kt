package com.capstone.lensakulitku.view.main

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var homeTab: View
    private lateinit var historyTab: View
    private lateinit var cameraTab: View
    private lateinit var trackingTab: View
    private lateinit var moreTab: View

    private lateinit var homeIcon: ImageView
    private lateinit var historyIcon: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var trackingIcon: ImageView
    private lateinit var moreIcon: ImageView

    private lateinit var homeIndicator: View
    private lateinit var historyIndicator: View
    private lateinit var trackingIndicator: View
    private lateinit var moreIndicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize tabs, icons, and indicators
        homeTab = binding.homeTab
        historyTab = binding.historyTab
        cameraTab = binding.cameraTab
        trackingTab = binding.trackingTab
        moreTab = binding.moreTab

        homeIcon = binding.homeIcon
        historyIcon = binding.historyIcon
        cameraIcon = binding.cameraIcon
        trackingIcon = binding.trackingIcon
        moreIcon = binding.moreIcon

        homeIndicator = binding.homeIndicator
        historyIndicator = binding.historyIndicator
        trackingIndicator = binding.trackingIndicator
        moreIndicator = binding.moreIndicator

        // Set initial active tab (assuming home is default)
        setActiveTab(homeTab)

        // Set up onClickListeners for tabs
        homeTab.setOnClickListener {
            navController.navigate(R.id.homeFragment)
        }

        historyTab.setOnClickListener {
            navController.navigate(R.id.historyFragment)
        }

        cameraTab.setOnClickListener {
            navController.navigate(R.id.scanFragment)
        }

        trackingTab.setOnClickListener {
            navController.navigate(R.id.trackingFragment)
        }

        moreTab.setOnClickListener {
            navController.navigate(R.id.moreOptionsFragment)
        }

        // Observe navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->

            // Hide Bottom Navigation for specific fragments
            val hideNavFragments = setOf(
                R.id.medicalProfileFragment,
                R.id.cameraXFragment,
                R.id.checkPhotoFragment,
                R.id.describeSymptomsFragment,
                R.id.analysisLoadingFragment,
                R.id.resultAnalysisFragment,
                R.id.trackingCameraXFragment,
                R.id.trackingCheckPhotoFragment,
                R.id.trackingDescribeSymptomsFragment,
                R.id.trackingAnalysisLoadingFragment,
                R.id.trackingResultFragment,
                R.id.doctorListFragment,
                R.id.chatFragment
            )

            if (destination.id in hideNavFragments) {
                binding.customBottomNavigationView.visibility = View.GONE
            } else {
                binding.customBottomNavigationView.visibility = View.VISIBLE
            }

            // Update active tab based on current destination
            when (destination.id) {
                R.id.homeFragment -> {
                    setActiveTab(homeTab)
                }
                R.id.historyFragment -> {
                    setActiveTab(historyTab)
                }
                R.id.scanFragment -> {
                    setActiveTab(cameraTab)
                }
                R.id.trackingFragment -> {
                    setActiveTab(trackingTab)
                }
                R.id.moreOptionsFragment -> {
                    setActiveTab(moreTab)
                }
            }
        }

    }

    private fun setActiveTab(activeTab: View) {
        // Reset icons and indicators
        homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray))
        historyIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray))
        cameraIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray))
        trackingIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray))
        moreIcon.setColorFilter(ContextCompat.getColor(this, R.color.gray))

        homeIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        historyIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        trackingIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
        moreIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))

        homeIndicator.visibility = View.GONE
        historyIndicator.visibility = View.GONE
        trackingIndicator.visibility = View.GONE
        moreIndicator.visibility = View.GONE

        // Set active icon and indicator
        when (activeTab.id) {
            R.id.homeTab -> {
                homeIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue))
                homeIndicator.visibility = View.VISIBLE
                homeIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            }
            R.id.historyTab -> {
                historyIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue))
                historyIndicator.visibility = View.VISIBLE
                historyIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            }
            R.id.cameraTab -> {
                cameraIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue))
                // Tidak ada indicator untuk camera tab? Jika perlu, tambahkan indicator serupa.
            }
            R.id.trackingTab -> {
                trackingIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue))
                trackingIndicator.visibility = View.VISIBLE
                trackingIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            }
            R.id.moreTab -> {
                moreIcon.setColorFilter(ContextCompat.getColor(this, R.color.blue))
                moreIndicator.visibility = View.VISIBLE
                moreIndicator.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
            }
        }
    }
}
