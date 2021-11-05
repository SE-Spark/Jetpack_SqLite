package com.smunyeke.jetpacksqlite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smunyeke.jetpacksqlite.database.DatabaseHelper
import com.smunyeke.jetpacksqlite.database.TodoItem
import com.smunyeke.jetpacksqlite.ui.theme.JetpackSqLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackSqLiteTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    mainScreen()
                }
            }
        }
    }
}

@Composable
fun mainScreen() {
    val context = LocalContext.current
    val db = DatabaseHelper(context)

    val todos = db.getData()
    val insertAction = remember {
        mutableStateOf(false)
    }
    val showAddUpdateDialog = remember {
        mutableStateOf(false)
    }
    var item= TodoItem()
    var todo by remember { mutableStateOf(item.itemName) }

    Scaffold(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.onSurface),
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
                    .height(40.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Text(text = "SQLITE Jetpack CRUD",fontSize = 25.sp,color = Color.White)
            }
        },
        floatingActionButton = {
            fabButtonAdd(insertAction = insertAction,showAddUpdateDialog)
        }

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            if (showAddUpdateDialog.value) {
                AddUpdateTodo(todos = todo) {
                    if (insertAction.value) {
                        db.insertData(TodoItem(itemName = it))
                        showAddUpdateDialog.value = false
                    } else {
                        db.updateData(TodoItem(itemId = item.itemId,itemName = it))
                        item=TodoItem()
                        todo=item.itemName
                        showAddUpdateDialog.value = false
                    }
                }
            }
            todos.let {
                ContentView(
                    todos = db.getData(),
                    onTodoRemoved ={db.deleteData(it)},
                    onTodoUpdate = {
                        item = it
                        todo = it.itemName
                        insertAction.value=false
                        showAddUpdateDialog.value=true
                    }
                )

            }
        }

    }
}


@Composable
fun ContentView(todos: List<TodoItem>,onTodoRemoved: (TodoItem) -> Unit,onTodoUpdate:(TodoItem) -> Unit) {
    LazyColumn(){
        itemsIndexed(todos){_,todo->
            Row() {
                Text(text = todo.itemName, Modifier.fillMaxWidth(.6f),fontSize = 18.sp)
                TextButton(onClick = {
                    onTodoUpdate(todo)
                }
                ) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                }
                TextButton(onClick = { onTodoRemoved(todo)}) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                }

            }
        }

    }
}

@Composable
fun AddUpdateTodo(todos:String, onTodoAdded:(String)->Unit) {
    var todo by remember {
        mutableStateOf(todos)
    }
    var context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Text(text = "Add Todo", Modifier.align(Alignment.CenterHorizontally))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = todo,
            onValueChange = { todo = it },
            label = { Text(text = "Note") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Go
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    var newTodo = todo
                    if (newTodo.isNotEmpty()) {
                        onTodoAdded(newTodo)
                        todo = ""
                    } else {
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        )
        TextButton(
            onClick = {
                var newTodo = todo
                if (newTodo.isNotEmpty()) {
                    onTodoAdded(newTodo)
                } else {
                    todo = ""
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                }
            },
            Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row {
                Text(text = "Save")
            }
        }
    }

}

@Composable
fun fabButtonAdd(insertAction: MutableState<Boolean>, showAddUpdateDialog: MutableState<Boolean>) {
    Image(
        painter = painterResource(id = R.drawable.ic_add),
        contentDescription = null,
        modifier = Modifier.clickable {
            insertAction.value= true
            showAddUpdateDialog.value=true
        } ,
        alignment = Alignment.BottomEnd,
    )
}