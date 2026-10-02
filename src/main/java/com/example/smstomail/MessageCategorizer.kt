package com.example.smstomail

object MessageCategorizer {

    enum class Category(val displayName: String) {
        PROMOTIONAL("تبلیغی"),
        OTP("کد تایید"),
        IMPORTANT("اطلاعات مهم"),
        DEADLINE("ددلاین"),
        OTHERS("سایر")
    }

    fun categorizeMessage(sender: String, messageText: String): Category {
        val text = messageText.lowercase()
        val senderLower = sender.lowercase()

        // OTP Detection
        if (hasOTPPatterns(text)) {
            return Category.OTP
        }

        // Promotional Detection
        if (hasPromotionalPatterns(text, senderLower)) {
            return Category.PROMOTIONAL
        }

        // Deadline Detection
        if (hasDeadlinePatterns(text)) {
            return Category.DEADLINE
        }

        // Important Notification Detection
        if (hasImportantPatterns(text, senderLower)) {
            return Category.IMPORTANT
        }

        return Category.OTHERS
    }

    private fun hasOTPPatterns(text: String): Boolean {
        val otpKeywords = listOf(
            "کد", "code", "otp", "تایید", "verify", "verification",
            "رمز", "password", "pin", "token", "تأیید", "شماره تایید",
            "ورود", "login", "confirm"
        )

        // Check for OTP keywords
        for (keyword in otpKeywords) {
            if (text.contains(keyword)) {
                return true
            }
        }

        // Check for digit patterns (codes are usually 4-6 digits)
        if (text.contains(Regex("\\b\\d{4,6}\\b"))) {
            return true
        }

        return false
    }

    private fun hasPromotionalPatterns(text: String, sender: String): Boolean {
        val promoKeywords = listOf(
            "تخفیف", "discount", "offer", "sale", "فروش", "خرید",
            "محصول جدید", "new product", "لیک", "like", "follow", "فالو",
            "بازدید", "visit", "پیوند", "link", "درآمد", "earn",
            "ثبت نام", "register", "بنر", "banner", "آگهی", "advertisement"
        )

        for (keyword in promoKeywords) {
            if (text.contains(keyword)) {
                return true
            }
        }

        return false
    }

    private fun hasDeadlinePatterns(text: String): Boolean {
        val deadlineKeywords = listOf(
            "ددلاین", "deadline", "موعد", "due date", "سرانجام", "نهایت",
            "آخرین", "final", "انقضا", "expire", "آخرین فرصت",
            "آخر هفته", "end of week", "فوری", "urgent"
        )

        for (keyword in deadlineKeywords) {
            if (text.contains(keyword)) {
                return true
            }
        }

        return false
    }

    private fun hasImportantPatterns(text: String, sender: String): Boolean {
        val importantKeywords = listOf(
            "بانک", "bank", "حساب", "account", "تراکنش", "transaction",
            "انتقال", "transfer", "پرداخت", "payment", "موجودی",
            "balance", "سفارش", "order", "تایید سفارش", "پیامد", "alert",
            "هشدار", "warning", "مهم", "important", "فوری", "urgent",
            "تغییر", "change", "به روزرسانی", "update", "نیاز", "required",
            "اطلاعیه", "announcement", "خبر", "news"
        )

        for (keyword in importantKeywords) {
            if (text.contains(keyword)) {
                return true
            }
        }

        // Check if sender is a bank or important service
        val importantSenders = listOf(
            "بانک", "bank", "paypal", "google", "apple", "amazon",
            "instagram", "telegram", "whatsapp", "twitter"
        )

        for (sender in importantSenders) {
            if (sender.contains(sender)) {
                return true
            }
        }

        return false
    }
}
