package io.github.darriousliu.han1meviewer.ui.screen.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import io.github.darriousliu.han1meviewer.ui.component.HapticTextButton as TextButton
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.darriousliu.utils.SonnerToast
import io.github.darriousliu.han1meviewer.ui.component.ConfirmDialog
import io.github.darriousliu.han1meviewer.ui.component.appbar.HanimeScaffold
import io.github.darriousliu.han1meviewer.ui.screen.home.dailycheckin.CheckInDialog
import io.github.darriousliu.han1meviewer.ui.screen.home.dailycheckin.ContributionReportDialog
import io.github.darriousliu.han1meviewer.ui.screen.home.dailycheckin.DailyCheckInContent
import io.github.darriousliu.han1meviewer.ui.screen.home.dailycheckin.DailyCheckInEvent
import io.github.darriousliu.han1meviewer.ui.screen.home.dailycheckin.DailyCheckInUiState
import io.github.darriousliu.han1meviewer.util.rememberAddCalendarEvent
import io.github.darriousliu.han1meviewer.util.rememberReportWindowMode
import io.github.darriousliu.han1meviewer.ui.viewmodel.CheckInCalendarViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlin.time.Duration.Companion.milliseconds
import io.github.darriousliu.han1meviewer.generated.resources.Res
import io.github.darriousliu.han1meviewer.generated.resources.calendar_dialog_confirm
import io.github.darriousliu.han1meviewer.generated.resources.calendar_dialog_title
import io.github.darriousliu.han1meviewer.generated.resources.cancel
import io.github.darriousliu.han1meviewer.generated.resources.check_in_feature_name
import io.github.darriousliu.han1meviewer.generated.resources.checkin_report
import io.github.darriousliu.han1meviewer.generated.resources.forgot_confirm
import io.github.darriousliu.han1meviewer.generated.resources.forgot_dismiss
import io.github.darriousliu.han1meviewer.generated.resources.forgot_title
import io.github.darriousliu.han1meviewer.generated.resources.ic_event_note
import io.github.darriousliu.han1meviewer.generated.resources.suck_back_confirm
import io.github.darriousliu.han1meviewer.generated.resources.suck_back_dismiss
import io.github.darriousliu.han1meviewer.generated.resources.suck_back_title
import io.github.darriousliu.han1meviewer.generated.resources.calendar_dialog_message
import io.github.darriousliu.han1meviewer.generated.resources.forgot_message
import io.github.darriousliu.han1meviewer.generated.resources.suck_back_done
import io.github.darriousliu.han1meviewer.generated.resources.suck_back_message
import io.github.darriousliu.han1meviewer.generated.resources.date_pattern_month_day
import io.github.darriousliu.han1meviewer.util.currentYearMonth
import io.github.darriousliu.han1meviewer.util.monthsBetween
import io.github.darriousliu.han1meviewer.util.today
import io.github.darriousliu.han1meviewer.util.plusMonths
import kotlinx.datetime.number
import io.github.darriousliu.han1meviewer.util.formatPattern
import org.koin.compose.viewmodel.koinViewModel

/**
 * 打卡日历页面 Screen 层。
 *
 * 作为 V-S-C 架构的胶水层：订阅 ViewModel 状态生成 [DailyCheckInUiState]，
 * 将 [DailyCheckInEvent] 映射到 ViewModel 操作和导航。
 *
 * @param onBack 返回回调
 * @param viewModel 打卡日历 ViewModel
 */
