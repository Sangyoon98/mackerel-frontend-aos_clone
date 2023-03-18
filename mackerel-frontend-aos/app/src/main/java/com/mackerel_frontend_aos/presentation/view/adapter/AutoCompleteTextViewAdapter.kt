package com.mackerel_frontend_aos.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.mackerel_frontend_aos.data.model.SchoolListDataList
import java.util.*

class AutoCompleteTextViewAdapter(context: Context, viewResourceId: Int, items: List<SchoolListDataList>) : ArrayAdapter<SchoolListDataList>(context, viewResourceId, items) {
    private val items: List<SchoolListDataList>
    private val itemsAll: List<SchoolListDataList>
    private val suggestions: List<SchoolListDataList>
    private val viewResourceId: Int

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
            v = vi.inflate(viewResourceId, parent, false)
        }

        val product: SchoolListDataList = items[position]
        val school = v!!.findViewById<View>(com.mackerel_frontend_aos.R.id.school) as TextView
        school.text = product.name
        val address = v.findViewById<View>(com.mackerel_frontend_aos.R.id.address) as TextView
        address.text = product.address
        return v
    }

    override fun getFilter(): Filter { return nameFilter }

    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String { return (resultValue as SchoolListDataList).name }

        override fun performFiltering(constraint: CharSequence?): FilterResults? { return null }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        }
    }

    init {
        this.items = items
        itemsAll = items
        suggestions = ArrayList<SchoolListDataList>()
        this.viewResourceId = viewResourceId
    }
}