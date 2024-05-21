package com.example.photoeditorv2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterList : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filter1 = view.findViewById<ImageView>(R.id.filter1)
        filter1.setOnClickListener {
            val intent = Intent(activity, FirstAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }

        val filter2 = view.findViewById<ImageView>(R.id.filter2)
        filter2.setOnClickListener {
            val intent = Intent(activity, SecondAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }

        val filter3 = view.findViewById<ImageView>(R.id.filter3)
        filter3.setOnClickListener {
            val intent = Intent(activity, ThirdAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }
        val filter4 = view.findViewById<ImageView>(R.id.filter4)
        filter4.setOnClickListener {
            val intent = Intent(activity, FourthAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }
        val filter5 = view.findViewById<ImageView>(R.id.filter5)
        filter5.setOnClickListener {
            val intent = Intent(activity, FifthAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }
        val filter7 = view.findViewById<ImageView>(R.id.filter7)
        filter7.setOnClickListener {
            val intent = Intent(activity, SeventhAlgorithm::class.java)
            startActivity(intent)
            dismiss()
        }

        //и тд
    }
}