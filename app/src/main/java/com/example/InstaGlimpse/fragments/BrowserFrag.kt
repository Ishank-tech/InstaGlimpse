package com.example.InstaGlimpse.fragments

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.InstaGlimpse.Models.storyModels.Item
import com.example.InstaGlimpse.R
import com.example.InstaGlimpse.repository.InstagramRepository
import com.example.InstaGlimpse.util.DirectoryUtils
import com.example.InstaGlimpse.util.Utils
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import papayacoders.instastory.utils.SharePrefs
import java.net.URI
import javax.inject.Inject

@AndroidEntryPoint
class BrowserFrag : Fragment() {

    @Inject
    lateinit var repository: InstagramRepository
    lateinit var editText : TextInputEditText
    lateinit var pasteBtn : Button
    lateinit var downloadBtn : Button
    lateinit var clipboard : ClipboardManager
    lateinit var items : List<Item>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_browser, container, false)

        editText = view.findViewById(R.id.insta_url)
        pasteBtn = view.findViewById(R.id.paste_btn)
        downloadBtn = view.findViewById(R.id.download_btn)

        getUrlFromInstagram()

        clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        pasteBtn.setOnClickListener{
            editText.setText(clipboard.text)
        }

        downloadBtn.setOnClickListener{
            downloadFromUrl()
        }

        return view
    }

    private fun downloadFromUrl() {
        val url = editText.text.toString()
        val link = "${getUrlWithoutParameters(url)}?__a=1&__d=dis"
        val cookies = "ds_user_id=${
            SharePrefs.getInstance(requireContext()).getString(SharePrefs.USERID)
        }; sessionid=${
            SharePrefs.getInstance(requireContext()).getString(SharePrefs.SESSIONID)
        }"

        GlobalScope.launch {

            val response = repository.downloadInstagramFile(link, cookies)

            if (response.isSuccess) {

                val userId = response.downloadUrls?.user?.id
                val newUrl = "https://i.instagram.com/api/v1/feed/user/${userId}/reel_media/"
                val res = repository.downloadStory(newUrl, cookies)
                if (res.isSuccess) {

                    items = res.downloadUrls?.items!!
                    for (item in items) {
                        if (url.contains(item.pk.toString())) {
                            if (item.media_type == 2) {
                                val downloadUrl = item.video_versions[0].url
                                Log.d("ISHANK", url)
                                Utils.startDownload(
                                    downloadUrl, DirectoryUtils.DIRECTORY, requireActivity(),
                                    "Instagram_story_" + System.currentTimeMillis() + ".mp4"
                                )
                            } else {
                                val downloadUrl = item.image_versions2.candidates[0].url
                                Utils.startDownload(
                                    downloadUrl, DirectoryUtils.DIRECTORY, requireActivity(),
                                    "Instagram_story_" + System.currentTimeMillis() + ".png"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUrlFromInstagram() {
        val intent = requireActivity().intent
        if (intent != null) {
            val action: String = intent.action.toString()
            val type: String = intent.type.toString()
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (type.equals("text/plain")) {
                    val data = intent.getStringExtra(Intent.EXTRA_TEXT)
                    editText.setText(data)
                }
            }
        }
    }

    private fun getUrlWithoutParameters(url: String): String {
        return try {
            val uri = URI(url)
            URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,  // Ignore the query part of the input url
                uri.fragment
            ).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }


}