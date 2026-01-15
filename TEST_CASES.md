# TrustShield Test Senaryoları

Aşağıdaki senaryoları **Events** sayfasından girerek kuralların çalıştığını test edebilirsiniz.

## Senaryo 1: FORCE_2FA (2 Adımlı Doğrulama)
**Hedef Kural:** RR-01 (Priority: 1)
**Beklenen Aksiyon:** FORCE_2FA
**Giriş Verileri:**
- **User ID:** U1
- **Service:** BiP
- **Event Type:** LOGIN
- **Value:** 0
- **Meta:** `device=new, ip_risk=high`

---

## Senaryo 2: PAYMENT_REVIEW (Ödeme İnceleme)
**Hedef Kural:** RR-02 (Priority: 3)
**Beklenen Aksiyon:** PAYMENT_REVIEW
**Giriş Verileri:**
- **User ID:** U2
- **Service:** Paycell
- **Event Type:** PAYMENT
- **Value:** 1500  *(Kural: >= 1000)*
- **Meta:** *(Boş bırakılabilir)*

---

## Senaryo 3: TEMPORARY_BLOCK (Çoklu Kural Tetikleme & Öncelik)
Bu senaryoda aslında hem RR-02 (Tutar > 1000 olduğu için) hem de RR-03 (Count >= 2 olduğu için) tetiklenebilir.
Ancak **RR-03 (Priority: 2)**, **RR-02 (Priority: 3)**'den daha önemli olduğu için **TEMPORARY_BLOCK** seçilmelidir. RR-02 bastırılmalıdır.

**Hedef Kural:** RR-03 (Priority: 2)
**Beklenen Aksiyon:** TEMPORARY_BLOCK
**Bastırılan (Suppressed):** PAYMENT_REVIEW
**Giriş Verileri:**
- **User ID:** U3
- **Service:** Paycell
- **Event Type:** PAYMENT
- **Value:** 2000
- **Meta:** `merchant=GiftCards, payments_15min_count=5`

---

## Senaryo 4: OPEN_FRAUD_CASE (Otomatik Case Açma)
Bu senaryo veritabanında **Fraud Cases** tablosunda yeni bir kayıt oluşturmalıdır.
**Hedef Kural:** RR-04 (Priority: 2)
**Beklenen Aksiyon:** OPEN_FRAUD_CASE
**Giriş Verileri:**
- **User ID:** U4
- **Service:** Paycell
- **Event Type:** CHARGEBACK
- **Value:** 500
- **Meta:** *(Boş bırakılabilir)*

---

## Senaryo 5: ANOMALY_ALERT (Anomali Uyarısı)
**Hedef Kural:** RR-05 (Priority: 4)
**Beklenen Aksiyon:** ANOMALY_ALERT
**Giriş Verileri:**
- **User ID:** U5
- **Service:** Superonline
- **Event Type:** USAGE
- **Value:** 50  *(Kural: > 15)*
- **Meta:** `night_usage=true`
