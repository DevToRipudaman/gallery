package com.example.openpicture.galleryPicker.model

import com.example.openpicture.galleryPicker.model.interactor.PdfInteractorImpl
import com.example.openpicture.galleryPicker.presenter.PdfPresenter

import com.example.openpicture.galleryPicker.view.PdfFragment
import com.example.openpicture.galleryPicker.view.PhotosFragment

class PdfPresenterImpl(var pdfFragment: PdfFragment): PdfPresenter {
    val interactor: PdfInteractorImpl = PdfInteractorImpl(this)
    override fun getPdfAlbums() {
        interactor.getPdfAlbums()
    }
}