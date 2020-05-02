# NiceDialog
A Very Nice Dialog For Android Developer.

## 导入

如果连JCenter比较快的话就用这个方法。

```gradle
implementation 'dog.abcd:nicedialog:1.0.0'
```

或者就用下面这个方法，二选一即可。

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
//something else...
implementation 'com.github.michaellee123:NiceDialog:1.0.0'
```

## 简单使用

首先需要你的项目支持DataBinding，在gradle文件中添加如下代码

 ```gradle
android {
    //...
    dataBinding {
        enabled = true
    }
}
 ```
 
 ## 创建一个布局
 
 需要用`<layout></layout>`包裹整个布局。
 
 `dialog_nice.xml`
 
 ```xml
<layout>

    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/colorPrimary"
        android:gravity="center"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tvMessage"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="#ffffff"
            android:textSize="48sp"
            android:textStyle="bold" />

        <Button
            android:id="@+id/btnConfirm"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content" />

    </LinearLayout>
</layout>
 ```
 
 现在DataBinding会自动创建一个类 `DialogNiceBinding`。
 
 ## 使用NiceDialog显示一个弹窗
 
```kotlin
NiceDialog<DialogNiceBinding>(DialogNiceBinding.inflate(LayoutInflater.from(this)))
    .config {
        //可以在这里配置一些属性，按需配置即可，不用写完。
        width = WindowManager.LayoutParams.MATCH_PARENT //default is MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT //default is MATCH_PARENT
        gravity = Gravity.BOTTOM //this is default value
        backgroundDrawable = ColorDrawable(0x00000000) //this is default value
        paddingTop = 0 //this is default value
        paddingBottom = 48 //default is 0
        paddingLeft = 48 //default is 0
        paddingRight = 48 //default is 0
        cancelable = true //this is default value
        animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom //默认是0，0就没有动画
    }.bind { binding, dialog ->
        //在这里绑数据或者是事件
        binding.tvMessage.text = "Nice Dialog!"
        binding.btnConfirm.text = "Cool!"
        binding.btnConfirm.setOnClickListener { 
            //关闭弹窗
            dialog.dismiss()
        }
    }.show(supportFragmentManager, "tag")//tag可以为空，但是我建议最好赋值
```

简单的两步操作之后你就能看到一个这样的弹窗了。

![device-2020-05-02-201201.png](device-2020-05-02-201201.png)

你也可以使用 `tag` 来关闭弹窗。

```kotlin
NiceDialog.dismiss("tag")
```

## 代码封装

在项目中，一个弹窗通常会使用很多次，所以我也提供了`NiceDialogFactory`。举个例子。

`dialog_circle.xml`

```xml
<layout>

    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ProgressBar
            android:id="@+id/progressBar"
            style="?android:attr/progressBarStyle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true" />
    </RelativeLayout>
</layout>
```

然后再创建一个类继承`NiceDialogFactory`。

```kotlin
class CircleDialogFactory(context: Context) :
    NiceDialogFactory<DialogCircleBinding, Unit, Unit>(
        DialogCircleBinding.inflate(LayoutInflater.from(context))
    ) {
    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
        cancelable = false
    }

    override fun binder(): (DialogCircleBinding, NiceDialogFragment<DialogCircleBinding>) -> Unit =
        { _, _ ->
        }

}
```

调用。

```kotlin
CircleDialogFactory(this).create().show(supportFragmentManager, "circle").onDismiss {
    //可以添加关闭监听。
    Toast.makeText(this, "on dismiss", Toast.LENGTH_LONG).show()
}
```

## 复杂情况

比如说弹窗出来要做一个列表选择之类的，也可以很简单，在继承`NiceDialogFactory`做了一些操作之后，还能够继续在调用的时候对`NiceDialog`进行操作。

```kotlin
class TestDialogFactory(context: Context) :
    NiceDialogFactory<DialogNiceBinding, String, String>(
        DialogNiceBinding.inflate(LayoutInflater.from(context))
    ) {

    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.BOTTOM
    }

    override fun binder(): (DialogNiceBinding, NiceDialogFragment<DialogNiceBinding>) -> Unit =
        { binding, dialog ->
            binding.tvMessage.text = "Nice Factory!"
            binding.btnConfirm.text = "Confirm"
            binding.btnCancel.text = "Cancel"
            binding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                next?.invoke("Next!")
            }
            binding.btnCancel.setOnClickListener {
                dialog.dismiss()
                finish?.invoke("Finish!")
            }
        }
}
```

可以这样来用`TestDialogFactory`。

```kotlin
TestDialogFactory(this)
    .onNext {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }
    .onFinish {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
    }
    .create()
    .config {
        animatorStyleRes = R.style.NiceDialog_Animation_Zoom
    }
    .bind { binding, _ ->
        binding.tvMessage.text = "${binding.tvMessage.text}!!"
    }
    .show(supportFragmentManager, "tag")
```

现在会得到这样一个弹窗。

![device-2020-05-02-203649.png](device-2020-05-02-203649.png)