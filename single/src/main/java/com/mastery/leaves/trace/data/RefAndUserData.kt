package com.mastery.leaves.trace.data

import com.mastery.leaves.trace.ami.DataPgTool
import com.mastery.leaves.trace.ami.DataPgTool.RequestResult
import com.mastery.leaves.trace.core.CanNextGo
object RefAndUserData {
    fun rAndData(){
        DataPgTool.instance.postAdminData { result ->
            when (result) {
                is RequestResult.Success -> {
                }
                is RequestResult.Error -> {
                    CanNextGo.showLog("admin-error: ${result.message}")
                }
            }
        }
    }
}