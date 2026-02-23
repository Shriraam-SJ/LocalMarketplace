package com.example.localmarketplace

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class MyProductsFragment : Fragment() {

    private lateinit var gridMyProducts: GridLayout
    private var selectedProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customers, container, false)
        gridMyProducts = view.findViewById(R.id.gridMyProducts)
        return view
    }

    override fun onResume() {
        super.onResume()
        displayProducts()
    }

    private fun displayProducts() {
        gridMyProducts.removeAllViews()
        val products = ProductRepository.getProducts()

        for (product in products) {
            val cardView = createProductCard(product)
            gridMyProducts.addView(cardView)
        }
    }

    private fun createProductCard(product: Product): CardView {
        val cardView = CardView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
            }
            radius = 12.dpToPx().toFloat()
            cardElevation = 6.dpToPx().toFloat()
            isClickable = true
            isFocusable = true
        }

        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
        }

        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                100.dpToPx()
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setImageResource(R.drawable.ic_marketproducts)
        }

        val tvName = TextView(requireContext()).apply {
            text = product.name
            setTextColor(resources.getColor(R.color.black, null))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 8.dpToPx() }
        }

        val tvPrice = TextView(requireContext()).apply {
            text = product.price
            setTextColor(resources.getColor(R.color.dark_blue, null))
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val tvSeller = TextView(requireContext()).apply {
            text = "Seller: ${product.seller}"
            textSize = 12f
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 4.dpToPx() }
        }

        val tvLocation = TextView(requireContext()).apply {
            text = "Loc: ${product.location}"
            textSize = 11f
            setTextColor(resources.getColor(R.color.grey, null))
        }

        linearLayout.addView(imageView)
        linearLayout.addView(tvName)
        linearLayout.addView(tvPrice)
        linearLayout.addView(tvSeller)
        linearLayout.addView(tvLocation)
        cardView.addView(linearLayout)

        cardView.setOnLongClickListener {
            selectedProduct = product
            requireActivity().openContextMenu(it)
            true
        }

        registerForContextMenu(cardView)

        return cardView
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Product Options")
        menu.add(0, v.id, 0, "About")
        menu.add(0, v.id, 0, "Delete")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            "About" -> {
                selectedProduct?.let {
                    val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                        putExtra("PRODUCT_NAME", it.name)
                        putExtra("PRODUCT_PRICE", it.price)
                        putExtra("PRODUCT_LOCATION", it.location)
                        putExtra("PRODUCT_SELLER", it.seller)
                    }
                    startActivity(intent)
                }
                true
            }
            "Delete" -> {
                selectedProduct?.let {
                    ProductRepository.deleteProduct(it)
                    displayProducts()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}
