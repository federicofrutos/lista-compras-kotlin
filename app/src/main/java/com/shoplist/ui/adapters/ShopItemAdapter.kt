package com.shoplist.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shoplist.R
import com.shoplist.databinding.ItemShopBinding
import com.shoplist.models.ShopItem
import com.shoplist.util.Constants
import com.shoplist.util.viewHolderItemBinding

class ShopItemAdapter :
    RecyclerView.Adapter<ShopItemAdapter.ViewHolder>() {

    private lateinit var list: List<ShopItem>
    private lateinit var listener: ShopItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.viewHolderItemBinding(R.layout.item_shop) as ItemShopBinding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.isNotEmpty()) {
            val shopItem = list[position]

            holder.viewBinder.run {
                itemName.text = shopItem.name
                itemCost.text = Constants.formatCurrency(shopItem.itemCost)
                itemQuantity.text = String.format(
                    "%s ${Constants.returnItemsOrItem(shopItem.quantity)}",
                    shopItem.quantity
                )

                btnMore.setOnClickListener {

                    val popup = PopupMenu(
                        ContextThemeWrapper(root.context, R.style.ItemPopUpMenuStyle),
                        btnMore
                    )
                    popup.inflate(R.menu.shop_item_menu)
                    popup.setOnMenuItemClickListener {

                        if (it.itemId == R.id.item_edit) {
                            listener.onAction(shopItem, ShopItemAction.EDIT)
                        }
                        if (it.itemId == R.id.item_delete) {
                            listener.onAction(shopItem, ShopItemAction.DELETE)
                        }
                        true
                    }

                    val menuPopupHelper =
                        MenuPopupHelper(
                            ContextThemeWrapper(
                                root.context,
                                R.style.ItemPopUpMenuStyle
                            ), popup.menu as MenuBuilder, btnMore
                        )
                    menuPopupHelper.setForceShowIcon(true)
                    menuPopupHelper.show()
                }

                if (shopItem.isMarked) {
                    itemName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    itemCost.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    itemQuantity.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                //Set checkbox according to is marked
                itemCheck.isChecked = shopItem.isMarked

                //Set Strike through marked if marked is true
                setStrikeThrough(this, shopItem.isMarked)

                //Set Strike through according to OnCheckedChangeListener
                itemCheck.setOnCheckedChangeListener { _, isChecked ->
                    setStrikeThrough(this, isChecked)
                    shopItem.isMarked = isChecked
                    listener.onShopItemMarked(shopItem)
                }
            }
        }

    }

    fun setShopItemList(list: List<ShopItem>) {
        this.list = list
        this.asyncListDiffer.submitList(this.list)
    }

    fun setListener(listener: ShopItemListener) {
        this.listener = listener
    }

    private fun setStrikeThrough(binder: ItemShopBinding, isSet: Boolean) {
        if (isSet) {

            binder.itemName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binder.itemCost.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binder.itemQuantity.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<ShopItem>() {
        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffCallBack)

    class ViewHolder(val viewBinder: ItemShopBinding) : RecyclerView.ViewHolder(viewBinder.root) {

    }

    interface ShopItemListener {
        fun onShopItemMarked(shopItem: ShopItem)
        fun onAction(shopItem: ShopItem, action: ShopItemAction)
    }

    enum class ShopItemAction {
        EDIT, DELETE
    }
}