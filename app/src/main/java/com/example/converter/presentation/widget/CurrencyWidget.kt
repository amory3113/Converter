package com.example.converter.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import com.example.converter.R
import com.example.converter.data.presentation.UserPreferencesRepository
import com.example.converter.domain.repository.CurrencyRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CurrencyWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val repository = entryPoint.getCurrencyRepository()
        val prefsRepository = entryPoint.getUserPreferencesRepository()

        val baseCurrency = prefsRepository.multiBaseCurrencyFlow.first()
        val targetCurrencies = prefsRepository.multiTargetCurrenciesFlow.first().toList()

        val result = repository.getRates(baseCurrency)
        val ratesMap = result.getOrNull()?.rates

        provideContent {
            WidgetContent(baseCurrency, targetCurrencies, ratesMap)
        }
    }

    @Composable
    private fun WidgetContent(
        baseCurrency: String,
        targetCurrencies: List<String>,
        rates: Map<String, Double>?
    ) {
        val context = LocalContext.current

        val widgetBackgroundColor = Color(0xFF111318)
        val textColorPrimary = Color.White
        val textColorSecondary = Color(0xFFFFFFFF)

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(widgetBackgroundColor)
                .padding(12.dp)
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(getFlagDrawableId(baseCurrency)),
                    contentDescription = baseCurrency,
                    modifier = GlanceModifier.size(24.dp, 18.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(GlanceModifier.width(8.dp))

                Text(
                    text = "1 $baseCurrency",
                    style = TextStyle(
                        color = ColorProvider(day = textColorSecondary, night = textColorSecondary),
                        fontSize = 14.sp
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )

                Image(
                    provider = ImageProvider(R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier
                        .size(24.dp)
                        .padding(4.dp)
                        .clickable(actionRunCallback<RefreshActionCallback>()),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(GlanceModifier.height(10.dp))

            if (rates.isNullOrEmpty()) {
                Text(
                    text = context.getString(R.string.error_no_data),
                    style = TextStyle(color = ColorProvider(day = Color.Red, night = Color.Red))
                )
                return@Column
            }

            val baseRate = rates[baseCurrency] ?: 1.0

            LazyColumn(
                modifier = GlanceModifier.defaultWeight()
            ) {
                items(targetCurrencies) { targetCode ->
                    val targetRate = rates[targetCode] ?: 1.0
                    val result = (1.0 / baseRate) * targetRate
                    val formattedResult = String.format(java.util.Locale.US, "%.2f", result)

                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            provider = ImageProvider(getFlagDrawableId(targetCode)),
                            contentDescription = targetCode,
                            modifier = GlanceModifier.size(20.dp, 15.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(GlanceModifier.width(10.dp))
                        Text(
                            text = targetCode,
                            style = TextStyle(
                                color = ColorProvider(
                                    day = textColorPrimary,
                                    night = textColorPrimary
                                ),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(GlanceModifier.defaultWeight())

                        Text(
                            text = formattedResult,
                            style = TextStyle(
                                color = ColorProvider(
                                    day = textColorPrimary,
                                    night = textColorPrimary
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    class CurrencyWidgetReceiver : GlanceAppWidgetReceiver() {
        override val glanceAppWidget: GlanceAppWidget = CurrencyWidget()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetEntryPoint {
        fun getCurrencyRepository(): CurrencyRepository
        fun getUserPreferencesRepository(): UserPreferencesRepository
    }

    class RefreshActionCallback : ActionCallback {
        @OptIn(DelicateCoroutinesApi::class)
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            )
            val repository = entryPoint.getCurrencyRepository()
            val prefsRepository = entryPoint.getUserPreferencesRepository()
            val baseCurrency = prefsRepository.multiBaseCurrencyFlow.first()
            GlobalScope.launch {
                repository.getRates(baseCurrency)
                CurrencyWidget().updateAll(context)
            }
        }
    }
}