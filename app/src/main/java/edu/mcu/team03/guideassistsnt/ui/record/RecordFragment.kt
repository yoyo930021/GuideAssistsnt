package edu.mcu.team03.guideassistsnt.ui.record


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.mcu.team03.guideassistsnt.R
import kotlinx.android.synthetic.main.fragment_record.*
import org.jetbrains.anko.support.v4.startActivity


/**
 * A simple [Fragment] subclass.
 */
class RecordFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 0 until typeList.childCount) {
            typeList.getChildAt(i).setOnClickListener { inView ->
                startActivity<RecordActivity>("type" to inView.tag.toString())
            }
        }
    }

}// Required empty public constructor
