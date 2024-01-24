package com.example.yourweather.screens.successinit

import android.location.Location
import android.opengl.Visibility
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yourweather.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationElement(
    city:String,
    drawerState: DrawerState,
    switchLocation:(String)->Unit,
    deleteLocation:(String)->Unit
){
    val visibility = remember {
        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()
    AnimatedVisibility(

        visible = visibility.value,
        enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End)
                + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                + shrinkHorizontally() + fadeOut(),)
    {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    switchLocation(city)
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
                    text = city,
                    style = TextStyle(color = Color.White, fontSize = 18.sp,),
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.width(50.dp))
            IconButton(onClick = {
                visibility.value = false
                deleteLocation(city)
            }) {
                Icon(painter = rememberVectorPainter(Icons.Default.Delete), contentDescription = "sync",
                    tint = Color.White)
            }
        }
    }

}