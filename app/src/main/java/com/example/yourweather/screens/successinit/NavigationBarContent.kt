package com.example.yourweather.screens.successinit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.R
import com.example.yourweather.models.AppState
import com.example.yourweather.ui.theme.LightBlue
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.example.yourweather.models.AppAction
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationContent(
    screenState: AppState.SuccessInit,
    dialogState: MutableState<Boolean>,
    deleteLocation: (String) -> Unit,
    switchLocation: (String) -> Unit,
    drawerState: DrawerState
){
    val scope = rememberCoroutineScope()
    Column {
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .aspectRatio(16f / 11f)
                .background(Color.Transparent) // Цвет фона для избежания черного фона в прозрачных областях изображения
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Ваша погода", modifier = Modifier.padding(16.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace
            )
        )

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = LightBlue
            )
            Text(
                text = "Текущеее место",
                style = TextStyle(color = Color.White, fontSize = 20.sp,),
                modifier = Modifier.padding(end = 40.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = screenState.location,
                style = TextStyle(color = Color.White, fontSize = 18.sp,),
            )
        }
        Divider(
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.baseline_add_location_24),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Другие места",
                style = TextStyle(color = Color.White, fontSize = 20.sp,),
                modifier = Modifier.padding(end = 40.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .heightIn(max = 200.dp),
        ) {

            itemsIndexed(screenState.otherLocations) { _, item ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            switchLocation(item)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_location_24), contentDescription = "city_loc" )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = item,
                            style = TextStyle(color = Color.White, fontSize = 18.sp,),
                            modifier = Modifier
                        )
                    }
                    Spacer(modifier = Modifier.width(50.dp))
                    IconButton(onClick = {
                        deleteLocation(item)
                    }) {
                        Icon(painter = rememberVectorPainter(Icons.Default.Delete), contentDescription = "sync",
                            tint = Color.White)
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        ExtendedFloatingActionButton(
            text = { Text("Добавить город") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "") },
            onClick = {
                dialogState.value = true
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            containerColor = Color.DarkGray,
            contentColor = Color.White

        )
        var isSwitchOn by remember { mutableStateOf(false) }
        var voiceSupportState by remember { mutableStateOf(if (isSwitchOn) "вкл" else "выкл") }

        LaunchedEffect(isSwitchOn) {
            voiceSupportState = if (isSwitchOn) "вкл" else "выкл"
        }

        Divider(
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
        )
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxWidth(),
                checked = isSwitchOn,
                onCheckedChange = { isChecked ->
                    isSwitchOn = isChecked
                }
            )
            Text(
                text = "Голосовой помощник - $voiceSupportState",
                style = TextStyle(color = Color.White, fontSize = 18.sp,),
            )
        }
    }
}


