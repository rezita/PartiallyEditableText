package com.github.rezita.partiallyeditabletext

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatEditText

/**
 * This is a partly editable text that means
 * part of the text is immutable (cannot be overwritten by the user)
 * and part of the text is mutable (can be modified by the user).
 */
@Suppress("SENSELESS_COMPARISON")
class PartiallyEditableText (context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs), OnClickListener {

    private val _EDITABLE_MIN_LENGTH = 10
    private val _EDITABLE_MAX_LENGTH = -1

    var editableText: String = ""
        private set
    private var editableTextStyle = 0
    private var editableTextColor = -1

    private var selfEdited = false

    var editableMinLength = _EDITABLE_MIN_LENGTH
        set(value) {
            field = value
            validateEditableLengthProperties()
            refreshText()
        }
    var editableMaxLength = _EDITABLE_MAX_LENGTH
        set(value) {
            field = value
            validateEditableLengthProperties()
            setInputLengthFilter()
            refreshText()
        }

    var editableStartIndex = 0
        private set

    var baseText: String = ""
        private set

    private val editableTextWatcher: EditableTextWatcher = EditableTextWatcher()
    private var textChangedListeners: ArrayList<TextWatcher>? = null

    init {
        isSaveEnabled = true
        addTextChangedListener(editableTextWatcher)
        setOnClickListener(this)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PartiallyEditableText)

