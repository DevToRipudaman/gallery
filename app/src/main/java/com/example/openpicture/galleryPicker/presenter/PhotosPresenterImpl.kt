package com.example.openpicture.galleryPicker.presenter

import com.example.openpicture.galleryPicker.model.interactor.PhotosInteractorImpl
import com.example.openpicture.galleryPicker.view.PhotosFragment

class PhotosPresenterImpl(var photosFragment: PhotosFragment): PhotosPresenter {
    val interactor: PhotosInteractorImpl = PhotosInteractorImpl(this)
    override fun getPhoneAlbums() {
        interactor.getPhoneAlbums()
    }
}