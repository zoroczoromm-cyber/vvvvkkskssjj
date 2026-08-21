package com.example.model

data class SubscriptionPlan(
    val id: String, // "free", "pro", "ultra"
    val title: String,
    val subtitle: String,
    val priceText: String,
    val billingCycle: String,
    val badgeText: String,
    val colorHex: String,
    val isPopular: Boolean = false,
    val isUltra: Boolean = false,
    val monthlyTokens: String,
    val features: List<String>,
    val creditsAwarded: Int
)

object SubscriptionPlans {
    val Free = SubscriptionPlan(
        id = "free",
        title = "الخطة المجانية",
        subtitle = "لتجربة الذكاء الاصطناعي الأساسي",
        priceText = "$0",
        billingCycle = "مجاناً دائماً",
        badgeText = "FREE",
        colorHex = "#757575",
        isPopular = false,
        isUltra = false,
        monthlyTokens = "50,000 رمز شهرياً",
        creditsAwarded = 100,
        features = listOf(
            "محادثة ذكية بأسلوب المحترفين (Gemini Flash)",
            "100 رصيد يومي لتوليد الإجابات",
            "إملاء صوتي وتحويل الصوت لنص",
            "البحث المباشر في Google Search",
            "حفظ ومزامنة المحادثات محلياً"
        )
    )

    val Pro = SubscriptionPlan(
        id = "pro",
        title = "خطة المحترفين (Pro)",
        subtitle = "للمطورين وصناع المحتوى والباحثين",
        priceText = "$9.99",
        billingCycle = "شهرياً",
        badgeText = "PRO ⭐",
        colorHex = "#2196F3",
        isPopular = true,
        isUltra = false,
        monthlyTokens = "2,000,000 رمز شهرياً",
        creditsAwarded = 2500,
        features = listOf(
            "جميع مميزات الخطة المجانية",
            "سرعة استجابة فائقة بدون انتظار",
            "توليد وتعديل الصور بدقة عالية (Imagen 3)",
            "معاينة تفاعلية لتطبيقات الأندرويد المولدة (App Sandbox)",
            "تصدير واستخراج الأكواد البرمجية بصيغ متعددة",
            "أولوية الوصول لأحدث التحديثات"
        )
    )

    val Ultra = SubscriptionPlan(
        id = "ultra",
        title = "خطة الترا الذكية (Ultra VIP)",
        subtitle = "أقوى ذكاء اصطناعي بالعالم بلا حدود",
        priceText = "$19.99",
        billingCycle = "شهرياً",
        badgeText = "ULTRA VIP 👑",
        colorHex = "#E040FB",
        isPopular = false,
        isUltra = true,
        monthlyTokens = "رموز غير محدودة (Unlimited)",
        creditsAwarded = 20000,
        features = listOf(
            "جميع مميزات باقة Pro مع قوة مضاعفة",
            "استوديو صناعة وتوليد الفيديو بالذكاء الاصطناعي بدقة 4K",
            "أداة تعديل الصور الشاملة (Inpainting & Enhancer)",
            "بناء وتوليد تطبيقات متكاملة مع المعاينة الحية فوراً",
            "وصول مباشر لمحركات Gemini 3.5 Pro و Imagen 3",
            "دعم فني مخصص وشارة Ultra VIP الذهبية"
        )
    )

    val allPlans = listOf(Free, Pro, Ultra)

    fun getPlan(id: String): SubscriptionPlan {
        return allPlans.find { it.id.equals(id, ignoreCase = true) } ?: Free
    }
}