            val originalFromXML = a.getString(R.styleable.PartiallyEditableText_baseText)
            if (originalFromXML != null) {
                baseText = originalFromXML.toString()
            }
            editableStartIndex =
                a.getInt(R.styleable.PartiallyEditableText_editableStartIndex, 0)
            editableMinLength =
                a.getInt(R.styleable.PartiallyEditableText_editableMinLength, _EDITABLE_MIN_LENGTH)
            editableTextStyle =
                a.getInt(R.styleable.PartiallyEditableText_editableTextStyle, editableTextStyle)
            editableTextColor =
                a.getColor(R.styleable.PartiallyEditableText_editableTextColor, -1)
            editableMaxLength =
                a.getInt(R.styleable.PartiallyEditableText_editableMaxLength, _EDITABLE_MAX_LENGTH)
            a.recycle()
        }
        validateEditableLengthProperties()
        validateStartIndex()
        setInputLengthFilter()
        refreshText()
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        super.dispatchSaveInstanceState(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        super.dispatchRestoreInstanceState(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val saveState = SavedState(superState)
        saveState.editableStartIndex = editableStartIndex
        saveState.editableText = editableText
        saveState.baseText = baseText
        saveState.editableTextStyle = editableTextStyle
        saveState.editableTextColor = editableTextColor
        return saveState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                //val saveState = SavedState(state)
                Log.i("restoreOriginal", state.baseText)
                super.onRestoreInstanceState(state.superState)
                Log.i("text", text.toString())

                editableText = state.editableText
                editableStartIndex = state.editableStartIndex
                baseText = state.baseText
                editableTextStyle = state.editableTextStyle
                editableTextColor = state.editableTextColor
                editableMinLength = state.editableMinLength
                editableMaxLength = state.editableMaxLength
            }
            else -> super.onRestoreInstanceState(state)
        }
        refreshText()
    }

    fun setBaseText(text: String, startIndex: Int) {
        baseText = text
        editableStartIndex = startIndex
        validateStartIndex()
        setInputLengthFilter()
        refreshText()
    }

    fun setBaseText(text: String) {
        baseText = text
        validateStartIndex()
        setInputLengthFilter()
        refreshText()
    }

    fun setEditableStartIndex(index: Int) {
        editableStartIndex = index
        validateStartIndex()
        refreshText()
    }

    private fun setInputLengthFilter() {
        if (editableMaxLength != _EDITABLE_MAX_LENGTH) {
            val maxLength = editableMaxLength + baseText.length
            filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        }
    }

    private fun validateEditableLengthProperties(){
        if (editableMaxLength != _EDITABLE_MAX_LENGTH && editableMaxLength < editableMinLength) {
            throw IllegalArgumentException(
                "editableMaxLength ($editableMaxLength)" +
                        " is smaller than editableMinLength ($editableMinLength)"
            )
        }
    }

    private fun validateStartIndex(){
        if (editableStartIndex < 0 ) {
            throw IllegalArgumentException(
                "Start index value is not valid: $editableStartIndex)"
            )
        }
        if (editableStartIndex > baseText.length) {
            throw IllegalArgumentException(
                "Start index value ($editableStartIndex) is larger than base text length " +
                        "(${baseText.length})"
            )
        }
    }

    private fun refreshText() {
        clearAllTextChangeListeners()
        selfEdited = true
        super.setText(getDisplayedText())
        selfEdited = false
        reapplyTextChangeListeners()
    }

    private fun applyChanges(text: String) {
        val startIndexInEditable = editableTextWatcher.changeStartIndex - editableStartIndex

        //in case if trying to delete from very beginning (before the text starts)
        if (startIndexInEditable == -1) {
            return
        }

        val startOfEndPart = Math.min(
            startIndexInEditable + editableTextWatcher.changeDeleted,
            editableText.length
        )
        val beginningPart = editableText.substring(0, startIndexInEditable)
        val endPart = editableText.substring(startOfEndPart, editableText.length)

        editableText = beginningPart + text + endPart
    }

    private fun getDisplayedText(): SpannableStringBuilder {
        val displayEditableText = getDisplayableEditableText()
        val spannedText = SpannableStringBuilder(baseText)
        spannedText.insert(editableStartIndex, displayEditableText)
        spannedText.setSpan(
            UnderlineSpan(),
            editableStartIndex,
            getLastDisplayPosition(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannedText.setSpan(
            StyleSpan(editableTextStyle),
            editableStartIndex,
            getLastDisplayPosition(),
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        if (editableTextColor != -1) {
            spannedText.setSpan(
                ForegroundColorSpan(editableTextColor),
                editableStartIndex,
                getLastDisplayPosition(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        return spannedText
    }

    private fun getDisplayableEditableText(): String {
        if (editableText == null) {
            editableText = ""
        }

        val lengthDiff = editableMinLength - editableText.length
        if (lengthDiff > 0) {
            return editableText + " ".repeat(lengthDiff)
        }
        return editableText
    }

    override fun onClick(v: View?) {
        setCursorPosition()
        isCursorVisible = true
    }

    private fun moveCursorPosition() {
        val newPosition =
            editableTextWatcher.changeStartIndex + editableTextWatcher.changeAdded
        if ((0 until getDisplayedText().length).contains(newPosition)) {
            setSelection(newPosition)
        }
        setCursorPosition()
    }

    private fun setCursorPosition() {
        if (!isSelectionInRightPosition()) {
            setLastEditablePosition()
        }
    }

    private fun setLastEditablePosition() {
        setSelection(getLastEditedPosition())
    }

    private fun getLastEditedPosition(): Int {
        return editableStartIndex + getEditableLength()
    }

    private fun getLastDisplayPosition(): Int {
        return editableStartIndex + getEditableDisplayLength()
    }

    private fun isSelectionInRightPosition(): Boolean {
        return (editableStartIndex..editableStartIndex + getEditableLength()).contains(selectionEnd)
    }

    private fun getEditableDisplayLength(): Int {
        return getDisplayableEditableText().length
    }

    private fun getEditableLength(): Int {
        return editableText.length
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (editableText != null) {
            setCursorPosition()
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (textChangedListeners == null) {
            textChangedListeners = ArrayList<TextWatcher>()
        }
        if (watcher != null) {
            textChangedListeners!!.add(watcher)
        }
        super.addTextChangedListener(watcher)
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        textChangedListeners!!.remove(watcher)
        super.removeTextChangedListener(watcher)
    }

    private fun clearAllTextChangeListeners(){
        textChangedListeners!!.forEach { it -> super.removeTextChangedListener(it) }
    }

    private fun reapplyTextChangeListeners(){
        textChangedListeners!!.forEach { it -> super.addTextChangedListener(it) }
    }

    class SavedState : BaseSavedState {
        var editableText: String = ""
        var baseText: String = ""
        var editableStartIndex: Int = -1
        var editableTextStyle: Int = 0
        var editableTextColor: Int = 0
        var editableMinLength: Int = 10
        var editableMaxLength: Int = -1


        constructor(parcel: Parcel) : super(parcel)
        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(out: Parcel, flags: Int) {
            Log.i("write to parcel", "writet to parcel")
            super.writeToParcel(out, flags)
            out.writeString(editableText)
            out.writeString(baseText)
            out.writeInt(editableStartIndex)
            out.writeInt(editableTextStyle)
            out.writeInt(editableTextColor)
            out.writeInt(editableMinLength)
            out.writeInt(editableMaxLength)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    private inner class EditableTextWatcher() : TextWatcher {
        var changeStartIndex = 0
        var changeDeleted = 0
        var changeAdded = 0

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            changeStartIndex = start
            changeAdded = after
            changeDeleted = count
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            //for avoiding crash when setText(text: sharArray, start: Int, len: Int) is called
            //it is a final method of TextView
            if (!hasFocus()) {
                refreshText()
                return
            }

            if (!selfEdited) {
                val insertedText = getInsertedText(s.toString())
                applyChanges(insertedText)
                refreshText()
                moveCursorPosition()
            }
        }

        private fun getInsertedText(fullText: String): String {
            return fullText.substring(changeStartIndex, changeStartIndex + changeAdded)
        }

    }
}
