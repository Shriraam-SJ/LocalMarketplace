package com.example.localmarketplace

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
        displayProductsProgressively()
    }

    private fun displayProductsProgressively() {
        gridMyProducts.removeAllViews()
        ProductRepository.getProductIds { ids ->
            activity?.runOnUiThread {
                if (ids != null) {
                    for (id in ids) {
                        val placeholder = createPlaceholderCard()
                        gridMyProducts.addView(placeholder)
                        fetchIndividualProduct(id, placeholder)
                    }
                } else {
                    Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchIndividualProduct(id: String, placeholder: CardView) {
        ProductRepository.getProductById(id) { product ->
            activity?.runOnUiThread {
                if (product != null) {
                    updateCardWithProduct(placeholder, product)
                } else {
                    gridMyProducts.removeView(placeholder)
                }
            }
        }
    }

    private fun createPlaceholderCard(): CardView {
        val cardView = CardView(requireContext()).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 180.dpToPx()
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
            }
            radius = 12.dpToPx().toFloat()
            cardElevation = 2.dpToPx().toFloat()
        }

        val progressBar = ProgressBar(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
            )
        }
        
        val frameLayout = FrameLayout(requireContext())
        frameLayout.addView(progressBar)
        cardView.addView(frameLayout)
        
        return cardView
    }

    private fun updateCardWithProduct(cardView: CardView, product: Product) {
        cardView.removeAllViews()
        cardView.cardElevation = 6.dpToPx().toFloat()
        
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

        cardView.setOnClickListener {
            ProductRepository.selectedProduct = product
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            startActivity(intent)
        }

        cardView.setOnLongClickListener {
            selectedProduct = product
            requireActivity().openContextMenu(it)
            true
        }

        registerForContextMenu(cardView)
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
                    ProductRepository.selectedProduct = it
                    val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            "Delete" -> {
                selectedProduct?.id?.let { id ->
                    ProductRepository.deleteProduct(id) { success ->
                        activity?.runOnUiThread {
                            if (success) {
                                Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                                displayProductsProgressively()
                            } else {
                                Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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
