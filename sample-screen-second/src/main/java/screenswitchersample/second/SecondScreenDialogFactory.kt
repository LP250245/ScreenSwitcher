package screenswitchersample.second

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import com.jaynewstrom.screenswitcher.dialogmanager.DialogFactory
import javax.inject.Inject

internal class SecondScreenDialogFactory @Inject constructor() : DialogFactory {
    override fun createDialog(context: Context): Dialog {
        return AlertDialog.Builder(context).setTitle("Second Dialog").setMessage("Rotate to test!").create()
    }
}
