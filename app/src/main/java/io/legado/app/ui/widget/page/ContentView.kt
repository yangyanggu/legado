package io.legado.app.ui.widget.page

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import io.legado.app.R
import io.legado.app.constant.AppConst.TIME_FORMAT
import io.legado.app.help.ImageLoader
import io.legado.app.help.ReadBookConfig
import io.legado.app.utils.*
import kotlinx.android.synthetic.main.view_book_page.view.*
import org.jetbrains.anko.matchParent
import java.util.*


class ContentView : FrameLayout {
    private var isScroll: Boolean = false
    private val bgImage: AppCompatImageView = AppCompatImageView(context)
        .apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

    constructor(context: Context) : super(context) {
        this.isScroll = true
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        //设置背景防止切换背景时文字重叠
        setBackgroundColor(context.getCompatColor(R.color.background))
        addView(bgImage, LayoutParams(matchParent, matchParent))
        if (isScroll) {
            inflate(context, R.layout.view_book_page_scroll, this)
        } else {
            inflate(context, R.layout.view_book_page, this)
        }
        top_bar.layoutParams.height = context.getStatusBarHeight()
        upStyle()
        upTime()
        content_text_view.customSelectionActionModeCallback =
            ContentSelectActionCallback(content_text_view)
    }

    fun upStyle() {
        ReadBookConfig.getConfig().apply {
            val pt = if (context.getPrefBoolean("hideStatusBar", false)) {
                top_bar.visible()
                0
            } else {
                top_bar.gone()
                context.getStatusBarHeight()
            }
            page_panel.setPadding(paddingLeft.dp, pt, paddingRight.dp, 0)
            content_text_view.setPadding(0, paddingTop.dp, 0, paddingBottom.dp)
            content_text_view.textSize = textSize.toFloat()
            content_text_view.setLineSpacing(lineSpacingExtra.toFloat(), lineSpacingMultiplier)
            content_text_view.letterSpacing = letterSpacing
            content_text_view.paint.isFakeBoldText = textBold
            textColor().let {
                content_text_view.setTextColor(it)
                tv_top_left.setTextColor(it)
                tv_top_right.setTextColor(it)
                tv_bottom_left.setTextColor(it)
                tv_bottom_right.setTextColor(it)
            }
        }
        context.getPrefString("readBookFont")?.let {
            if (it.isNotEmpty()) {
                content_text_view.typeface = Typeface.createFromFile(it)
            } else {
                content_text_view.typeface = Typeface.DEFAULT
            }
        }
    }

    fun setBg(bg: Drawable?) {
        //all supported
        ImageLoader.load(context, bg)
            .centerCrop()
            .setAsDrawable(bgImage)
    }

    fun upTime() {
        tv_top_left.text = TIME_FORMAT.format(Date(System.currentTimeMillis()))
    }

    fun upBattery(battery: Int) {
        tv_top_right.text = context.getString(R.string.battery_show, battery)
    }

    @SuppressLint("SetTextI18n")
    fun setContent(page: TextPage?) {
        content_text_view.text = page?.text
        tv_bottom_left.text = page?.title
        tv_bottom_right.text = "${page?.index?.plus(1)}/${page?.pageSize}"
    }

    fun isTextSelected(): Boolean {
        return content_text_view.selectionEnd - content_text_view.selectionStart != 0
    }

    fun contentTextView(): ContentTextView? {
        return content_text_view
    }
}