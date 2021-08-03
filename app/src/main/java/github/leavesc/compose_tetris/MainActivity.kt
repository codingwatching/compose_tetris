package github.leavesc.compose_tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesc.compose_tetris.logic.Action
import github.leavesc.compose_tetris.logic.TetrisViewModel
import github.leavesc.compose_tetris.logic.combinedPlayListener
import github.leavesc.compose_tetris.logic.previewTetrisState
import github.leavesc.compose_tetris.ui.TetrisBody
import github.leavesc.compose_tetris.ui.TetrisButton
import github.leavesc.compose_tetris.ui.TetrisScreen
import github.leavesc.compose_tetris.ui.theme.ComposeTetrisTheme
import github.leavesc.compose_tetris.utils.StatusBarUtil

/**
 * @Author: leavesC
 * @Date: 2021/6/3 22:06
 * @Desc:
 * @Github：https://github.com/leavesC
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        setContent {
            val tetrisViewModel = viewModel<TetrisViewModel>()
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            DisposableEffect(key1 = Unit) {
                val observer = object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        tetrisViewModel.dispatch(Action.Resume)
                    }

                    override fun onPause(owner: LifecycleOwner) {
                        tetrisViewModel.dispatch(Action.Background)
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    lifecycle.removeObserver(observer)
                }
            }
            ComposeTetrisTheme {
                Surface {
                    val tetrisState by tetrisViewModel.tetrisStateLD.collectAsState()
                    TetrisBody(tetrisScreen = {
                        TetrisScreen(tetrisState = tetrisState)
                    }, tetrisButton = {
                        TetrisButton(
                            playListener = combinedPlayListener(
                                onStart = {
                                    tetrisViewModel.dispatch(Action.Start)
                                },
                                onPause = {
                                    tetrisViewModel.dispatch(Action.Pause)
                                },
                                onReset = {
                                    tetrisViewModel.dispatch(Action.Reset)
                                },
                                onTransformation = {
                                    tetrisViewModel.dispatch(Action.Transformation(it))
                                },
                                onSound = {
                                    tetrisViewModel.dispatch(Action.Sound)
                                },
                            )
                        )
                    })
                }
            }
            tetrisViewModel.dispatch(action = Action.Welcome)
        }
    }
}

@Composable
@Preview(widthDp = 420, heightDp = 760)
fun DefaultPreview() {
    TetrisBody(tetrisScreen = {
        TetrisScreen(tetrisState = previewTetrisState)
    }, tetrisButton = {
        TetrisButton()
    })
}