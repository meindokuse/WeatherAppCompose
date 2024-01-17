package com.example.yourweather.screens.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.yourweather.models.AppAction
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCityDialog(
    onAddCity: (String) -> Unit,
    dialogState: MutableState<Boolean>
) {
    var cityName by remember { mutableStateOf("") }
    var error by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            dialogState.value = false
        },
        title = {
            Text("Добавить город")
        },
        text = {
            Column {
                Text("Введите название города:")
                TextField(
                    value = cityName,
                    onValueChange = { cityName = it  },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(color = LocalContentColor.current),
                    isError = error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (cityName.isNotBlank()) {
                        dialogState.value = false
                        onAddCity(cityName)
                    } else error = true
                },
                colors =  ButtonDefaults.buttonColors(
                   containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    dialogState.value = false
                },
                colors =  ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DialogShouldShowRationale(
    permissionState: PermissionState,
    dialogState:MutableState<Boolean>
){
    AlertDialog(
        onDismissRequest = {
            dialogState.value = false
        },
        confirmButton = {
            Button(onClick = {
                permissionState.launchPermissionRequest()
                dialogState.value = false
            }) {
                Text("Предоставить разрешение")
            }
        },
        title = {
            Text("Нам нужно разрешение на доступ к местоположению")
        },
        text = {
            Text("Мы используем ваше местоположение для предоставления вам более точных данных.")
        },
        dismissButton = {
            Button(onClick = {
                dialogState.value = false
                // Обработка закрытия диалога без предоставления разрешения
            }) {
                Text("Отмена")
            }
        }
    )
}

//@Composable
//fun InfoDialog(
//
//    dialogState: MutableState<Boolean>,
//    switchLocation:(String)->Unit,
//    currentCity:String
//){
//    AlertDialog(
//        title = {
//            Text(text = "Смена локации" )
//        },
//        text = {
//               Text(text = "При смене локации мы также и обновим данные о погоде")
//        },
//        onDismissRequest = {
//             dialogState.value =false
//        },
//        confirmButton = {
//            dialogState.value =false
//            switchLocation(currentCity)
//        },
//    )
//}
