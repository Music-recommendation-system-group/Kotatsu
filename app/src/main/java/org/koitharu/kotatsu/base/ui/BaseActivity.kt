package org.koitharu.kotatsu.base.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import org.koin.android.ext.android.get
import org.koitharu.kotatsu.BuildConfig
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.exceptions.resolve.ExceptionResolver
import org.koitharu.kotatsu.core.prefs.AppSettings

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(), OnApplyWindowInsetsListener {

	protected lateinit var binding: B
		private set


	protected val exceptionResolver by lazy(LazyThreadSafetyMode.NONE) {
		ExceptionResolver(this, supportFragmentManager)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		if (get<AppSettings>().isAmoledTheme) {
			setTheme(R.style.AppTheme_Amoled)
		}
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
	}

	@Deprecated("Use ViewBinding", level = DeprecationLevel.ERROR)
	override fun setContentView(layoutResID: Int) {
		super.setContentView(layoutResID)
		setupToolbar()
	}

	@Deprecated("Use ViewBinding", level = DeprecationLevel.ERROR)
	override fun setContentView(view: View?) {
		super.setContentView(view)
		setupToolbar()
	}

	protected fun setContentView(binding: B) {
		this.binding = binding
		super.setContentView(binding.root)
		(binding.root.findViewById<View>(R.id.toolbar) as? Toolbar)?.let(this::setSupportActionBar)
		ViewCompat.setOnApplyWindowInsetsListener(binding.root, this)
	}

	override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
		onWindowInsetsChanged(insets.getInsets(WindowInsetsCompat.Type.systemBars()))
		return insets
	}

	override fun onOptionsItemSelected(item: MenuItem) = if (item.itemId == android.R.id.home) {
		onBackPressed()
		true
	} else super.onOptionsItemSelected(item)

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // TODO remove
			ActivityCompat.recreate(this)
			return true
		}
		return super.onKeyDown(keyCode, event)
	}

	protected abstract fun onWindowInsetsChanged(insets: Insets)

	private fun setupToolbar() {
		(findViewById<View>(R.id.toolbar) as? Toolbar)?.let(this::setSupportActionBar)
	}

}