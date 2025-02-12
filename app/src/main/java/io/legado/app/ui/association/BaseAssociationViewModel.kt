package io.legado.app.ui.association

import android.app.Application
import io.legado.app.R
import io.legado.app.base.BaseViewModel
import io.legado.app.data.appDb
import io.legado.app.data.entities.HttpTTS
import io.legado.app.data.entities.TxtTocRule
import io.legado.app.exception.NoStackTraceException
import io.legado.app.help.config.ThemeConfig
import io.legado.app.utils.GSON
import io.legado.app.utils.fromJsonArray
import io.legado.app.utils.fromJsonObject
import io.legado.app.utils.isJsonArray

abstract class BaseAssociationViewModel(application: Application) : BaseViewModel(application) {


    fun importTextTocRule(json: String, finally: (title: String, msg: String) -> Unit) {
        execute {
            if (json.isJsonArray()) {
                GSON.fromJsonArray<TxtTocRule>(json).getOrThrow()?.let {
                    appDb.txtTocRuleDao.insert(*it.toTypedArray())
                } ?: throw NoStackTraceException("格式不对")
            } else {
                GSON.fromJsonObject<TxtTocRule>(json).getOrThrow()?.let {
                    appDb.txtTocRuleDao.insert(it)
                } ?: throw NoStackTraceException("格式不对")
            }
        }.onSuccess {
            finally.invoke(context.getString(R.string.success), "导入Txt规则成功")
        }.onError {
            finally.invoke(
                context.getString(R.string.error),
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }

    fun importHttpTTS(json: String, finally: (title: String, msg: String) -> Unit) {
        execute {
            if (json.isJsonArray()) {
                HttpTTS.fromJsonArray(json).getOrThrow().let {
                    appDb.httpTTSDao.insert(*it.toTypedArray())
                    return@execute it.size
                }
            } else {
                HttpTTS.fromJson(json).getOrThrow().let {
                    appDb.httpTTSDao.insert(it)
                    return@execute 1
                }
            }
        }.onSuccess {
            finally.invoke(context.getString(R.string.success), "导入${it}朗读引擎")
        }.onError {
            finally.invoke(
                context.getString(R.string.error),
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }

    fun importTheme(json: String, finally: (title: String, msg: String) -> Unit) {
        execute {
            if (json.isJsonArray()) {
                GSON.fromJsonArray<ThemeConfig.Config>(json).getOrThrow()?.forEach {
                    ThemeConfig.addConfig(it)
                } ?: throw NoStackTraceException("格式不对")
            } else {
                GSON.fromJsonObject<ThemeConfig.Config>(json).getOrThrow()?.let {
                    ThemeConfig.addConfig(it)
                } ?: throw NoStackTraceException("格式不对")
            }
        }.onSuccess {
            finally.invoke(context.getString(R.string.success), "导入主题成功")
        }.onError {
            finally.invoke(
                context.getString(R.string.error),
                it.localizedMessage ?: context.getString(R.string.unknown_error)
            )
        }
    }


}