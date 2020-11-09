package com.a99Spicy.a99spicy.ui.order

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.a99Spicy.a99spicy.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_order_slot.view.*

class OrderSlotFragment(private val clickListener: OnSlotClickListener) :
    BottomSheetDialogFragment() {

    interface OnSlotClickListener {
        fun onSlotClick(time: String)
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_slot, container, false)
        var time: String = getString(R.string.within_1_hour)

        val orderSlotRadioGroup = view.order_slot_radio_group
        val submitTimeSlotButton = view.submit_time_slot_button

        orderSlotRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.within_1_hr -> {
                    time = getString(R.string.within_1_hour)
                }
                R.id.within_2_hr -> {
                    time = getString(R.string.within_2_hour)
                }
                R.id.four_to_six -> {
                    time = getString(R.string._4_00_pm_to_6pm)
                }
                R.id.after_four -> {
                    time = getString(R.string.after_4_pm)
                }
                R.id.after_six -> {
                    time = getString(R.string.after_6_pm)
                }
                R.id.tomorrow_nine -> {
                    time = getString(R.string.tommorrow_9am)
                }
                else -> {
                    time = getString(R.string.tommorrow_12_pm)
                }
            }
        }

        submitTimeSlotButton.setOnClickListener {
            clickListener.onSlotClick(time)
            dismiss()
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view = View.inflate(context, R.layout.fragment_order_slot, null)
        val rootLayout: LinearLayout = view.findViewById(R.id.order_root_layout)
        val params: LinearLayout.LayoutParams = rootLayout.layoutParams as LinearLayout.LayoutParams
        params.height = getScreenHeight()
        rootLayout.layoutParams = params

        bottomSheetDialog.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        return bottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    //Get Screen Height
    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }
}