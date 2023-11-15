# PartiallyEditableText

PartiallyEditableText is an android library that allows the user to edit (overwrite) only a particular part of a text. 

# Showcase
PartiallyEditableText can be used in any project where you use text that needs to be completed with missing information for some reason, for example in 'fill in the gap' quizzes.
There is a sample project under the sample package.


# Dependency (Setup)
At the moment for using PartiallyEditableText you need a github personal access token and a github account (username).

For adding the package dependency, merge the following lines to your root `build.gradle` file:

```
dependencies {
    implementation 'io.github.rezita:partiallyeditabletext:0.0.2'    
}
```

Then edit your `build.gradle` file (for adding the repository) with merging the followings: 

```
dependencyResolutionManagement {
    repositories {    
        maven {
          url = uri("https://maven.pkg.github.com/rezita/PartiallyEditableText")
          credentials {
            username = "YOUR_USERNAME"
            password = "YOUR_GITHUB_TOKEN"
          }
        }
    }
}
```

After this, sync and clean your build.

NOTE: I recommend storing your access token and username in gradle.properties file to avoid publish this information - make sure it is outside of any version control system. 

# Usage (Example)
To use PartiallyEditableText, simply include it in your XML layout file or use the constructor and add it to the layout.

## Layout XML

```
<com.github.rezita.partiallyeditabletext.PartiallyEditableText
    android:id="@+id/editable"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:cursorVisible="true"
    android:inputType="textMultiLine|text"
    android:singleLine="false"
    app:baseText="Have you    the alarm clock? (reset)"
    app:editableStartIndex="9"
    app:editableTextColor="#3F51B5"
    app:editableTextStyle="bold"
    app:editableMaxLength="10"
    app:editableMinLength="8" />

```
For formatting baseText (like `textColor`, `textSize` etc.) use the normal EditText attribues.
NOTE: Setting the `android:text` attribute has no effect on the base text or editableText. (It will be ignored.)

## Kotlin
```
val edit = PartiallyEditableText(this, null)
edit.setBaseText(getString("What have you    with your hair? (do)"))
edit.setEditableStartIndex(14)
val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
layout.addView(edit)
```

# Attributes
```
<attr name="baseText" format="string"/>
<attr name="editableMinLength" format="integer"/>
<attr name="editableMaxLength" format="integer"/>
<attr name="editableStartIndex" format="integer"/>
<attr name="editableTextStyle">
  <flag name="normal" value="0" />
  <flag name="bold" value="1" />
  <flag name="italic" value="2" />
  <flag name="bold_italic" value="3" />
</attr>
<attr name="editableTextColor" format = "reference|color"/>
```

# Documentation

You can control or read the the following attributes:

| Name | Type | Description | Default Value | Code control | XML control |
| --- | --- | --- | --- | --- | --- |
| baseText | String | The immutable part of the whole test.| "" | yes | yes|
| editableStartIndex | Integer | The position in the `baseText` where the editable part starts. | 0 | yes| yes|
| editableMinLength | Integer | Minimum length of the mutable part. (If the editableText part is shorter than this value, underscores appear to fill the length) | 10 | yes | yes |
| editableMaxLength | Integer | Maximum length of the mutable part. If the length of mutable part reaches this value, the user cannot type more characters. | -1 | yes | yes |
| editableTextStyle | Integer | Style of the `editableText`. It can be normal, bold, italic or bold-italic | 0 | no | yes |
| editableTextColor | Color | The colour of the `editableText`. | -1 | no | yes |
| editableText | String | The mutable (editable) part of the whole text. If shorter than the `editableMinLength` underscores appear to complete the editable text. Only the user can change it by typing. | "" | no | no |

Other attributes such as the colour of the íbaseTextí can be set through the normal EditText attributes. (Except: `text`. NOTE: Setting the text has no effect on the base text or editableText. (It will be ignored.)

An `IllegalArgumentException` can be thrown by setting the attributes in the wrong way:
- if `editableMaxLength` is set, it must be larger than `editableMinLength`
- `editableStartIndex` has to be less than the length of the `baseText`
- `editableStartIndex` must be positive integer

`baseText` and `editableStartIndex` can be set at the same time by calling the `setBaseText(text: String, startIndex: Int)` public function of PartiallyEditableText.

# Next Steps
-  allow to modify `editableText`
-  hint for `editableText`
-  make `editableText` styleable from code 

# License
Library falls under [Apache 2.0] (LICENSE.md)

