package com.example.scalio.utils

/**
 * Model Wrapper for modelling
 */
interface BaseMapper<F, L> {

    fun mapToLocal(foreign: F): L

    fun mapToLocalList(foreignList: List<F>): List<L> {
        return foreignList.mapTo(mutableListOf(), ::mapToLocal)
    }
}
