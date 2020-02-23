package com.routesme.taxi_screen.kotlin.View.AdminConsole.dummy

import java.util.ArrayList

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object listItem {

    val ITEMS: MutableList<Item> = ArrayList()

    init {
        // Add some sample items.
        addItem(Item(0,"Info"))
        addItem(Item(1,"Account"))
        addItem(Item(2,"Settings"))
    }

    private fun addItem(item: Item) {
        ITEMS.add(item)
    }

    data class Item(val id: Int, val title: String)

    enum class ItemView { Info, Account, Settings }
}
