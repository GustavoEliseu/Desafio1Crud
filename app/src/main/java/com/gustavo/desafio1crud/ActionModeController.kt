package com.gustavo.desafio1crud

import android.content.Context
import android.view.Menu
import androidx.appcompat.view.ActionMode
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.selection.SelectionTracker



class ActionModeController(private val context: Context, private val selectionTracker: SelectionTracker<Long>) :
    ActionMode.Callback {

    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        actionMode.getMenuInflater().inflate(R.menu.selected_menu,menu);
        Toast.makeText(context,"teste",Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        return false
    }

    //TODO - Implementar os cliques da actionBar

    override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
        when (menuItem.getItemId()) {
            R.id.delete_item ->
                // removeSelection();
                return true
            R.id.cancel_item ->
                    return selectionTracker.clearSelection()


            else -> return false
        }
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {
        selectionTracker.clearSelection()
        actionMode.finish()
    }
}