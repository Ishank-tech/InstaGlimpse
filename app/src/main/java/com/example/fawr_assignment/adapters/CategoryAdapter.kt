package com.example.fawr_assignment.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fawr_assignment.fragments.BrowserFrag
import com.example.fawr_assignment.fragments.HomeFrag

class CategoryAdapter(context : Context, fm : FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            BrowserFrag()
        } else {
            HomeFrag()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            "Home"
        } else {
            "Browser"
        }
    }
}