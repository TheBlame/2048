package com.example.a2048.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.a2048.R
import com.example.a2048.databinding.DialogGameOverBinding
import com.example.a2048.util.DialogListener

class GameOverDialogFragment : DialogFragment() {
    private lateinit var listener: DialogListener

    private var _binding: DialogGameOverBinding? = null
    private val binding: DialogGameOverBinding
        get() = _binding ?: throw RuntimeException("DialogGameOverBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogGameOverBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogHome.setOnClickListener {
            listener.onNegativeClick()
            dialog?.dismiss()
        }
        binding.dialogRetry.setOnClickListener {
            listener.onPositiveClick()
            dialog?.dismiss()
        }
    }

    companion object {
        const val TAG = "GameOverDialogFragment"
    }
}