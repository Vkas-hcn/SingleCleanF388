package com.mastery.leaves.trace.data

import com.mastery.leaves.trace.ami.AllDataTool
import com.mastery.leaves.trace.ami.ChongTool
import com.mastery.leaves.trace.ami.DataPgTool
import com.mastery.leaves.trace.ami.DataPgTool.RequestResult
import com.mastery.leaves.trace.core.AdminRequestManager
import com.mastery.leaves.trace.core.CanNextGo
import com.mastery.leaves.trace.dimoting.laqleis.InitDies

object RefAndUserData {
    fun boeforPostAdmin(){
        val initDies = InitDies()
        initDies.initPang(AllDataTool.refState)
        ChongTool.postInstallJson()
    }
    fun rAndData(){
        boeforPostAdmin()
        startPopAdmin()
    }



    fun startPopAdmin(){
        AdminRequestManager.startPopAdmin()
    }

}