package com.rohman.spbu.persistence.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.rohman.spbu.model.Template
import com.rohman.spbu.persistence.dao.TemplateDao

class TemplateRepository(private val templateDao: TemplateDao) {
    var template: LiveData<Template> = templateDao.getTemplate()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(template: Template){
        templateDao.update(template = template)
    }

}