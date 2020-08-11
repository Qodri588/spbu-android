package com.rohman.spbu.ui.splashscreen

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.rohman.spbu.R
import com.rohman.spbu.databinding.FragmentWelcomeBinding
import com.rohman.spbu.ui.home.MainActivity

class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            Glide.with(requireActivity()).load(R.drawable.pertamina_logo_white)
                .into(imgPertaminaLogo)
            Glide.with(requireActivity()).load(R.drawable.welome_img).into(imgWelcome)

            buttonLogin.setOnClickListener {
                buttonLogin.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                    )
                )
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

//            buttonOffline.setOnClickListener {
//                buttonOffline.backgroundTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.colorGray
//                    )
//                )
//                val intent = Intent(requireContext(),MainActivity::class.java)
//                startActivity(intent)
//            }

        }
    }
}