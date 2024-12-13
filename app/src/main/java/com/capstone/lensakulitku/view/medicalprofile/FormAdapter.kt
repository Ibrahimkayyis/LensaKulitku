import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.lensakulitku.R
import com.capstone.lensakulitku.view.medicalprofile.FieldData
import java.util.Calendar

class FormAdapter(
    private val formData: List<FieldData>,
    private val onFieldChange: (Int, String) -> Unit
) : RecyclerView.Adapter<FormAdapter.FormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_form_field, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val field = formData[position]
        holder.bind(field, position)
    }

    override fun getItemCount(): Int = formData.size

    inner class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val label: TextView = itemView.findViewById(R.id.tvFieldLabel)
        private val input: EditText = itemView.findViewById(R.id.etFieldInput)

        fun bind(field: FieldData, position: Int) {
            label.text = field.label
            input.setText(field.value)

            // Handle DatePicker khusus untuk "Date of Birth"
            if (field.label == "Date of Birth") {
                input.isFocusable = false
                input.setOnClickListener {
                    showDatePicker(itemView.context, input) { date ->
                        input.setText(date)
                        onFieldChange(position, date)
                    }
                }
            } else {
                // Handle input lainnya termasuk "Medication"
                input.isFocusableInTouchMode = true
                input.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        onFieldChange(position, input.text.toString())
                    }
                }
            }
        }

        private fun showDatePicker(context: Context, editText: EditText, onDateSelected: (String) -> Unit) {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val date = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                onDateSelected(date)
            }, year, month, day).show()
        }
    }
}
