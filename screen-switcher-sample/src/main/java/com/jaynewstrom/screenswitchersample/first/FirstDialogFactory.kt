package com.jaynewstrom.screenswitchersample.first

import android.app.Dialog
import android.content.Context
import com.jaynewstrom.screenswitchersample.R

import com.jnewstrom.screenswitcher.dialoghub.DialogFactory

import javax.inject.Inject

internal class FirstDialogFactory @Inject constructor() : DialogFactory {
    override fun createDialog(context: Context): Dialog {
        return FirstDialog(context)
    }
}

private class FirstDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.first_dialog)
    }
}