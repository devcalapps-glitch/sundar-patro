package com.sundarpatro.app

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray

/**
 * On-device festival and public-holiday reminders.
 *
 * The web page owns the calendar logic: while the app is open it hands over the upcoming
 * reminders (trigger time, title, body, already in the user's language) via [save].
 * This object only stores that list and keeps one alarm armed for the next due item.
 * Nothing leaves the device.
 */
object Reminders {
    const val ACTION_FIRE = "com.sundarpatro.app.REMINDER_DUE"
    private const val CHANNEL = "holiday_reminders"
    private const val PREFS = "reminders"
    private const val KEY_ON = "enabled"
    private const val KEY_LIST = "list"
    private const val KEY_SHOWN_UP_TO = "shown_up_to"
    private const val KEY_ASKED = "asked"
    /** A reminder missed while the phone was off is still shown if it is at most this late. */
    private const val LATE_GRACE_MS = 12 * 60 * 60 * 1000L

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(ctx: Context) = prefs(ctx).getBoolean(KEY_ON, false)

    fun canPost(ctx: Context): Boolean {
        val permitted = Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return permitted && NotificationManagerCompat.from(ctx).areNotificationsEnabled()
    }

    fun wasAsked(ctx: Context) = prefs(ctx).getBoolean(KEY_ASKED, false)
    fun markAsked(ctx: Context) = prefs(ctx).edit().putBoolean(KEY_ASKED, true).apply()

    fun setEnabled(ctx: Context, on: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_ON, on).apply()
        if (on) scheduleNext(ctx) else {
            prefs(ctx).edit().remove(KEY_LIST).apply()
            alarm(ctx).cancel(pending(ctx))
        }
    }

    /** Replaces the stored list with [json]: `[{"at":epochMs,"title":"…","body":"…"}]`. */
    fun save(ctx: Context, json: String) {
        if (!isEnabled(ctx)) return
        JSONArray(json) // reject malformed input before storing it
        prefs(ctx).edit().putString(KEY_LIST, json).apply()
        scheduleNext(ctx)
    }

    /** Posts every reminder that is due and not yet shown, then arms the alarm for the next one. */
    fun fire(ctx: Context) {
        if (!isEnabled(ctx)) return
        val now = System.currentTimeMillis()
        var shownUpTo = shownUpTo(ctx, now)
        if (canPost(ctx)) {
            ensureChannel(ctx)
            for (r in items(ctx)) {
                if (r.at <= shownUpTo || r.at > now + 60_000L) continue
                if (r.at >= now - LATE_GRACE_MS) post(ctx, r)
                shownUpTo = maxOf(shownUpTo, r.at)
            }
            prefs(ctx).edit().putLong(KEY_SHOWN_UP_TO, shownUpTo).apply()
        }
        scheduleNext(ctx)
    }

    /** Last reminder time already handled; reset if the clock was moved back past it, so nothing is skipped. */
    private fun shownUpTo(ctx: Context, now: Long): Long {
        val v = prefs(ctx).getLong(KEY_SHOWN_UP_TO, 0L)
        return if (v > now + 60_000L) now else v
    }

    private fun scheduleNext(ctx: Context) {
        val now = System.currentTimeMillis()
        val after = maxOf(now, shownUpTo(ctx, now))
        val next = items(ctx).filter { it.at > after }.minOfOrNull { it.at }
        val pi = pending(ctx)
        if (next == null) alarm(ctx).cancel(pi)
        // Inexact is fine for an evening-before reminder and needs no exact-alarm permission.
        else alarm(ctx).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
    }

    private data class Item(val at: Long, val title: String, val body: String)

    private fun items(ctx: Context): List<Item> {
        val raw = prefs(ctx).getString(KEY_LIST, null) ?: return emptyList()
        return try {
            val a = JSONArray(raw)
            (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                Item(o.getLong("at"), o.getString("title"), o.optString("body"))
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun post(ctx: Context, r: Item) {
        val open = PendingIntent.getActivity(
            ctx, 0,
            Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle(r.title)
            .setContentText(r.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(r.body))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()
        try {
            NotificationManagerCompat.from(ctx).notify((r.at / 60_000L).toInt(), n)
        } catch (_: SecurityException) { /* permission revoked between check and post */ }
    }

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val ch = NotificationChannel(CHANNEL, ctx.getString(R.string.reminder_channel), NotificationManager.IMPORTANCE_DEFAULT)
        ch.description = ctx.getString(R.string.reminder_channel_desc)
        ctx.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    private fun alarm(ctx: Context) = ctx.getSystemService(AlarmManager::class.java)

    private fun pending(ctx: Context) = PendingIntent.getBroadcast(
        ctx, 0, Intent(ctx, ReminderReceiver::class.java).setAction(ACTION_FIRE),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

/** Fires due reminders; also re-arms the alarm after a reboot, an app update or a clock change. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in HANDLED) Reminders.fire(context)
    }

    private companion object {
        val HANDLED = setOf(
            Reminders.ACTION_FIRE,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
