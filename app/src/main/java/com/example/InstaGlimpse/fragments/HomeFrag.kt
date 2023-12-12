package com.example.InstaGlimpse.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.adapters.StoryAdapter
import com.example.InstaGlimpse.adapters.ProfileAdapter
import com.example.InstaGlimpse.util.UserInterface
import papayacoders.instastory.Stories
import papayacoders.instastory.models.TrayModel

class HomeFrag : Fragment(), UserInterface {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.userProfileRecyclerView)
        val storyRecyclerView = view.findViewById<RecyclerView>(R.id.storyRecyclerView)

        val profileAdapter = ProfileAdapter(this)
        recyclerView.adapter = profileAdapter

        val storyAdapter = StoryAdapter()
        storyRecyclerView.adapter = storyAdapter

        val button : Button = view.findViewById(R.id.login)
        button.setOnClickListener{
            Stories.login(requireActivity())
        }

        if(Stories.isLogin(requireActivity())){
            button.visibility = View.GONE
        }

        Stories.users(requireActivity())

        Stories.storyList.observe(requireActivity()) {
            storyAdapter.submitList(it)
        }

        Stories.list.observe(requireActivity()) {
            profileAdapter.submitList(it)
        }

        return view
    }

    override fun userInterfaceClick(i: Int, trayModel: TrayModel) {
        Stories.getStories(context, trayModel.user.pk.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(Stories.isLogin(requireActivity())){
            Stories.users(requireActivity())
        }
    }

}