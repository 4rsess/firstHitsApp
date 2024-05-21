package com.example.photoeditor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.photoeditor.databinding.FragmentFilterBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterList : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.filter1).setOnClickListener {
            startActivity(Intent(activity, FirstAlgorithm::class.java))
            dismiss()
        }





        view.findViewById<ImageView>(R.id.filter4).setOnClickListener {
            startActivity(Intent(activity, FourthAlgorithm::class.java))
            dismiss()
        }
    }


}