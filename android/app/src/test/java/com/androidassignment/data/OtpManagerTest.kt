package com.androidassignment.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class OtpManagerTest {
  @Test
  fun `generates per-email otp snapshot`() {
    val manager = OtpManager()
    val (otp, snap) = manager.generate("test@example.com", now = 1_000)
    assertEquals(6, otp.length)
    assertEquals(60, snap.secondsLeft)
    assertEquals(3, snap.attemptsLeft)
    assertEquals(5, snap.resendSecondsLeft)
  }

  @Test
  fun `expires after ttl`() {
    val manager = OtpManager(ttlMs = 60_000)
    manager.generate("test@example.com", now = 0)
    val snapBefore = manager.snapshot("test@example.com", now = 59_000)
    assertNotNull(snapBefore)
    val snapAfter = manager.snapshot("test@example.com", now = 61_000)
    assertNull(snapAfter)
  }

  @Test
  fun `decrements attempts and locks`() {
    val manager = OtpManager()
    manager.generate("test@example.com", now = 0)
    val r1 = manager.validate("test@example.com", "000000", now = 1_000)
    val r2 = manager.validate("test@example.com", "000000", now = 2_000)
    val r3 = manager.validate("test@example.com", "000000", now = 3_000)

    assertEquals(OtpValidateResult.Invalid::class, r1::class)
    assertEquals(OtpValidateResult.Invalid::class, r2::class)
    assertEquals(OtpValidateResult.Locked::class, r3::class)
  }

  @Test
  fun `lockout applies resend cooldown`() {
    val manager = OtpManager()
    manager.generate("test@example.com", now = 0)
    manager.validate("test@example.com", "000000", now = 1_000)
    manager.validate("test@example.com", "000000", now = 2_000)
    val r3 = manager.validate("test@example.com", "000000", now = 3_000)

    assertEquals(OtpValidateResult.Locked::class, r3::class)
    val locked = r3 as OtpValidateResult.Locked
    assertEquals(10, locked.snapshot.resendSecondsLeft)
  }
}