@Composable
fun DailyCheckInScreen(
    onBack: () -> Unit,
    viewModel: CheckInCalendarViewModel = koinViewModel(),
) {
    var showReport by rememberSaveable { mutableStateOf(false) }
    var isReportFullscreen by rememberSaveable { mutableStateOf(false) }

    val setReportWindowMode = rememberReportWindowMode()
    LaunchedEffect(isReportFullscreen) {
        setReportWindowMode(isReportFullscreen)
    }

    DisposableEffect(Unit) {
        onDispose { setReportWindowMode(false) }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val yearRecords by viewModel.yearRecords.collectAsStateWithLifecycle()
    val yearStats by viewModel.yearStats.collectAsStateWithLifecycle()

    val today = remember { today() }

    var forgotDialogDate by remember { mutableStateOf<LocalDate?>(null) }
    var suckBackDialogDate by remember { mutableStateOf<LocalDate?>(null) }
    var calendarDialogDate by remember { mutableStateOf<LocalDate?>(null) }
    var checkInDialogDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEasterEgg by remember { mutableStateOf("") }
    var eggVisible by remember { mutableStateOf(false) }

    var reportSelectedYear by remember { mutableIntStateOf(today.year) }
    var reportViewMode by remember { mutableStateOf("year") }
    var reportSelectedMonth by remember { mutableIntStateOf(today.month.number) }

    val anchorMonth = remember { currentYearMonth() }
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage) { Int.MAX_VALUE }

    LaunchedEffect(uiState.currentMonth) {
        val monthsDiff = monthsBetween(anchorMonth, uiState.currentMonth)
        val targetPage = initialPage + monthsDiff
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                val pageMonth = anchorMonth.plusMonths(page - initialPage)
                if (pageMonth != uiState.currentMonth) {
                    if (pageMonth > uiState.currentMonth) viewModel.nextMonth()
                    else viewModel.previousMonth()
                }
            }
    }

    LaunchedEffect(showEasterEgg) {
        if (showEasterEgg.isNotEmpty()) {
            eggVisible = true
            kotlinx.coroutines.delay(1500.milliseconds)
            eggVisible = false
        }
    }

    val addCalendarEvent = rememberAddCalendarEvent()

    val handleEvent: (DailyCheckInEvent) -> Unit = { event ->
        when (event) {
            is DailyCheckInEvent.OnDateClick -> {
                when {
                    event.date > today -> {
                        calendarDialogDate = event.date
                    }

                    event.date < today && (uiState.records[event.date] ?: 0) == 0 -> {
                        forgotDialogDate = event.date
                    }

                    else -> {
                        checkInDialogDate = event.date
                    }
                }
            }

            is DailyCheckInEvent.OnDateLongClick -> {
                val count = uiState.records[event.date] ?: 0
                if (count > 0 && event.date < today) {
                    suckBackDialogDate = event.date
                } else if (count > 0) {
                    viewModel.clearCheckIn(event.date)
                }
            }

            DailyCheckInEvent.OnPreviousMonth -> viewModel.previousMonth()
            DailyCheckInEvent.OnNextMonth -> viewModel.nextMonth()
            DailyCheckInEvent.OnTodayCheckIn -> {
                checkInDialogDate = today
            }

            DailyCheckInEvent.OnTodayClear -> viewModel.clearCheckIn(today)
            DailyCheckInEvent.OnShowReport -> {
                showReport = true
            }
        }
    }

    val scrollBehavior = pinnedScrollBehavior(rememberTopAppBarState())
    HanimeScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        title = stringResource(Res.string.check_in_feature_name),
        onBack = onBack,
        scrollBehavior = scrollBehavior,
        actions = {
            TextButton(
                onClick = { showReport = true }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_event_note),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(Res.string.checkin_report))
            }
        },
    ) { innerPadding ->
        DailyCheckInContent(
            paddingValues = innerPadding,
            uiState = uiState,
            onEvent = handleEvent,
            showEasterEgg = showEasterEgg,
            eggVisible = eggVisible,
            pagerState = pagerState,
            anchorMonth = anchorMonth,
            initialPage = initialPage,
        )
    }

    ConfirmDialog(
        visible = forgotDialogDate != null,
        title = stringResource(Res.string.forgot_title),
        message = forgotDialogDate?.let {
            stringResource(
                Res.string.forgot_message,
                it.formatPattern(stringResource(Res.string.date_pattern_month_day))
            )
        } ?: "",
        confirmText = stringResource(Res.string.forgot_confirm),
        dismissText = stringResource(Res.string.forgot_dismiss),
        onConfirm = {
            forgotDialogDate?.let { checkInDialogDate = it }
            forgotDialogDate = null
        },
        onDismiss = { forgotDialogDate = null },
    )

    ConfirmDialog(
        visible = calendarDialogDate != null,
        title = stringResource(Res.string.calendar_dialog_title),
        message = calendarDialogDate?.let {
            stringResource(
                Res.string.calendar_dialog_message,
                it.formatPattern(stringResource(Res.string.date_pattern_month_day))
            )
        } ?: "",
        confirmText = stringResource(Res.string.calendar_dialog_confirm),
        dismissText = stringResource(Res.string.cancel),
        onConfirm = {
            calendarDialogDate?.let { addCalendarEvent(it) }
            calendarDialogDate = null
        },
        onDismiss = { calendarDialogDate = null },
    )

    ConfirmDialog(
        visible = suckBackDialogDate != null,
        title = stringResource(Res.string.suck_back_title),
        message = suckBackDialogDate?.let {
            stringResource(
                Res.string.suck_back_message,
                it.formatPattern(stringResource(Res.string.date_pattern_month_day)),
                uiState.records[it] ?: 0
            )
        } ?: "",
        confirmText = stringResource(Res.string.suck_back_confirm),
        dismissText = stringResource(Res.string.suck_back_dismiss),
        onConfirm = {
            suckBackDialogDate?.let {
                viewModel.clearCheckIn(it)
                SonnerToast.success(Res.string.suck_back_done)
            }
            suckBackDialogDate = null
        },
        onDismiss = { suckBackDialogDate = null },
    )

    checkInDialogDate?.let { date ->
        CheckInDialog(
            date = date,
            onLoadRecords = { d, cb -> viewModel.getRecordsByDate(d, cb) },
            onGetCountByDate = { d, cb -> viewModel.getCountByDate(d, cb) },
            onAddRecord = { d, time, type, feeling ->
                viewModel.addRecord(d, time, type, feeling)
            },
            onDeleteRecord = { record, onDone -> viewModel.deleteRecord(record, onDone) },
            onEasterEgg = { msg -> showEasterEgg = msg },
            onDismiss = { checkInDialogDate = null },
        )
    }

    if (showReport) {
        ContributionReportDialog(
            selectedYear = reportSelectedYear,
            viewMode = reportViewMode,
            selectedMonth = reportSelectedMonth,
            yearRecords = yearRecords,
            yearStats = yearStats,
            onYearChange = { reportSelectedYear = it },
            onViewModeChange = { reportViewMode = it },
            onMonthChange = { reportSelectedMonth = it },
            onDismiss = {
                showReport = false
                isReportFullscreen = false
            },
            isFullscreen = isReportFullscreen,
            onToggleFullscreen = { isReportFullscreen = !isReportFullscreen },
            onLoadYearRecords = { viewModel.loadYearRecords(it) },
        )
    }
}
