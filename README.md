## Example of creating a complex custom AlertDialog.

The project contains examples of working with color.
Convert ARGB color to its HSV components to obtain hue, saturation, and value.
The round cursor changes color depending on the brightness of the color.

![PaletteDialog1](https://github.com/user-attachments/assets/b1a34c87-77f9-4693-af95-7446cfe408b4)
![PaletteDialog2](https://github.com/user-attachments/assets/12777040-1255-4835-8cbe-1029ae06a8cc)

The libs folder contains the compiled PaletteDialog.aar library.

Dependencies:
```
implementation(files("libs/PaletteDialog.aar"))
```

Usage

Kotlin

```
private fun openDialog() {
    PaletteDialog(this, myColor)
        .setOnClickListener {
            myColor = it
        }
}
```

If necessary, you can change the color of the buttons and the color of the button text.

```
private fun openDialog() {
    val dialog = PaletteDialog(this, myColor)
    dialog.buttonOkColor = -2490390
    dialog.textOkColor = -16546298
    dialog.buttonCancelColor = -6426
    dialog.textCancelColor = -3012857
    dialog.setOnClickListener {
        myColor = it
    }
}
```

Java

```
private void openDialog() {
    PaletteDialog dialog = new PaletteDialog(this, myColor);
    dialog.setOnClickListener(color -> {
        myColor = color;
        return null;
    });
}
```

