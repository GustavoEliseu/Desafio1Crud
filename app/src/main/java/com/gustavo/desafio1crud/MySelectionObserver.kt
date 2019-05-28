package com.gustavo.desafio1crud

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker

class MySelectionObserver(var actionMode: ActionMode?,val selectionTracker: SelectionTracker<Long>,val context: Context): SelectionTracker.SelectionObserver<Long>(){

    override fun onSelectionChanged() {
        super.onSelectionChanged();
        if (selectionTracker.hasSelection() && actionMode == null) {
            actionMode = (context as AppCompatActivity).startSupportActionMode( ActionModeController(context,
            selectionTracker) as ActionMode.Callback);
        } else if (!selectionTracker.hasSelection() && actionMode != null) {
            actionMode!!.finish();
            actionMode = null;
        }
        var itemIterable:Iterator<Long>  = selectionTracker.getSelection().iterator();
        while (itemIterable.hasNext()) {
            Log.i(TAG, itemIterable.next().toString());
        }
    }
}