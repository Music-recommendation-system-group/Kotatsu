package org.koitharu.kotatsu.settings.tools

import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.Insets
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import com.google.android.material.color.MaterialColors
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.base.ui.BaseFragment
import org.koitharu.kotatsu.base.ui.widgets.SegmentedBarView
import org.koitharu.kotatsu.databinding.FragmentToolsBinding
import org.koitharu.kotatsu.download.ui.DownloadsActivity
import org.koitharu.kotatsu.settings.AppUpdateChecker
import org.koitharu.kotatsu.settings.SettingsActivity
import org.koitharu.kotatsu.settings.tools.model.StorageUsage
import org.koitharu.kotatsu.utils.FileSize
import org.koitharu.kotatsu.utils.ext.getThemeColor
import com.google.android.material.R as materialR


class ToolsFragment : BaseFragment<FragmentToolsBinding>(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

	private var updateChecker: AppUpdateChecker? = null
	private val viewModel by viewModel<ToolsViewModel>()

	override fun onInflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentToolsBinding {
		return FragmentToolsBinding.inflate(inflater, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.buttonSettings.setOnClickListener(this)
		binding.buttonDownloads.setOnClickListener(this)
		binding.cardUpdate.root.setOnClickListener(this)
		binding.cardUpdate.buttonDownload.setOnClickListener(this)
		binding.incognito.setOnCheckedChangeListener(this)

		viewModel.storageUsage.observe(viewLifecycleOwner, ::onStorageUsageChanged)
	}

	override fun onClick(v: View) {
		when (v.id) {
			R.id.button_settings -> startActivity(SettingsActivity.newIntent(v.context))
			R.id.button_downloads -> startActivity(DownloadsActivity.newIntent(v.context))
		}
	}

	override fun onCheckedChanged(button: CompoundButton?, isChecked: Boolean) {
		// TODO Incognito enabling logic
	}

	override fun onWindowInsetsChanged(insets: Insets) {
		binding.root.updatePadding(
			left = insets.left,
			right = insets.right,
			bottom = insets.bottom,
		)
	}

	private fun onStorageUsageChanged(usage: StorageUsage) {
		val storageSegment = SegmentedBarView.Segment(usage.savedManga.percent, segmentColor(1))
		val pagesSegment = SegmentedBarView.Segment(usage.pagesCache.percent, segmentColor(2))
		val otherSegment = SegmentedBarView.Segment(usage.otherCache.percent, segmentColor(3))

		with(binding.layoutStorage) {
			bar.segments = listOf(storageSegment, pagesSegment, otherSegment)
			val pattern = getString(R.string.memory_usage_pattern)
			labelStorage.text = pattern.format(
				FileSize.BYTES.format(root.context, usage.savedManga.bytes),
				getString(R.string.saved_manga)
			)
			labelPagesCache.text = pattern.format(
				FileSize.BYTES.format(root.context, usage.pagesCache.bytes),
				getString(R.string.pages_cache)
			)
			labelOtherCache.text = pattern.format(
				FileSize.BYTES.format(root.context, usage.otherCache.bytes),
				getString(R.string.other_cache)
			)
			labelAvailable.text = pattern.format(
				FileSize.BYTES.format(root.context, usage.available.bytes),
				getString(R.string.available)
			)
			TextViewCompat.setCompoundDrawableTintList(labelStorage, ColorStateList.valueOf(storageSegment.color))
			TextViewCompat.setCompoundDrawableTintList(labelPagesCache, ColorStateList.valueOf(pagesSegment.color))
			TextViewCompat.setCompoundDrawableTintList(labelOtherCache, ColorStateList.valueOf(otherSegment.color))
			if (!labelStorage.isVisible) {
				TransitionManager.beginDelayedTransition(root)
			}
			labelStorage.isVisible = true
			labelPagesCache.isVisible = true
			labelOtherCache.isVisible = true
		}
	}

	@ColorInt
	private fun segmentColor(i: Int): Int {
		val hue = (93.6f * i) % 360
		val color = ColorUtils.HSLToColor(floatArrayOf(hue, 0.5f, 0.5f))
		val backgroundColor = requireContext().getThemeColor(materialR.attr.colorSecondaryContainer)
		return MaterialColors.harmonize(color, backgroundColor)
	}

	companion object {

		fun newInstance() = ToolsFragment()
	}
}