package io.dodo.nav3

import androidx.fragment.app.FragmentActivity

/**
 * The "before" host: a FragmentActivity whose layout is a single NavHostFragment. The Navigation 2
 * NavController inside it owns the entire back stack (home, the nested auth graph, the dialogs).
 */
class MainActivity : FragmentActivity(R.layout.activity_main)
