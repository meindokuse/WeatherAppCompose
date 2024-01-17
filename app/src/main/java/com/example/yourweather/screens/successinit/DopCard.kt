package com.example.yourweather.screens.successinit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ExtraCard(
    info:String,descriptor: String,idFotoInt: Int
){

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ){
            Box(
                contentAlignment = Alignment.Center

            ) {
                Image(painter = painterResource(id = idFotoInt), contentDescription ="uf",
                    modifier = Modifier.size(50.dp).align(Alignment.CenterStart))
                Text(text = descriptor , style = TextStyle(Color.Gray, fontSize = 20.sp),
                    modifier = Modifier.padding(start = 80.dp))
            }
            Text(text = info , style = TextStyle(Color.White, fontSize = 20.sp) ,
                modifier = Modifier)
        }
}

