package com.example.openpicture.galleryPicker.presenter

import com.example.openpicture.galleryPicker.model.interactor.VideosInteractorImpl
import com.example.openpicture.galleryPicker.view.VideosFragment

class VideosPresenterImpl(var videosFragment: VideosFragment): VideosPresenter {
    var interactor = VideosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}