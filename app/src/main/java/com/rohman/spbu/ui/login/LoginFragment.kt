package com.rohman.spbu.ui.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.widget.textChanges
import com.rohman.spbu.R
import com.rohman.spbu.databinding.FragmentLoginBinding
import com.rohman.spbu.ui.home.MainActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(requireContext(), MainActivity::class.java)

        binding.apply {
            buttonLogin.setOnClickListener {
                buttonLogin.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryDark
                    )
                )
                requireActivity().startActivity(intent)
            }

            inputUsername
                .textChanges()
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .compose(lengthMoreThanFour)
                .compose(retryWhenError {
                    inputUsername.error = it.message
                    inputUsername.background = ContextCompat.getDrawable(requireContext(),R.drawable.input_text_error_stroke)
                })
                .doOnNext {
                    inputUsername.background = ContextCompat.getDrawable(requireContext(),R.drawable.edit_text_stroke)
                }
                .subscribe()

            inputPassword
                .textChanges()
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .compose(lengthMoreThanFour)
                .compose(retryWhenError {
                    inputPassword.error = it.message
                    inputPassword.background = ContextCompat.getDrawable(requireContext(),R.drawable.input_text_error_stroke)
                })
                .doOnNext {
                    inputPassword.background = ContextCompat.getDrawable(requireContext(),R.drawable.edit_text_stroke)
                }
                .subscribe()

            buttonOffline.setOnClickListener {
                buttonOffline.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorGray
                    )
                )
                requireActivity().onBackPressed()
            }
        }

    }

    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> =
        ObservableTransformer { observable ->
            observable.retryWhen { errors ->
                errors.flatMap {
                    onError(it)
                    Observable.just(it)
                }
            }
        }

    private val lengthMoreThanFour = ObservableTransformer<String, String> { observable ->
        observable.flatMap { it ->
            Observable.just(it).map { it.trim() }.filter { it.length > 4 }.singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception("Panjang harus lebih dari 4"))
                    } else {
                        Single.error(it)
                    }
                }.toObservable()
        }
    }

}