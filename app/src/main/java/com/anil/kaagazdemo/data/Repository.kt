package com.anil.kaagazdemo.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repository @Inject constructor(
    localDataSource: LocalDataSource
) {
    val local = localDataSource
}